package com.mobileappconsultant.newsfeed.screens.forgot_password.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel: ViewModel() {
    val uiState = mutableStateOf(ForgotPasswordUIState.OTP_AUTHENTICATION)

    val email = mutableStateOf("")
    val emailError = mutableStateOf<String?>(null)

    fun onContinueClick() {
        if (uiState.value == ForgotPasswordUIState.NORMAL) {
            if (email.value.trim().isEmpty()) {
                emailError.value = "Please enter your email!"
            } else {
                emailError.value = null

                uiState.value = ForgotPasswordUIState.OTP_AUTHENTICATION
            }
        }
    }
}

enum class ForgotPasswordUIState {
    NORMAL,
    OTP_AUTHENTICATION,
}