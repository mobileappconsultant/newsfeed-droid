package com.mobileappconsultant.newsfeed.screens.reset_password.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.ResetPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPasswordViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {
    val uiState = mutableStateOf(UIState.NORMAL)
    val newPassword = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    val showSuccess = mutableStateOf(false)

    fun resetPassword(email: String) {

        if (newPassword.value.isEmpty() || confirmPassword.value.isEmpty()) {
            Toast.makeText(application, application.getString(R.string.please_enter_the_passwords), Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.value != confirmPassword.value) {
            Toast.makeText(application, application.getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            uiState.value = UIState.LOADING
            val response = withContext(Dispatchers.IO) { sdk.resetPassword(ResetPassword(email = email, newPassword.value)) }
            uiState.value = UIState.NORMAL

            if (response.error) {
                Toast.makeText(application, application.getString(R.string.please_enter_the_passwords), Toast.LENGTH_SHORT).show()
            } else {
                showSuccess.value = true
            }
        }
    }

}