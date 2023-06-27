package com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.CompleteRegistration
import com.mobileappconsultant.newsfeedmmsdk.optionalOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseInterestsViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {
    val selectedInterests = mutableStateListOf<NewsInterest>()
    val interests = mutableStateListOf<NewsInterest>()

    val uiState = mutableStateOf(UIState.NORMAL)

    suspend fun fetchInterests() {
        withContext(Dispatchers.IO) {
            uiState.value = UIState.LOADING
            val response = sdk.getNewsCategories()
            uiState.value = UIState.NORMAL

            withContext(Dispatchers.Main) {
                if (response.error) {
                    Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                response.data!!.forEach { item ->
                    interests.add(NewsInterest(
                        id = item.id ?: "",
                        name = item.name ?: "",
                    ))
                }
            }
        }
    }

    fun interestSelected(category: NewsInterest) {
        if (selectedInterests.contains(category)) {
            selectedInterests.remove(category)
        } else {
            selectedInterests.add(category)
        }
    }

    fun continueClick(
        navController: NavController,
    ) {
        if (selectedInterests.isEmpty()) {
            Toast.makeText(application, application.getString(R.string.please_select_at_least_one_interest), Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            uiState.value = UIState.LOADING
            val response = sdk.completeRegistration(
                CompleteRegistration(topics = optionalOf(
                    selectedInterests.map { it.name }
                ))
            )
            uiState.value = UIState.NORMAL

            withContext(Dispatchers.Main) {
                if (response.error) {
                    Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                navController.navigate(NavDestinations.Home.route)
            }
        }
    }
}

data class NewsInterest(
    val id: String,
    val name: String,
)

enum class UIState {
    NORMAL,
    LOADING
}