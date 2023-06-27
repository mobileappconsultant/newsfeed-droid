package com.mobileappconsultant.newsfeed.screens.reset_password

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.NewsHolder
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.LabelTextField
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeed.screens.reset_password.viewmodel.ResetPasswordViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import com.mobileappconsultant.newsfeed.utils.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: ResetPasswordViewModel,
) {
    val email = NewsHolder.email ?: return

    val uiState by remember { viewModel.uiState }

    var password by remember { viewModel.newPassword }
    var confirmPassword by remember { viewModel.confirmPassword }
    val showSuccess by remember { viewModel.showSuccess }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_a_new_password))},
                navigationIcon = { Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.popBackStack() },
                    painter = painterResource(
                        R.drawable.back), contentDescription = stringResource(R.string.back_icon)
                    )
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Image(
                painter = painterResource(id = R.drawable.reset_password),
                contentDescription = stringResource(R.string.reset_password_image),
            )
            
            Text(
                text = stringResource(id = R.string.create_a_new_password),
            )

            LabelTextField(
                label = "",
                leading = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = stringResource(R.string.password)
                    )
                },
                placeholder = stringResource(R.string.enter_your_new_password),
                value = password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
            ) { password = it }

            LabelTextField(
                label = "",
                leading = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = stringResource(R.string.password)
                    )
                },
                placeholder = stringResource(R.string.confirm_password),
                value = confirmPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            ) { confirmPassword = it }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.continue_txt)
            ) { viewModel.resetPassword(email) }

            if (showSuccess) {
                SuccessDialog() {
                    navController.navigate(NavDestinations.SignIn.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        if (uiState == UIState.LOADING) {
            LoadingIndicator()
        }
    }
}

@Composable
fun SuccessDialog(
    title: String = "New password update successful.",
    primaryButtonText: String = stringResource(R.string.login),
    onClick: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.success),
                contentDescription = stringResource(R.string.success_image),
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.successful),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W500,
            )
            
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )

            Box {}

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = primaryButtonText,
            ) {
                onClick()
            }
        }
    }
}