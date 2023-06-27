package com.mobileappconsultant.newsfeed.screens.forgot_password

import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.forgot_password.viewmodel.ForgotPasswordUIState
import com.mobileappconsultant.newsfeed.screens.forgot_password.viewmodel.ForgotPasswordViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import com.mobileappconsultant.newsfeed.utils.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel,
) {
    val uiState by remember { viewModel.uiState }

    Scaffold(
        topBar = {
             TopAppBar(title = { Text(
                 text = stringResource(R.string.forgot_password),
                 fontWeight = FontWeight.W500,
             ) })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            when (uiState) {
                ForgotPasswordUIState.NORMAL -> ForgotPassword(
                    navController,
                    viewModel,
                    this,
                )
                ForgotPasswordUIState.OTP_AUTHENTICATION -> EnterOTP(
                    navController,
                    viewModel,
                    this,
                )
                ForgotPasswordUIState.LOADING -> LoadingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(
    navController: NavController,
    viewModel: ForgotPasswordViewModel,
    scope: ColumnScope,
) {
    var email by remember { viewModel.email }
    val emailError by remember { viewModel.emailError }

    scope.apply {
        Text(
            text = stringResource(R.string.enter_email_address_to_reset_your_password),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = stringResource(R.string.email_address),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.enter_email),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        isError = emailError != null,
                        singleLine = true,
                        supportingText = emailError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done,
                        ),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.continue_txt),
            onClick = { viewModel.onContinueClick(navController) },
        )
    }
}

@Composable
fun EnterOTP(
    navController: NavController,
    viewModel: ForgotPasswordViewModel,
    scope: ColumnScope,
) {
    val email by remember { viewModel.email }
    var otpValue by remember { viewModel.otp }
    val showResendCode by remember { viewModel.showResendCode }
    val remainingTime by remember { viewModel.remainingTime }

    scope.apply {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.otp_authentication),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.enter_otp_sent_to_s, email),
            textAlign = TextAlign.Center,
        )

        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = otpValue,
            onValueChange = {
                if (it.length <= 6) {
                    otpValue = it
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
            ),
            singleLine = true,
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(6) {index ->
                        val char = when {
                            index >= otpValue.length -> ""
                            else -> otpValue[index].toString()
                        }
                        
                        Text(
                            modifier = Modifier
                                .width(50.dp)
                                .border(
                                    1.dp,
                                    Color(0xFF666666),
                                    RoundedCornerShape(16.dp),
                                )
                                .background(if (char.isNotEmpty()) Color(0x11094D8B) else Color.Transparent)
                                .padding(8.dp),
                            text = char,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF094D8B),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            },
        )

        if (showResendCode) {
            PrimaryButton(
                text = stringResource(R.string.resend_otp),
            ) {
                viewModel.resendCode()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(R.string.resend_otp_in),
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = "${remainingTime}s",
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF094D8B),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.continue_txt),
        ) {
            viewModel.onContinueClick(navController)
        }
    }
}