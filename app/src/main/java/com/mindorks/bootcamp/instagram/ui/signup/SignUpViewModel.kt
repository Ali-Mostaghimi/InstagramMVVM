package com.mindorks.bootcamp.instagram.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.*
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {


    private val validationList: MutableLiveData<List<Validation>> = MutableLiveData()
    val lunchLogin: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    val userNameField: MutableLiveData<String> = MutableLiveData()
    val emailField: MutableLiveData<String> = MutableLiveData()
    val passwordField: MutableLiveData<String> = MutableLiveData()

    val signingUp: MutableLiveData<Boolean> = MutableLiveData()

    val userNameValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.USERNAME)
    val emailValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.EMAIL)
    val passwordValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.PASSWORD)


    private fun filterValidation(field: Validation.Field) =
        Transformations.map(validationList){
            it?.find { validation -> validation.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    override fun onCreate() {
    }

    fun onUserNameChange(userName: String) = userNameField.postValue(userName)

    fun onEmailChange(email: String) = emailField.postValue(email)

    fun onPasswordChange(password: String) = passwordField.postValue(password)

    fun onSignUp(){
        val email = emailField.value
        val password = passwordField.value
        val userName = userNameField.value

        val validations = Validator.validateSignUpFields(userName, email, password)
        validationList.postValue(validations)

        if (validations.isNotEmpty() && email != null && password != null && userName != null){
            val successValidation = validations.filter { validation -> validation.resource.status == Status.SUCCESS}
            if (successValidation.size == validations.size && checkInternetConnectionWithMessage()){
                signingUp.postValue(true)
                compositeDisposable.addAll(
                    userRepository.doUserSignUp(userName, email, password)
                        .subscribeOn(schedulerProvider.io())
                        .subscribe(
                            {
                                signingUp.postValue(false)
                                lunchLogin.postValue(Event(emptyMap()))
                            },
                            {
                                handleNetworkError(it)
                                signingUp.postValue(false)
                            }
                        )
                )
            }

        }
    }

    fun onClickLoginTv(event: Event<Map<String, String>>) = lunchLogin.postValue(event)
}