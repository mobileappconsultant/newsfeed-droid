package com.mobileappconsultant.newsfeed.screens.news_details.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.PromptContent
import com.mobileappconsultant.newsfeedmmsdk.models.Article
import com.mobileappconsultant.newsfeedmmsdk.optionalOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsDetailsViewModel(
    private val sdk: NewsFeedSDK,
    private val application: Application,
): ViewModel() {
    val uiState = mutableStateOf(UIState.NORMAL)
    val showSummary = mutableStateOf(false)
    val summaryText = mutableStateOf("")

    fun summarizeArticle(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value = UIState.LOADING
            val response = sdk.askKora(PromptContent(
                optionalOf("Summarize this article ${article.link}")
            ))

            if (response.error) {
                showSummary.value = false
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, response.errors!![0].message, Toast.LENGTH_SHORT).show()
                }
            } else {
                summaryText.value = response.data?.result ?: ""
                showSummary.value = true
            }

            uiState.value = UIState.NORMAL
        }
    }
}