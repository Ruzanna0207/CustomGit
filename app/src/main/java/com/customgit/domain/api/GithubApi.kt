package com.customgit.domain.api

import com.customgit.core.data_classes.RemoteGithubUser
import com.customgit.core.data_classes.Repository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GithubApi {
    @GET("/users/{username}")
    suspend fun getCurrentUser(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): RemoteGithubUser

    @GET("/users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): List<Repository>
}

// Фабрика для созд-я экземпляров GithubApi с разными настройками
object GithubApiFactory {
    fun create(username: String, baseUrl: String): GithubApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(GithubApi::class.java)
    }
}
