package com.mobileappconsultant.newsfeed.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mobileappconsultant.newsfeed.NewsHolder
import com.mobileappconsultant.newsfeed.R
import com.mobileappconsultant.newsfeed.components.ColoredButton
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeed.screens.home.viewmodel.HomeScreenViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import com.mobileappconsultant.newsfeed.utils.NavDestinations
import com.mobileappconsultant.newsfeedmmsdk.models.Article
import kotlinx.coroutines.launch

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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val searchQuery = remember { viewModel.searchQuery }

    val showDeletDialog = remember { mutableStateOf(false) }

    val sources = newsSources.filter { it.name.lowercase().contains(searchQuery.value.lowercase()) }

    LaunchedEffect("") {
        viewModel.fetchData(navController)
    }

    ModalNavigationDrawer(
        drawerContent = {
            HomeDrawer(viewModel, navController) {
                scope.launch {
                    drawerState.close()
                }
                showDeletDialog.value = true
            }
        },
        drawerState = drawerState
    ) {
        Scaffold { padding ->

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    if (drawerState.isOpen) drawerState.close()
                                    else drawerState.open()
                                }
                            }
                            .padding(12.dp),
                        painter = painterResource(id = R.drawable.burger),
                        contentDescription = ""
                    )
                    Image(
                        painter = painterResource(id = R.drawable.latest_news),
                        contentDescription = stringResource(R.string.latest_news),
                    )
                }

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    value = searchQuery.value, onValueChange = { viewModel.searchQuery.value = it },
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(text = "Search Newsfeed")},
                    leadingIcon = {Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")},
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    maxLines = 1
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(sources.size) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LatestNewsIcon(
                                sources[it].url,
                                selectedNewsSource.value == sources[it],
                            ) {
                                viewModel.setSelectedNewsSource(sources[it])
                            }

                            Text(sources[it].name)
                        }
                    }
                }

                if (selectedNewsSource.value != null) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedNewsSource.value?.categories?.indexOf(
                            selectedCategory.value
                        ) ?: 0,
                        divider = {},
                        edgePadding = 0.dp
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
                    text = stringResource(R.string.breaking_news),
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
                    text = stringResource(R.string.trending_news),
                    style = MaterialTheme.typography.titleLarge,
                )

                val valueToDrop =
                    if (selectedCategory.value.articles.size >= 5) 5 else selectedCategory.value.articles.size

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
                        text = stringResource(R.string.no_trending_news_at_the_moment),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            if (uiState.value == UIState.LOADING) {
                LoadingIndicator()
            }
        }
    }

    if (showDeletDialog.value) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)))
        DeleteAccountDialog { delete ->
            showDeletDialog.value = false

            if (delete) {
                viewModel.deleteAccount(navController)
            }
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
                contentDescription = stringResource(R.string.news),
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
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .width(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .size(width = 260.dp, height = 141.dp),
            model = article.imageUrl, contentDescription = stringResource(R.string.article_image),
            contentScale = ContentScale.Crop,
        )

        Text(
            text = article.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.W500,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.mdi_web), contentDescription = "mdi_web",
            )
            Text(
                modifier = Modifier.weight(1f),
                text = article.categories?.joinToString() ?: stringResource(R.string.unknown),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.bodySmall
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(4.dp)
                    .background(Color.Gray),
            ) {}

            Text(
                text = article.pubDate, style = MaterialTheme.typography.bodySmall,
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDrawer(
    viewModel: HomeScreenViewModel,
    navController: NavController,
    onDeleteClick: () -> Unit
) {
    val profile = remember { viewModel.user }

    ModalDrawerSheet{
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.safepal),
                contentDescription = "Newsfeed logo",
            )

            Text(
                text = "NEWSFEED",
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Divider(color = MaterialTheme.colorScheme.primary)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {

            Box(modifier = Modifier.height(30.dp)) {}

            SideTile(
                resource = R.drawable.edit2,
                title = "Edit Interest"
            ) {
                NewsHolder.showBackButton = true
                navController.navigate(NavDestinations.ChooseInterest.route)
            }

            SideTile(
                resource = R.drawable.profile2,
                title = "Profile"
            ) {
                navController.navigate(NavDestinations.Profile.route)
            }

            SideTile(
                resource = R.drawable.logout2,
                title = "Logout"
            ) {
                viewModel.logout()
                navController.navigate(NavDestinations.SignIn.route) {
                    popUpTo(navController.graph.startDestinationRoute ?: "") {
                        inclusive = true
                    }
                }
            }

            SideTile(
                resource = R.drawable.delete2,
                title = "Delete Account",
                onClick = onDeleteClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Divider(color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(id = R.drawable.account), contentDescription = "Account",
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = profile.value?.full_name ?: "Unknown",
                )
            }
        }

    }
}

@Composable
fun SideTile(resource: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painter = painterResource(id = resource), contentDescription = "")
        Text(text = title)
    }
}

@Composable
fun DeleteAccountDialog(
    onDismiss: (delete: Boolean) -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Delete Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Are you sure you want to delete your account? This operation CANNOT be reversed!",
            )

            ColoredButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Yes, Delete My Account",
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
            ) {
                onDismiss(true)
            }

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "No",
                textColor = Color.White
            ) {
                onDismiss(false)
            }
        }
    }
}