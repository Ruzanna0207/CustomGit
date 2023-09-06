package com.customgit.data.auth

import android.util.Log
import com.customgit.data.auth.app_auth.AppAuth
import com.customgit.data.auth.app_auth.TokenStorage
import com.customgit.domain.auth.AuthRepository
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

class AuthRepositoryImpl : AuthRepository {

    //фун-я для входа
    override fun getAuthRequest(): AuthorizationRequest {
        return AppAuth.getAuthRequest()
    }

    //получение токена
    override suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) {
        val tokens = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
        //обмен кода на токен успешен, сохр. токен и завершаем авторизацию
        TokenStorage.accessToken = tokens.accessToken
        TokenStorage.refreshToken = tokens.refreshToken
        TokenStorage.idToken = tokens.idToken

        Log.i("Oauth", "Tokens accepted:\n access=${tokens.accessToken}\nrefresh=${tokens.refreshToken}\nNAME=${TokenStorage.username}")
    }
}
