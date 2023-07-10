package com.mobileappconsultant.newsfeed.utils

sealed class NavDestinations {
    abstract val route: String

    object Onboarding : NavDestinations() {
        override val route: String = "onboarding"
    }

    object SignIn: NavDestinations() {
        override val route: String = "sign_in"
    }

    object SignUp: NavDestinations() {
        override val route: String = "sign_up"
    }

    object Home: NavDestinations() {
        override val route: String = "home"
    }

    object ChooseInterest: NavDestinations() {
        override val route: String = "choose_interest"
    }

    object ForgotPassword: NavDestinations() {
        override val route: String = "forgot_password"
    }

    object ResetPassword: NavDestinations() {
        override val route: String = "reset_password"
    }

    object Welcome: NavDestinations() {
        override val route: String = "welcome"
    }

    object NewsDetails: NavDestinations() {
        override val route: String = "news_details"
    }

    object VerifyUser: NavDestinations() {
        override val route: String = "verify_user"
    }

    object Profile: NavDestinations() {
        override val route: String = "profile"
    }
}