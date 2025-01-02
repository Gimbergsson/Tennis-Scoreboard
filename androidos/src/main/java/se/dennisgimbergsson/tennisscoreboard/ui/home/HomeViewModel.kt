package se.dennisgimbergsson.tennisscoreboard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.tennisscoreboard.repositories.AppwriteDataSource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appwriteRepository: AppwriteDataSource
) : ReduxViewModel<String>(
    initialState = ""
) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    init {
        viewModelScope.launch {
            val documentList = appwriteRepository.getScoreboardData()
            documentList?.documents?.forEach {
                it.data.values.forEach { value ->
                    if (value is String) {
                        logAndroidMessage(message = value)
                    }
                }
            }
        }
    }
}