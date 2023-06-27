package com.mobileappconsultant.newsfeed.screens.verify_user

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.NewsFeedApplication
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.VerifyOtp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerifyUserViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {
    val verificationCode = mutableStateOf("")
    val loading = mutableStateOf(false)
    val showSuccess = mutableStateOf(false)

    fun doVerify() {
        if (verificationCode.value.isNotEmpty()) {
            showSuccess.value = false

            viewModelScope.launch(Dispatchers.IO) {
                loading.value = true
                val response = sdk.verifyEmail(VerifyOtp(verificationCode.value))
                loading.value = false

                if (!response.error) {
                    showSuccess.value = true
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            application,
                            response.errors!![0].message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun onSuccessClick(navController: NavController) {
        Toast.makeText(application, "Please sign in again", Toast.LENGTH_SHORT).show()
        navController.navigate(NavDestinations.SignIn.route) {
            popUpTo(NavDestinations.VerifyUser.route) {
                inclusive = true
            }
        }
    }
}