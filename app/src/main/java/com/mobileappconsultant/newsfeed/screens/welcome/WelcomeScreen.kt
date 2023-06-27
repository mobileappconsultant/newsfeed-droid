package com.mobileappconsultant.newsfeed.screens.welcome

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.MainActivity
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.welcome.components.LineDivider
import com.mobileappconsultant.newsfeed.screens.welcome.components.SocialButton
import com.mobileappconsultant.newsfeed.screens.welcome.viewmodel.WelcomeScreenViewModel
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: WelcomeScreenViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val loading = remember { viewModel.loading }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        SocialButton(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google logo"
                )
            },
            text = "Continue with Google",
            onClick = {
                coroutineScope.launch {
                    loading.value = true
                    (context as MainActivity).startGoogleSignIn { token ->
                        loading.value = false

                        if (token.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Unable to sign into Google",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.signInWithGoogle(navController, token)
                        }
                    }
                }
            },
        )

        // For extra spacing
        Box {}

        LineDivider(text = "Or")

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "SIGN IN WITH PASSWORD",
        ) {
            navController.navigate(NavDestinations.SignIn.route) {
                popUpTo(NavDestinations.Welcome.route) {
                    inclusive = true
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)) {
            Text("Don't have an account? ")
            Text(
                "Sign Up",
                modifier = Modifier.clickable { navController.navigate(NavDestinations.SignUp.route) {
                    popUpTo(NavDestinations.Welcome.route) {
                        inclusive = true
                    }
                } },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W600,
            )
        }
    }
}
