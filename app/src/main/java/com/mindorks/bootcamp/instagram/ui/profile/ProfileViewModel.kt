package com.mindorks.bootcamp.instagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.MyInfo
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.MyInfoRepository
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val myInfoRepository: MyInfoRepository
): BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val user: User = userRepository.getCurrentUser()!!
    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val myInfo: MutableLiveData<MyInfo> = MutableLiveData()

    val loggedOut: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val lunchEditProfile: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    val name: LiveData<String> = Transformations.map(myInfo){ it.name }
    val tagline: LiveData<String> = Transformations.map(myInfo){ it.tagline}
    val profileImage: LiveData<Image> = Transformations.map(myInfo){ Image(it.profilePicUrl, headers) }

    //val postCount: MutableLiveData<Int> = MutableLiveData()

    init {
        if (checkInternetConnectionWithMessage()) {
            loading.postValue(true)
            compositeDisposable.add(
                myInfoRepository.doFetchMyInfo(user)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            myInfo.postValue(it)
                            loading.postValue(false)
                        },
                        {
                            loading.postValue(false)
                            handleNetworkError(it)
                        }
                    )
            )
        }
    }

    override fun onCreate() {
    }

    fun onLogout() {
        if (checkInternetConnectionWithMessage()) {
            loading.postValue(true)
            compositeDisposable.add(
                userRepository.doLogout(user)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            userRepository.removeCurrentUser()
                            loading.postValue(false)
                            loggedOut.postValue(Resource.success(true))
                        },
                        {
                            loading.postValue(false)
                            handleNetworkError(it)
                        }
                    )
            )
        }
    }

    fun onEditProfile(){
        lunchEditProfile.postValue(Event(emptyMap()))
    }
}