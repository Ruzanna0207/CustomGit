package com.customgit.data.auth.app_auth

//сохр-е токена при успешной авторизации
object TokenStorage {
    var accessToken: String? = null
    var refreshToken: String? = null
    var idToken: String? = null
    var username = ""
}