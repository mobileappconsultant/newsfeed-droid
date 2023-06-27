package com.mobileappconsultant.newsfeed.screens.sign_in

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mobileappconsultant.newsfeed.MainActivity
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.LabelTextField
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.sign_in.viewmodel.SignInViewModel
import com.mobileappconsultant.newsfeed.screens.welcome.components.LineDivider
import com.mobileappconsultant.newsfeed.screens.welcome.components.SocialButton
import com.mobileappconsultant.newsfeed.ui.theme.blue
import com.mobileappconsultant.newsfeed.utils.BackgroundShimmer
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.NewsFeedSDK
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.Login
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel,
) {
    var username by remember { viewModel.username }
    var password by remember { viewModel.password }
    val loading by remember { viewModel.loading }
    var rememberSelected by remember { viewModel.rememberSelected }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blue),
    ) {
        BackgroundShimmer()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .clickable { navController.popBackStack() },
                painter = painterResource(id = R.drawable.back),
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = stringResource(R.string.back_button),
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.login_to_your_account),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                color = Color.White,
            )

            // Spacing
            Box {}

            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                ) {
                    LabelTextField(
                        label = stringResource(R.string.username),
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.person),
                                contentDescription = stringResource(R.string.person)
                            )
                        },
                        placeholder = stringResource(R.string.enter_your_username),
                        value = username,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    ) { username = it }

                    LabelTextField(
                        label = stringResource(R.string.password),
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.password),
                                contentDescription = stringResource(R.string.password)
                            )
                        },
                        placeholder = stringResource(R.string.enter_your_password),
                        value = password,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    ) { password = it }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                modifier = Modifier.padding(end = 8.dp),
                                checked = rememberSelected,
                                onCheckedChange = { rememberSelected = it },
                            )

                            Text(
                                text = stringResource(R.string.remember),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Text(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(NavDestinations.ForgotPassword.route)
                                },
                            text = stringResource(R.string.forgot_password),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Box {}

                    // Sign Up Button
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.sign_in),
                    ) {
                        viewModel.doSignIn(navController)
                    }

                    Box {}

                    LineDivider(text = stringResource(R.string.or))

                    Box {}

                    SocialButton(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = stringResource(R.string.google_logo)
                            )
                        },
                        text = stringResource(R.string.continue_with_google),
                        onClick = {
                            coroutineScope.launch {
                                (context as MainActivity).startGoogleSignIn { token ->
                                    viewModel.signInWithGoogle(navController, token)
                                }
                            }
                        },
                    )

                    Box {}

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                    ) {
                        Text(stringResource(R.string.don_t_have_an_account))
                        Text(
                            text = stringResource(R.string.sign_up),
                            modifier = Modifier.clickable { navController.navigate(NavDestinations.SignUp.route) },
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.W600,
                        )
                    }
                }
            }
        }

        if (loading) {
            LoadingIndicator()
            return
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable { },
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}