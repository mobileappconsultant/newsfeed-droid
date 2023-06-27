package com.mobileappconsultant.newsfeed.screens.welcome.viewmodel

import android.app.Application
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.screens.sign_in.viewmodel.orFalse
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.GoogleAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeScreenViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {

    val loading = mutableStateOf(false)

    fun signInWithGoogle(navController: NavController, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = sdk.googleLogin(
                GoogleAuth(
                access_token = token,
            )
            )

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
                                navController.navigate(NavDestinations.ChooseInterest.route)
                            } else {
                                navController.navigate(NavDestinations.Home.route)
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