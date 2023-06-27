package com.mobileappconsultant.newsfeed.screens.verify_user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.LabelTextField
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.reset_password.SuccessDialog
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import com.mobileappconsultant.newsfeed.screens.verify_user.viewmodel.VerifyUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyUserScreen(
    navController: NavController,
    viewModel: VerifyUserViewModel,
) {

    var code by remember { viewModel.verificationCode }
    val loading by remember { viewModel.loading }
    val showSuccess by remember { viewModel.showSuccess }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.verify_user))},
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
                contentDescription = stringResource(R.string.verify_user_account),
            )

            Text(
                text = stringResource(R.string.verify_your_account),
            )

            LabelTextField(
                label = stringResource(R.string.enter_verification_code),
                leading = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = stringResource(R.string.password)
                    )
                },
                placeholder = stringResource(R.string.verification_code),
                value = code,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
            ) { code = it }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.verify)
            ) {
                viewModel.doVerify()
            }

            if (showSuccess) {
                SuccessDialog(
                    title = stringResource(R.string.successfully_verified_your_account),
                    primaryButtonText = stringResource(R.string.continue_txt),
                ) {
                    viewModel.onSuccessClick(navController)
                }
            }
        }

        if (loading) {
            LoadingIndicator()
        }
    }
}