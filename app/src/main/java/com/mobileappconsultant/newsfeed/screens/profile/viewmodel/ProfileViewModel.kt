package com.mobileappconsultant.newsfeed.screens.profile.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.screens.forgot_password.viewmodel.ForgotPasswordUIState
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetUserQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.ChangePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ProfileUiState {
    NORMAL, CHANGE_PASSWORD
}

class ProfileViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application
): ViewModel() {
    val user = mutableStateOf<GetUserQuery.Response?>(null)
    val uiState = mutableStateOf(ProfileUiState.NORMAL)
    val loading = mutableStateOf(false)

    val oldPassword = mutableStateOf("")
    val newPassword = mutableStateOf("")
    val newPasswordConfirm = mutableStateOf("")

    fun loadProfile(navController: NavController) {
        viewModelScope.launch {
            loading.value = true
            val profile = withContext(Dispatchers.IO) { sdk.getUser() }
            loading.value = false
            if (profile.error) {
                Toast.makeText(application, profile.errors!![0].message, Toast.LENGTH_SHORT).show()

                if (profile.errors!![0].message == "token expired" || profile.errors!![0].message == "invalid token") {
                    navController.navigate(NavDestinations.SignIn.route) {
                        popUpTo(navController.graph.startDestinationRoute ?: "") {
                            inclusive = true
                        }
                    }
                }
            } else {
                user.value = profile.data!!
            }
        }
    }

    fun changePassword(navController: NavController) {
        viewModelScope.launch {
            loading.value = true
            val response = withContext(Dispatchers.IO) { sdk.changePassword(ChangePassword(
                newPassword = newPassword.value,
                oldPassword = oldPassword.value
            )) }
            loading.value = false

            if (response.error) {
                Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()

                if (response.errors!![0].message == "token expired" || response.errors!![0].message == "invalid token") {
                    navController.navigate(NavDestinations.SignIn.route) {
                        popUpTo(navController.graph.startDestinationRoute ?: "") {
                            inclusive = true
                        }
                    }
                }
            } else {
                Toast.makeText(application, "Password changed successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}