package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.MyInfo
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import io.reactivex.Single
import javax.inject.Inject


class MyInfoRepository @Inject constructor(
    private val networkService: NetworkService
) {
    fun updateMyInfo(
        name: String,
        profilePicUrl: String?,
        tagline: String?,
        user: User
    ): Single<MyInfo> =
        networkService.doUpdateMyInfoCall(
            MyInfo(name, profilePicUrl, tagline),
            user.id,
            user.accessToken
        ).map {
            MyInfo(name, profilePicUrl, tagline)
        }

    fun doFetchMyInfo(user: User): Single<MyInfo> =
        networkService.doMyInfoCall(user.id, user.accessToken)
            .map {
                MyInfo(
                    it.data.name,
                    it.data.profilePicUrl,
                    it.data.tagline
                )
            }

}