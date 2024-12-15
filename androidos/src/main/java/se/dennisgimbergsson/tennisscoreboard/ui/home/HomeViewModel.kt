package se.dennisgimbergsson.tennisscoreboard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import se.dennisgimbergsson.shared.utils.ReduxViewModel

class HomeViewModel : ReduxViewModel<String>(
    initialState = ""
) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}