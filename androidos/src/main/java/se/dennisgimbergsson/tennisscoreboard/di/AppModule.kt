package se.dennisgimbergsson.tennisscoreboard.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.shared.utils.DefaultDispatcherProvider
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeveloperPreferences

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun gson(): Gson = GsonBuilder().create()

    @Provides
    fun providesDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Singleton
    @Provides
    fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    @DeveloperPreferences
    fun developerPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            Constants.Suffixes.DEVELOPER_SETTINGS_SUFFIX,
            Context.MODE_PRIVATE
        )
}