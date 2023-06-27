package com.mobileappconsultant.newsfeed

import android.app.Application
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.ChooseInterestsViewModel
import com.mobileappconsultant.newsfeed.screens.forgot_password.viewmodel.ForgotPasswordViewModel
import com.mobileappconsultant.newsfeed.screens.home.viewmodel.HomeScreenViewModel
import com.mobileappconsultant.newsfeed.screens.news_details.viewmodel.NewsDetailsViewModel
import com.mobileappconsultant.newsfeed.screens.onboarding.viewmodel.OnboardingViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.viewmodel.SignInViewModel
import com.mobileappconsultant.newsfeed.screens.sign_up.viewmodel.SignUpViewModel
import com.mobileappconsultant.newsfeed.screens.verify_user.VerifyUserViewModel
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val appModule = module {
    viewModelOf(::SignUpViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ChooseInterestsViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::NewsDetailsViewModel)
    viewModelOf(::VerifyUserViewModel)
    single { NewsFeedSDK }
}

class NewsFeedApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@NewsFeedApplication)
            modules(appModule)
        }
    }
}