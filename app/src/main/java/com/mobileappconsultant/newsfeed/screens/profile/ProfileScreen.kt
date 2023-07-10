package com.mobileappconsultant.newsfeed.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.mobileappconsultant.newsfeed.screens.profile.viewmodel.ProfileUiState
import com.mobileappconsultant.newsfeed.screens.profile.viewmodel.ProfileViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    var uiState by remember { viewModel.uiState }
    var oldPassword by remember { viewModel.oldPassword }
    var newPassword by remember { viewModel.newPassword }
    var newPasswordConfirm by remember { viewModel.newPasswordConfirm }
    val loading by remember { viewModel.loading }

    val user by remember { viewModel.user }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProfile(navController)
    }

    Scaffold(
        topBar = {ProfileTopBar(navController)}
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState == ProfileUiState.NORMAL) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Person"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = user?.full_name ?: "Unknown", onValueChange = {},
                        label = { Text("Name") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = user?.phone_number ?: "Unknown", onValueChange = {},
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = user?.email ?: "Unknown", onValueChange = {},
                        label = { Text("someuseremail@email.com") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                Text(
                    modifier = Modifier
                        .clickable {
                            uiState = ProfileUiState.CHANGE_PASSWORD
                        }
                        .padding(8.dp),
                    text = "Change Password",
                    color = MaterialTheme.colorScheme.primary
                )

                Box(modifier = Modifier.height(16.dp)) {}
            } else if (uiState == ProfileUiState.CHANGE_PASSWORD) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabelTextField(
                        label = "Old Password",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.password),
                                contentDescription = stringResource(R.string.password)
                            )
                        },
                        placeholder = stringResource(R.string.enter_your_password),
                        value = oldPassword,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    ) { oldPassword = it }

                    LabelTextField(
                        label = "New Password",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.password),
                                contentDescription = stringResource(R.string.password)
                            )
                        },
                        placeholder = stringResource(R.string.enter_your_password),
                        value = newPassword,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    ) { newPassword = it }

                    LabelTextField(
                        label = "Confirm New Password",
                        leading = {
                            Image(
                                painter = painterResource(id = R.drawable.password),
                                contentDescription = stringResource(R.string.password)
                            )
                        },
                        placeholder = stringResource(R.string.enter_your_password),
                        value = newPasswordConfirm,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    ) { newPasswordConfirm = it }
                }

                PrimaryButton(
                    text = "DONE",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (oldPassword.isEmpty()) {
                        Toast.makeText(context, "Please enter old password", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }

                    if (newPassword.isEmpty()) {
                        Toast.makeText(context, "Please enter new password", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }

                    if (newPasswordConfirm.isEmpty()) {
                        Toast.makeText(context, "Please confirm password", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }

                    if (newPassword != newPasswordConfirm) {
                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                        return@PrimaryButton
                    }

                    viewModel.changePassword(navController)
                }
            }
        }
    }

    if (loading) {
        LoadingIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    TopAppBar(
        title = { Text(text = "Profile")},
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.back), contentDescription = "",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp)
            )
        }
    )
}
