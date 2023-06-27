package com.mobileappconsultant.newsfeed.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mobileappconsultant.newsfeed.R

@Composable
fun BackgroundShimmer() {
    Row {
        Spacer(modifier = Modifier.weight(1.0f))
        Image(
            painter = painterResource(id = R.drawable.particles),
            contentDescription = "Shiny Particles",
        )
    }
}