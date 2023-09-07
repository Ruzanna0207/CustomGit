package com.customgit.viewModels.user_info

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.customgit.core.data_classes.RemoteGithubUser
import com.customgit.core.data_classes.Repository
import com.customgit.data.user.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationService
import retrofit2.HttpException

class UserInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val userRepository = UserRepositoryImpl()
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)

    val logoutPageFlow: Flow<Intent> = logoutPageEventChannel.receiveAsFlow()
    val logoutCompletedFlow: Flow<Unit> = logoutCompletedEventChannel.receiveAsFlow()

    //репозитории
    private var _currentRepos = MutableLiveData<List<Repository>>()
    val currentRepos: LiveData<List<Repository>> = _currentRepos

    //инф-я о пользователе
    private var _currentUser = MutableLiveData<RemoteGithubUser>()
    val currentUser: LiveData<RemoteGithubUser> = _currentUser

    //ссобщение об ошибке для пользователя
    var errorUser = MutableLiveData<String>()
//--------------------------------------------------------------------------------------------------

    //инф-я о пользователе
    fun getUser() {
        errorUser.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userRepository.getUserInformation()

                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                    Log.i("Oauth", currentUser.value.toString())
                }

            } catch (e: Exception) {
                // Обработка ошибок
                if (e is HttpException && e.code() == 404) {
                    val errorMessage = "Пользователь не найден."
                    Log.e("Oauth", errorMessage)

                    withContext(Dispatchers.Main) {
                        errorUser.value = errorMessage
                    }
                } else {
                    val errorMessage = "Ошибка при получении информации о пользователе:${e.message}"
                    Log.e("Oauth", errorMessage)

                    withContext(Dispatchers.Main) {
                        errorUser.value = errorMessage
                    }
                }
            }
        }
    }

    //    //инф-я о репозиториях
    fun getAllRepos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val repos = userRepository.getRepositories()

                withContext(Dispatchers.Main) {
                    _currentRepos.value = repos
                    Log.i("Oauth", currentRepos.value.toString())
                }

            } catch (e: Exception) {
                // Обработка ошибок
                if (e is HttpException && e.code() == 404) {
                    val errorMessage = "Репозитории не найдены."
                    Log.e("Oauth", errorMessage)

                } else {
                    val errorMessage = "Ошибка при получении репозиториев: ${e.message}"
                    Log.e("Oauth", errorMessage)
                }
            }
        }
    }

    //отмена авторизации
    fun logout() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val logoutPageIntent = authService.getEndSessionRequestIntent(
            userRepository.getEndSessionRequest(),
            customTabsIntent
        )
        logoutPageEventChannel.trySendBlocking(logoutPageIntent)
    }

    //отмена авторизации выполнена
    fun webLogoutComplete() {
        userRepository.logout()
        logoutCompletedEventChannel.trySendBlocking(Unit)
    }

    //освобождение ресурсов
    override fun onCleared() {
        super.onCleared()
        authService.dispose()

    }
}
