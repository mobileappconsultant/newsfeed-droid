package com.mobileappconsultant.newsfeed.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.typography.bodyMedium.color,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(vertical = 8.dp),
            color = textColor
        )
    }
}

@Composable
fun ColoredButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.typography.bodyMedium.color,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(vertical = 8.dp),
            color = textColor
        )
    }
}