package com.mobileappconsultant.newsfeed

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.mobileappconsultant.newsfeed.screens.choose_interest.ChooseInterestScreen
import com.mobileappconsultant.newsfeed.screens.forgot_password.ForgotPasswordScreen
import com.mobileappconsultant.newsfeed.screens.home.HomeScreen
import com.mobileappconsultant.newsfeed.screens.news_details.NewsDetailsScreen
import com.mobileappconsultant.newsfeed.screens.onboarding.OnboardingScreen
import com.mobileappconsultant.newsfeed.screens.profile.ProfileScreen
import com.mobileappconsultant.newsfeed.screens.reset_password.ResetPasswordScreen
import com.mobileappconsultant.newsfeed.screens.sign_in.SignInScreen
import com.mobileappconsultant.newsfeed.screens.sign_up.SignUpScreen
import com.mobileappconsultant.newsfeed.screens.verify_user.VerifyUserScreen
import com.mobileappconsultant.newsfeed.screens.welcome.WelcomeScreen
import com.mobileappconsultant.newsfeed.ui.theme.MyApplicationTheme
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    val RC_SIGN_IN = 1011

    var googleTokenCompleter: (token: String) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val sharedPreferences = getDefaultSharedPreferences(this)

            var initialRoute = NavDestinations.Onboarding.route

            if (sharedPreferences.contains("signedIn")) {
                initialRoute  = NavDestinations.Home.route
            }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = initialRoute,
                    ) {

                        composable(NavDestinations.Onboarding.route) {
                            OnboardingScreen(
                                navController,
                                koinViewModel(),
                            )
                        }

                        composable(NavDestinations.SignUp.route) {
                            SignUpScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.SignIn.route) {
                            SignInScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.ChooseInterest.route) {
                            ChooseInterestScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                navController,
                                koinViewModel(),
                            )
                        }

                        composable(NavDestinations.ResetPassword.route) {
                            ResetPasswordScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.Home.route) {
                            HomeScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.Welcome.route) {
                            WelcomeScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.NewsDetails.route) {
                            NewsDetailsScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.VerifyUser.route) {
                            VerifyUserScreen(navController, koinViewModel())
                        }

                        composable(NavDestinations.Profile.route) {
                            ProfileScreen(viewModel = koinViewModel(), navController = navController)
                        }

                    }
                }
            }
        }
    }

    fun startGoogleSignIn(
        completer: (token: String) -> Unit,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

            val client = GoogleSignIn.getClient(this@MainActivity, options);
            googleTokenCompleter = completer
            val intent = client.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                GlobalScope.launch(Dispatchers.IO) {
                    val token = GoogleAuthUtil.getToken(
                        this@MainActivity,
                        account.account!!,
                        "oauth2:email profile",
                    )
                    googleTokenCompleter(token)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                googleTokenCompleter("")
            }
        }
    }
}
