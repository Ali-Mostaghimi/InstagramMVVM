package com.mindorks.bootcamp.instagram.ui.profile.editProfile

import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.MyInfo
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.MyInfoRepository
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.FileUtils
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.InputStream

class EditProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val myInfoRepository: MyInfoRepository,
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
    private val directory: File
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val user: User = userRepository.getCurrentUser()!!
    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )

    val myInfo: MutableLiveData<MyInfo> = MutableLiveData()
    val updatedMyInfo: MutableLiveData<MyInfo> = MutableLiveData()
    val nameField: MutableLiveData<String> = MutableLiveData()
    val taglineField: MutableLiveData<String> = MutableLiveData()

    val imageProfile: MutableLiveData<Image> = MutableLiveData()
    val loadingProfile: MutableLiveData<Boolean> = MutableLiveData()

    val email: MutableLiveData<String> = MutableLiveData()

    val loadingPage: MutableLiveData<Boolean> = MutableLiveData()
    val showContent: LiveData<Boolean> = Transformations.map(loadingPage) { !it }
    val updatingMyInfo: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadingPage.postValue(true)
        email.postValue(user.email)
        compositeDisposable.add(
            myInfoRepository.doFetchMyInfo(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        myInfo.postValue(it)
                        loadingPage.postValue(false)
                    },
                    {
                        handleNetworkError(it)
                        loadingPage.postValue(false)
                    }
                )
        )
    }

    override fun onCreate() {

    }

    fun onGalleryImageSelected(inputStream: InputStream) {
        loadingProfile.postValue(true)
        compositeDisposable.add(
            Single.fromCallable {
                FileUtils.saveInputStreamToFile(
                    inputStream, directory, "temp_profile", 500
                )
            }
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        if (it != null) {
                            uploadPhoto(it)
                        } else {
                            loadingProfile.postValue(false)
                            messageStringId.postValue(Resource.error(R.string.try_again))
                        }
                    },
                    {
                        loadingProfile.postValue(false)
                        messageStringId.postValue(Resource.error(R.string.try_again))
                    }
                )
        )

    }

    fun onCameraImageTaken(cameraImageProcessor: () -> String) {
        loadingProfile.postValue(true)
        compositeDisposable.add(
            Single.fromCallable { cameraImageProcessor() }
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        val file = File(it)
                        if (file.exists()) uploadPhoto(file) else loadingProfile.postValue(false)
                    },
                    {
                        loadingProfile.postValue(false)
                        messageStringId.postValue(Resource.error(R.string.try_again))
                    }
                )
        )
    }

    private fun uploadPhoto(file: File) {
        compositeDisposable.add(
            photoRepository.uploadPhoto(file, user)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        imageProfile.postValue(Image(it, headers))
                        loadingProfile.postValue(false)
                    },
                    {
                        handleNetworkError(it)
                        loadingProfile.postValue(false)
                    }
                )
        )
    }

    fun updateMyInfo() {
        updatingMyInfo.postValue(true)
        if (!nameField.value.isNullOrBlank()) {
            compositeDisposable.add(
                myInfoRepository.updateMyInfo(
                    nameField.value.orEmpty(),
                    imageProfile.value?.url,
                    taglineField.value,
                    user
                )
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            updatedMyInfo.postValue(it)
                            userRepository.saveCurrentUser(
                                User(
                                    user.id,
                                    it.name,
                                    user.email,
                                    user.accessToken,
                                    user.profilePicUrl
                                )
                            )
                            updatingMyInfo.postValue(false)
                        },
                        {
                            handleNetworkError(it)
                            updatingMyInfo.postValue(false)
                        }
                    )
            )
        } else {
            updatingMyInfo.postValue(false)
            messageStringId.postValue(Resource.error(R.string.editProfile_name_empty_error))
        }
    }

    fun onNameChange(name: String) {
        nameField.postValue(name)
    }

    fun onTaglineChange(tagline: String) {
        taglineField.postValue(tagline)
    }

    fun onFetchedMyInfo(myInfo: MyInfo) {
        nameField.postValue(myInfo.name)
        imageProfile.postValue(Image(myInfo.profilePicUrl.orEmpty(), headers))
        taglineField.postValue(myInfo.tagline)
    }
}