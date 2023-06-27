package com.mobileappconsultant.newsfeed.screens.sign_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.LabelTextField
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import com.mobileappconsultant.newsfeed.screens.sign_up.viewmodel.SignUpViewModel
import com.mobileappconsultant.newsfeed.screens.welcome.components.LineDivider
import com.mobileappconsultant.newsfeed.screens.welcome.components.SocialButton
import com.mobileappconsultant.newsfeed.ui.theme.blue
import com.mobileappconsultant.newsfeed.utils.BackgroundShimmer
import com.mobileappconsultant.newsfeed.utils.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel,
) {

    var username by remember { viewModel.username }
    var email by remember { viewModel.email }
    var phoneNumber by remember { viewModel.phoneNumber }
    var password by remember { viewModel.password }

    val loading by remember { viewModel.loading }

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
                contentDescription = "Back button",
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Create Your Account",
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LabelTextField(
                        label = "Username",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.person),
                                contentDescription = "Person"
                            )
                        },
                        placeholder = "Enter your username",
                        value = username,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    ) { username = it }

                    LabelTextField(
                        label = "Email",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.mail),
                                contentDescription = "Email"
                            )
                        },
                        placeholder = "Enter your email",
                        value = email,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    ) { email = it }

                    LabelTextField(
                        label = "Phone Number",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.phone),
                                contentDescription = "Phone Number"
                            )
                        },
                        placeholder = "Phone Number",
                        value = phoneNumber,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next,
                        ),
                    ) { phoneNumber = it }

                    LabelTextField(
                        label = "Password",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.password),
                                contentDescription = "Password"
                            )
                        },
                        placeholder = "Enter your password",
                        supportingText = { Text("Password needs at least 8 characters") },
                        value = password,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    ) { password = it }

                    Box {}

                    // Sign Up Button
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "SIGN UP"
                    ) {
                        viewModel.doSignUp(navController)
                    }

                    Box {}

                    LineDivider(text = "Or")

                    Box {}

                    SocialButton(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google logo"
                            )
                        },
                        text = "Continue with Google",
                        onClick = {},
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                    ) {
                        Text("Already have an account? ")
                        Text(
                            "Sign In",
                            modifier = Modifier.clickable { navController.navigate(NavDestinations.SignIn.route) },
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.W600,
                        )
                    }
                }
            }
        }

        if (loading) {
            LoadingIndicator()
        }
    }
}