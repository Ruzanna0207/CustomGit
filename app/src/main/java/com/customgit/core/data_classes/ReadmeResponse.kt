package com.customgit.core.data_classes

import com.google.gson.annotations.SerializedName

data class ReadmeResponse(
    @SerializedName("html_url") val html: String,
    @SerializedName("download_url") val file: String,
    @SerializedName("content") val content: String
)
