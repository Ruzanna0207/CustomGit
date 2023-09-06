package com.customgit.domain.user

import com.customgit.core.data_classes.RemoteGithubUser
import com.customgit.core.data_classes.Repository
import net.openid.appauth.EndSessionRequest

//описание use cases
interface UserRepository {

    //получ-е инфы о пользователе
    suspend fun getUserInformation(): RemoteGithubUser

    //получ-е репозиториев
    suspend fun getRepositories():  List<Repository>

    //функция для логаута
    fun logout()

    //функция для логаута
    fun getEndSessionRequest(): EndSessionRequest

    //обновл-е инфы о пользователе после логаута
    fun updateUser()
}
