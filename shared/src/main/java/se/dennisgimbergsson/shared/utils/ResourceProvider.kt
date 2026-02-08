package se.dennisgimbergsson.shared.utils

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import se.dennisgimbergsson.shared.extensions.getLocalizedResources
import javax.inject.Inject

interface ResourceProviderSource {
    fun getString(@StringRes stringResId: Int): String

    fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String

    fun getPlural(@PluralsRes pluralResId: Int, quantity: Int): String
}

class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : ResourceProviderSource {

    override fun getString(@StringRes stringResId: Int): String =
        context.getLocalizedResources().getString(stringResId)

    override fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String =
        context.getLocalizedResources().getString(stringResId, *formatArgs)

    override fun getPlural(@PluralsRes pluralResId: Int, quantity: Int): String =
        context.getLocalizedResources().getQuantityString(pluralResId, quantity, quantity)
}