package com.customgit.data.user

import com.customgit.core.data_classes.RemoteGithubUser
import com.customgit.core.data_classes.Repository
import com.customgit.data.auth.app_auth.AppAuth
import com.customgit.data.auth.app_auth.TokenStorage
import com.customgit.domain.api.GithubApiFactory
import com.customgit.domain.user.UserRepository
import net.openid.appauth.EndSessionRequest

var token = TokenStorage.accessToken ?: ""
var user = TokenStorage.username
private var actualGitUser = GithubApiFactory.create(user, "https://api.github.com/")

class UserRepositoryImpl : UserRepository {

    //фун-я для выхода
    override fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
        TokenStorage.idToken = null
        TokenStorage.username = ""
    }

    //получ-е инфы о пользователе
    override suspend fun getUserInformation(): RemoteGithubUser {
        updateUser()
        return actualGitUser.getCurrentUser(user, token)
    }

    //получ-е инфы о репозиториях
    override suspend fun getRepositories(): List<Repository> {
        updateUser()
        return actualGitUser.getUserRepositories(user, token)
    }

    //выход из системы с использованием Custom Tabs
    override fun getEndSessionRequest(): EndSessionRequest {
        return AppAuth.getEndSessionRequest()
    }

    //обеовл-е пользователя после логаута для нового входа
    override fun updateUser() {
        token = TokenStorage.accessToken ?: ""
        user = TokenStorage.username
    }
}