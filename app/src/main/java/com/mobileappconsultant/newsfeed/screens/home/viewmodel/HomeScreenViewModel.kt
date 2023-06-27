package com.mobileappconsultant.newsfeed.screens.home.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.NewsInterest
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeed.screens.sign_in.viewmodel.orFalse
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
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
    val uiState = mutableStateOf(UIState.NORMAL)

    private var dataFetched = false

    fun fetchData() {
        if (dataFetched) {
            return
        }
        dataFetched = true

        viewModelScope.launch(Dispatchers.IO) {
            uiState.value = UIState.LOADING
            val news = sdk.fetchNewsSources()
            if (news.error) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, news.errors!![0].message, Toast.LENGTH_SHORT).show()
                }
            } else {
                newsSources.clear()
                newsSources.addAll(news.data ?: listOf())
                setSelectedNewsSource(newsSources[0])

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
}
