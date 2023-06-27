package com.mobileappconsultant.newsfeed.screens.onboarding

import android.view.RoundedCorner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.OnboardingIndicators
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.onboarding.viewmodel.OnboardingViewModel
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel,
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Skip",
            modifier = Modifier.align(Alignment.End),
        )

        Box(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.weight(4f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                pageCount = 3,
                state = pagerState,
                pageSize = PageSize.Fixed(300.dp),
            ) { page ->
                Card(
                    modifier = Modifier.size(240.dp, 332.dp),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(getPageImage(page)),
                        contentDescription = "onboarding image 1",
                        contentScale = ContentScale.FillBounds,
                    )
                }
            }

            Box(modifier = Modifier.height(16.dp))

            OnboardingIndicators(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                selectedIndex = pagerState.currentPage,
                count = 3,
            )

            Text(
                text = stringResource(viewModel.onboardingItems[pagerState.currentPage].title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
            )
            
            Text(
                text = stringResource(viewModel.onboardingItems[pagerState.currentPage].description),
                style = MaterialTheme.typography.bodySmall,
            )
            
            PrimaryButton(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                text = "NEXT",
            ) {
                if (pagerState.currentPage < viewModel.onboardingItems.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    navController.navigate(NavDestinations.Welcome.route) {
                        popUpTo(NavDestinations.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}

fun getPageImage(page: Int): Int {
    return when (page) {
        0 -> R.drawable.onboarding1
        1 -> R.drawable.onboarding2
        2 -> R.drawable.onboarding3
        else -> TODO()
    }
}