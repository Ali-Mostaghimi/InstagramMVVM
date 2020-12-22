package com.mindorks.bootcamp.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.Status
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : BaseActivity<SignUpViewModel>() {
    override fun provideLayoutId(): Int = R.layout.activity_signup

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        signUp_et_fullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onUserNameChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        signUp_et_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEmailChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        signUp_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPasswordChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        signUp_btn_signUp.setOnClickListener { viewModel.onSignUp() }
        signUp_tv_loginWithEmail.setOnClickListener { viewModel.onClickLoginTv(Event(emptyMap())) }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.lunchLogin.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        })

        viewModel.userNameField.observe(this, Observer {
            if (signUp_et_fullName.text.toString() != it) signUp_et_fullName.setText(it)
        })
        viewModel.userNameValidation.observe(this, Observer {
            when(it.status){
                Status.ERROR ->
                    signUp_layout_fullName.error = it.data?.run { getString(this) }
                else ->
                    signUp_layout_fullName.isErrorEnabled = false
            }
        })

        viewModel.emailField.observe(this, Observer {
            if (signUp_et_email.text.toString() != it) signUp_et_email.setText(it)
        })
        viewModel.emailValidation.observe(this, Observer {
            when(it.status){
                Status.ERROR ->
                    signUp_layout_email.error = it.data?.run { getString(this) }
                else ->
                    signUp_layout_email.isErrorEnabled = false
            }
        })

        viewModel.passwordField.observe(this, Observer {
            if(signUp_et_password.text.toString() != it) signUp_et_password.setText(it)
        })
        viewModel.passwordValidation.observe(this, Observer {
            when(it.status){
                Status.ERROR ->
                    signUp_layout_password.error = it.data?.run { getString(this) }
                else ->
                    signUp_layout_password.isErrorEnabled = false
            }
        })

        viewModel.signingUp.observe(this, Observer {
            signUp_pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

    }

}