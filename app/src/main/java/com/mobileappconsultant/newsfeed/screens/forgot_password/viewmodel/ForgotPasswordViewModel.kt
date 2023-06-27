package com.mobileappconsultant.newsfeed.screens.forgot_password.viewmodel

import android.app.Application
import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.NewsHolder
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.ForgotPassword
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.ResetPassword
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.VerifyOtp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {
    val uiState = mutableStateOf(ForgotPasswordUIState.NORMAL)

    val email = mutableStateOf("")
    val otp = mutableStateOf("")
    val emailError = mutableStateOf<String?>(null)
    val remainingTime = mutableStateOf("")
    val showResendCode = mutableStateOf(false)

    private val countDownTimer = object: CountDownTimer(1000 * 60, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val remaining = millisUntilFinished / 1000
            remainingTime.value = "$remaining"
        }

        override fun onFinish() {
            showResendCode.value = true
        }

    }

    fun onContinueClick(navController: NavController) {
        if (uiState.value == ForgotPasswordUIState.NORMAL) {
            if (email.value.trim().isEmpty()) {
                emailError.value = application.getString(R.string.please_enter_your_email)
            } else {
                emailError.value = null

                viewModelScope.launch {
                    val previousUIState = uiState.value
                    uiState.value = ForgotPasswordUIState.LOADING

                    val response = withContext(Dispatchers.IO) { sdk.forgotPassword(ForgotPassword(
                        email = email.value,
                    )) }

                    if (response.error) {
                        Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()
                        uiState.value = previousUIState
                    } else {
                        countDownTimer.cancel()
                        countDownTimer.start()
                        uiState.value = ForgotPasswordUIState.OTP_AUTHENTICATION
                    }
                }
            }
        } else if (uiState.value == ForgotPasswordUIState.OTP_AUTHENTICATION) {
            if (otp.value.isEmpty()) {
                Toast.makeText(application, application.getString(R.string.please_enter_a_value_for_the_otp), Toast.LENGTH_SHORT).show()
            } else {
                uiState.value = ForgotPasswordUIState.LOADING
                viewModelScope.launch {
                    val response = withContext(Dispatchers.IO) { sdk.verifyResetOtp(VerifyOtp(otp.value)) }

                    uiState.value = ForgotPasswordUIState.NORMAL

                    if (response.error) {
                        Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()
                    } else {
                        NewsHolder.email = email.value
                        navController.navigate(NavDestinations.ResetPassword.route)
                    }
                }
            }
        }
    }

    fun resendCode() {
        viewModelScope.launch {
            val previousUIState = uiState.value
            uiState.value = ForgotPasswordUIState.LOADING

            val response = withContext(Dispatchers.IO) { sdk.forgotPassword(ForgotPassword(
                email = email.value,
            )) }

            if (response.error) {
                Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()

            }

            uiState.value = previousUIState

            showResendCode.value = false
            countDownTimer.cancel()
            countDownTimer.start()
        }
    }
}

enum class ForgotPasswordUIState {
    NORMAL,
    LOADING,
    OTP_AUTHENTICATION,
}