package com.mindorks.bootcamp.instagram.utils.common

import com.mindorks.bootcamp.instagram.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.hasSize
import org.junit.Test

class ValidatorTest {

    @Test
    fun givenValidEmailAndValidPwd_whenValidate_shouldReturnSuccess(){
        val email = "test@gmail.com"
        val password = "password"
        val validations = Validator.validateLoginFields(email, password)
        assertThat(validations, hasSize(2))
        assertThat(
            validations,
            contains(
                Validation(Validation.Field.EMAIL, Resource.success()),
                Validation(Validation.Field.PASSWORD, Resource.success())
            )
        )
    }

    @Test
    fun givenInvalidEmailAndValidPwd_whenValidate_shouldReturnEmailError(){
        val email = "test"
        val password = "password"
        val validations = Validator.validateLoginFields(email, password)
        assertThat(validations, hasSize(2))
        assertThat(
            validations,
            contains(
                Validation(Validation.Field.EMAIL, Resource.error(R.string.login_email_field_invalid)),
                Validation(Validation.Field.PASSWORD, Resource.success())
            )
        )
    }

    @Test
    fun givenValidEmailAndInvalidPwd_whenValidate_shouldReturnPwdError(){
        val email = "test@gmail.com"
        val password = "pwd"
        val validations = Validator.validateLoginFields(email, password)
        assertThat(validations, hasSize(2))
        assertThat(
            validations,
            contains(
                Validation(Validation.Field.EMAIL, Resource.success()),
                Validation(Validation.Field.PASSWORD, Resource.error(R.string.login_password_field_small_length))
            )
        )
    }
}