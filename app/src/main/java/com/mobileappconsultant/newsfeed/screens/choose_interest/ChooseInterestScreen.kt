package com.mobileappconsultant.newsfeed.screens.choose_interest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.ChooseInterestsViewModel
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChooseInterestScreen(
    navController: NavController,
    viewModel: ChooseInterestsViewModel,
) {

    val uiState by remember { viewModel.uiState }
    val interests = remember { viewModel.interests }
    val selectedInterests = remember { viewModel.selectedInterests }

    Scaffold {

        LaunchedEffect(Unit) {
            viewModel.fetchInterests()
        }

        if (uiState == UIState.LOADING) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.select_your_favourite_news_categories),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W500,
            )

            Box {}

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            ) {
                interests.forEach { interest ->
                    NewsCategoryUI(
                        name = interest.name, selected = selectedInterests.contains(interest),
                    ) {
                        viewModel.interestSelected(interest)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.continue_txt),
            ) {
                viewModel.continueClick(navController)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun NewsCategoryUI(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    if (selected) Color.Transparent else Color.Black,
                    RoundedCornerShape(8.dp)
                )
                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            ) {
                Icon(
                    painter = painterResource(R.drawable.add), contentDescription = stringResource(R.string.add_icon),
                    tint = if (selected) Color.White else Color.Black,
                )
                Text(name, color = if (selected) Color.White else Color.Black)
            }
        }
    }
}