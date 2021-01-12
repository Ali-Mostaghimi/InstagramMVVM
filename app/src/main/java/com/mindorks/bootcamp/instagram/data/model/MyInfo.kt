package com.mindorks.bootcamp.instagram.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyInfo(
    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("profilePicUrl")
    val profilePicUrl: String,

    @Expose
    @SerializedName("tagline")
    val tagline: String
)