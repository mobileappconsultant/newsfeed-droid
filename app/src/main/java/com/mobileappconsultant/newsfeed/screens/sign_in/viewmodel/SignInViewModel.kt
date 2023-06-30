package com.mobileappconsultant.newsfeed.screens.sign_in.viewmodel

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.GoogleAuth
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.Login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {
    private val TAG = "SignInViewModel"

    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val loading = mutableStateOf(false)
    val rememberSelected = mutableStateOf(false)

    fun doSignIn(navController: NavController) {
        if (username.value.isNotEmpty() && password.value.isNotEmpty()) {
            val payload = Login(username.value, password.value)

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    loading.value = true
                    val response = sdk.login(payload)
                    loading.value = false

                    withContext(Dispatchers.Main) {
                        if (!response.error) {
                            val user = withContext(Dispatchers.IO) { sdk.getUser() }
                            if (!user.error) {
                                user.data?.let { user ->
                                    val sharedPreferences =
                                        PreferenceManager.getDefaultSharedPreferences(application)
                                    sharedPreferences.edit()
                                        .putBoolean("signedIn", true)
                                        .apply()

                                    if (!user.is_verified.orFalse()) {
                                        navController.navigate(NavDestinations.VerifyUser.route)
                                    } else if (user.topics.isNullOrEmpty()) {
                                        navController.navigate(NavDestinations.ChooseInterest.route)
                                    } else {
                                        navController.navigate(NavDestinations.Home.route)
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    application,
                                    user.errors!![0].message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            if (response.errors!![0].message == "account not verified") {
                                navController.navigate(NavDestinations.VerifyUser.route)
                            }

                            Toast.makeText(
                                application,
                                response.errors!![0].message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(application, application.getString(R.string.please_enter_valid_values), Toast.LENGTH_SHORT).show()
        }
    }

    fun signInWithGoogle(navController: NavController, token: String) {
        if (token.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = sdk.googleLogin(GoogleAuth(
                access_token = token,
            ))

            if (response.error) {
                Toast.makeText(
                    application,
                    response.errors!![0].message,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val user = sdk.getUser()
                if (!user.error) {
                    user.data?.let { user ->
                        val sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(application)
                        sharedPreferences.edit()
                            .putBoolean("signedIn", true)
                            .apply()

                        withContext(Dispatchers.Main) {
                            if (!user.is_verified.orFalse()) {
                                navController.navigate(NavDestinations.VerifyUser.route)
                            } else if (user.topics.isNullOrEmpty()) {
                                navController.navigate(NavDestinations.ChooseInterest.route) {
                                    popUpTo(NavDestinations.SignIn.route) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(NavDestinations.Home.route) {
                                    popUpTo(NavDestinations.SignIn.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            application,
                            user.errors!![0].message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }

            loading.value = false
        }
    }
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}