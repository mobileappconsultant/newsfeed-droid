package com.mobileappconsultant.newsfeed.screens.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import com.mobileappconsultant.newsfeed.R

data class OnboardingItem(
    val title: Int,
    val description: Int,
)

class OnboardingViewModel: ViewModel() {
    val onboardingItems = listOf(
        OnboardingItem(
            R.string.discover_the_latest_news,
            R.string.more_than_thousands_of_new_stories_come_up_each_day,
        ),
        OnboardingItem(
            R.string.choose_your_interest,
            R.string.choose_the_news_that_you_love_and_are_most_interested_in,
        ),
        OnboardingItem(
            R.string.amazing_ai_features_to_help_with_analysis,
            R.string.smart_experience_from_browsing_to_delivery,
        )
    )
}