package com.mobileappconsultant.newsfeed.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mobileappconsultant.newsfeed.NewsHolder
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeed.screens.home.viewmodel.HomeScreenViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.models.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel,
) {
    val uiState = remember { viewModel.uiState }
    val newsSources = remember { viewModel.newsSources }
    val selectedNewsSource = remember { viewModel.selectedNewsSource }
    val selectedCategory = remember { viewModel.selectedInterest }

    LaunchedEffect("") {
        viewModel.fetchData()
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.latest_news),
                contentDescription = "Latest news",
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(newsSources.size) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LatestNewsIcon(
                            newsSources[it].url,
                            selectedNewsSource.value == newsSources[it],
                        ) {
                            viewModel.setSelectedNewsSource(newsSources[it])
                        }

                        Text(newsSources[it].name)
                    }
                }
            }

            if (selectedNewsSource.value != null) {
                ScrollableTabRow(
                    selectedTabIndex = selectedNewsSource.value?.categories?.indexOf(selectedCategory.value) ?: 0,
                    divider = {},
                ) {
                    selectedNewsSource.value?.categories?.forEachIndexed { _, category ->
                        Tab(
                            selected = category == selectedCategory.value,
                            onClick = { viewModel.updateSelectedCategory(category) },
                            text = { Text(category.name ?: "Invalid") },
                        )
                    }
                }
            }
            
            Text(
                text = "Breaking News",
                style = MaterialTheme.typography.bodyLarge,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (selectedCategory.value.articles.isNotEmpty()) {
                    items(if (selectedCategory.value.articles.size > 5) 5 else selectedCategory.value.articles.size) {
                        BreakingNewsItem(selectedCategory.value.articles[it]) {
                            NewsHolder.news = selectedCategory.value.articles[it]
                            navController.navigate(NavDestinations.NewsDetails.route)
                        }
                    }
                }
            }

            Text(
                text = "Trending News",
                style = MaterialTheme.typography.titleLarge,
            )

            val valueToDrop = if (selectedCategory.value.articles.size >= 5) 5 else selectedCategory.value.articles.size

            selectedCategory.value.articles.drop(valueToDrop).forEach { article ->
                TrendingNewsItem(
                    title = article.title,
                    subtitle = article.description,
                    image = article.imageUrl ?: "",
                ) {
                    NewsHolder.news = article
                    navController.navigate(NavDestinations.NewsDetails.route)
                }
            }

            if (selectedCategory.value.articles.size <= 5) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "No Trending News at the moment",
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (uiState.value == UIState.LOADING) {
            LoadingIndicator()
        }
    }
}

@Composable
fun LatestNewsIcon(
    imageUrl: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .size(50.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.LightGray)
            .padding(4.dp)
    ) {
        imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        if (imageUrl == null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.newsfeed_logo),
                contentDescription = "News",
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}

@Composable
fun BreakingNewsItem(
    article: Article,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .size(width = 240.dp, height = 330.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = article.imageUrl, contentDescription = "Article Image",
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.W400,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Like
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.like_outline),
                        contentDescription = "Like",
                        tint = Color.White,
                    )

                    Text(
                        text = "800",
                        color = Color.White,
                    )
                }

                // Message
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.message),
                        contentDescription = "Message",
                        tint = Color.White,
                    )

                    Text(
                        text = "201",
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingNewsItem(
    title: String,
    subtitle: String,
    image: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(60.dp)
                .background(Color.LightGray),
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}