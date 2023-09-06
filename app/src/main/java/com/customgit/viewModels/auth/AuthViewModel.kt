package com.customgit.viewModels.auth

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.customgit.R
import com.customgit.data.auth.AuthRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val authService: AuthorizationService =
        AuthorizationService(getApplication()) //сервис для опер-й с flow в appAuth
    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val loadingMutableStateFlow = MutableStateFlow(false)

    val openAuthPageFlow: Flow<Intent> = openAuthPageEventChannel.receiveAsFlow()
    val loadingFlow: Flow<Boolean> = loadingMutableStateFlow.asStateFlow()
    val toastFlow: Flow<Int> = toastEventChannel.receiveAsFlow()
    val authSuccessFlow: Flow<Unit> = authSuccessEventChannel.receiveAsFlow()

//--------------------------------------------------------------------------------------------------

    //обработка исключений
    fun onAuthCodeFailed(exception: AuthorizationException) {
        toastEventChannel.trySendBlocking(R.string.auth_canceled)
    }

    //фун-я для авторизации
    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        Log.i("Oauth", "3. Received code = ${tokenRequest.authorizationCode}")

        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            runCatching {
                Log.i("Oauth", "4. Change code to token. Url = ${tokenRequest.configuration.tokenEndpoint}, verifier = ${tokenRequest.codeVerifier}")

                //заверш-е обмена кода на токен
                authRepository.performTokenRequest(
                    authService = authService, tokenRequest = tokenRequest
                )
            }.onSuccess {
                loadingMutableStateFlow.value = false
                authSuccessEventChannel.send(Unit) //отправляется событие об успешной аутентификации
            }.onFailure {
                loadingMutableStateFlow.value = false
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }

    //открытие кастом.таб для авторизации
    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val authRequest = authRepository.getAuthRequest() //запрос для авторизации

        Log.i(
            "Oauth",
            "1. Generated verifier=${authRequest.codeVerifier},challenge=${authRequest.codeVerifierChallenge}"
        )
        //codeVerifier и codeVerifierChallenge генерятся автом. библ-ой

        //общий интент для обраб-и customTabsIntent и authRequest
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest, customTabsIntent
        )

        //откр-е интента во фрагм-те
        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
        Log.i("Oauth", "2. Open auth page: ${authRequest.toUri()}")
    }

    //освобождение ресурсов
    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}
