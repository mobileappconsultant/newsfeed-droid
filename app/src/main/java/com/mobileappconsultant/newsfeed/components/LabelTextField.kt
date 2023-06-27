package com.mobileappconsultant.newsfeed.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelTextField(
    label: String,
    placeholder: String,
    leading: @Composable () -> Unit,
    trailing: @Composable () -> Unit = {},
    supportingText: @Composable () -> Unit = {},
    value: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (value: String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = label)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = leading,
            trailingIcon = trailing,
            value = value,
            onValueChange = onChange,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            shape = TextFieldDefaults.filledShape,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = if (!isSystemInDarkTheme()) Color.Transparent else Color.White,
                unfocusedIndicatorColor = if (!isSystemInDarkTheme()) Color.Transparent else Color.White,
                disabledIndicatorColor = if (!isSystemInDarkTheme()) Color.Transparent else Color.White,
                errorIndicatorColor = if (!isSystemInDarkTheme()) Color.Transparent else Color.Red,
                containerColor = if (!isSystemInDarkTheme())  Color(0xFFF3F2F2) else MaterialTheme.colorScheme.surface,
            ),
            supportingText = supportingText,
            placeholder = { Text(placeholder) },
        )
    }
}