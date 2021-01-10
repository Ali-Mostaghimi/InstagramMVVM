package com.mindorks.bootcamp.instagram.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostCreationRequest(
    @Expose
    @SerializedName("imgUrl")
    val imgUrl: String,

    @Expose
    @SerializedName("imgWidth")
    val imgWidth: Int,

    @Expose
    @SerializedName("imgHeight")
    val imgHeight: Int
    )