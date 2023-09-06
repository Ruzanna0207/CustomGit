package com.customgit.domain.auth

import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

//описание use cases
interface AuthRepository {

    //фун-я для входа
    fun getAuthRequest(): AuthorizationRequest

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    )
}