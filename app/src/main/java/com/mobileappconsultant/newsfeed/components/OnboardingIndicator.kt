package com.mobileappconsultant.newsfeed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingIndicators(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    count: Int,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        for (i in 0 until count) {
            OnboardingIndicator(selected = i == selectedIndex)
    }
    }
}

@Composable
fun OnboardingIndicator(
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .width(if (selected) 34.dp else 8.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE7E7E7))
    ) {}
}