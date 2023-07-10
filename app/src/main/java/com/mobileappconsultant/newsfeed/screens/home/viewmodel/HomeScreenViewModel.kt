package com.mobileappconsultant.newsfeed.screens.home.viewmodel

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.NewsInterest
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeed.screens.sign_in.viewmodel.orFalse
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetUserQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.Logout
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.NewsQuery
import com.mobileappconsultant.newsfeedmmsdk.models.Article
import com.mobileappconsultant.newsfeedmmsdk.models.NewsCategory
import com.mobileappconsultant.newsfeedmmsdk.models.NewsSource
import com.mobileappconsultant.newsfeedmmsdk.models.toProperImageURL
import com.mobileappconsultant.newsfeedmmsdk.optionalOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {

    val selectedInterest = mutableStateOf(NewsCategory(
        id = "",
        name = "",
        articles = listOf(),
    ))
    val newsSources = mutableStateListOf<NewsSource>()
    val selectedNewsSource = mutableStateOf<NewsSource?>(null)
    val user = mutableStateOf<GetUserQuery.Response?>(null)
    val uiState = mutableStateOf(UIState.NORMAL)

    val searchQuery = mutableStateOf("")

    private var dataFetched = false

    fun fetchData(navController: NavController) {
        if (dataFetched) {
            return
        }
        dataFetched = true

        viewModelScope.launch(Dispatchers.IO) {
            uiState.value = UIState.LOADING
            val news = sdk.fetchNewsSources(500, 1)
            if (news.error) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, news.errors!![0].message, Toast.LENGTH_SHORT).show()

                    if (news.errors!![0].message == "token expired" || news.errors!![0].message == "invalid token") {
                        navController.navigate(NavDestinations.SignIn.route) {
                            popUpTo(navController.graph.startDestinationRoute ?: "") {
                                inclusive = true
                            }
                        }
                    }
                }
            } else {
                newsSources.clear()
                newsSources.addAll(news.data ?: listOf())
                setSelectedNewsSource(newsSources[0])

            }

            val profile = sdk.getUser()
            if (profile.error) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, profile.errors!![0].message, Toast.LENGTH_SHORT).show()

                    if (profile.errors!![0].message == "token expired" || profile.errors!![0].message == "invalid token") {
                        navController.navigate(NavDestinations.SignIn.route) {
                            popUpTo(navController.graph.startDestinationRoute ?: "") {
                                inclusive = true
                            }
                        }
                    }
                }
            } else {
                user.value = profile.data!!
            }

            uiState.value = UIState.NORMAL
        }
    }

    fun setSelectedNewsSource(source: NewsSource) {
        if (selectedNewsSource.value != source) {
            selectedNewsSource.value = source

            selectedInterest.value = source.categories[0]
        }
    }

    fun updateSelectedCategory(category: NewsCategory) {
        if (selectedInterest.value != category) {
            selectedInterest.value = category
        }
    }

    fun logout() {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)
        sharedPreferences.edit().remove("signed_in").apply()
    }

    fun deleteAccount(navController: NavController) {
        viewModelScope.launch {
            uiState.value = UIState.LOADING
            val response = withContext(Dispatchers.IO) { sdk.deleteUserProfile() }
            uiState.value = UIState.NORMAL

            if (response.error) {
                Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()
            } else {
                logout()

                navController.navigate(NavDestinations.SignIn.route) {
                    popUpTo(navController.graph.startDestinationRoute ?: "") {
                        inclusive = true
                    }
                }
            }
        }
    }
}
