package com.mobileappconsultant.newsfeed.screens.news_details

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.mobileappconsultant.newsfeed.components.PrimaryButton
import com.mobileappconsultant.newsfeed.screens.choose_interest.viewmodel.UIState
import com.mobileappconsultant.newsfeed.screens.news_details.viewmodel.NewsDetailsViewModel
import com.mobileappconsultant.newsfeed.screens.sign_in.LoadingIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailsScreen(
    navController: NavController,
    viewModel: NewsDetailsViewModel,
) {

    val article = NewsHolder.news ?: return
    val showSummary = remember {
        viewModel.showSummary
    }
    val uiState = remember { viewModel.uiState }

    val summaryText = remember { viewModel.summaryText }

    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(
            modifier = Modifier
                .padding(8.dp),
            navigationIcon = {
                Icon(
                    modifier = Modifier
                        .clickable { navController.popBackStack() },
                    painter = painterResource(R.drawable.back),
                    contentDescription = stringResource(R.string.back),
                )
            },
            title = {},
            actions = {
                Icon(
                    modifier = Modifier
                        .clickable {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, article.link)
                                putExtra(Intent.EXTRA_SUBJECT, article.title)
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(intent, context.getString(R.string.share_news))
                            context.startActivity(shareIntent)
                        }
                        .padding(end = 16.dp),
                    painter = painterResource(R.drawable.share),
                    contentDescription = stringResource(R.string.share),
                )
            }
        ) }
    ){ padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                ) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            item {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            item {
                Row(
                    modifier = Modifier.clickable {
                        val url = article.link
                        val intent = CustomTabsIntent.Builder().build()
                        intent.launchUrl(context, Uri.parse(url))
                    },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painter = painterResource(id = R.drawable.mdi_web), contentDescription = "mdi_web")
                    Text(
                        modifier = Modifier.weight(1f),
                        text = article.categories?.joinToString() ?: stringResource(R.string.unknown),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.W500,
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(8.dp)
                            .background(Color.Gray),
                    ) {}

                    Text(
                        text = article.pubDate, style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = stringResource(R.string.heart),
                        )

                        Text(stringResource(R.string._0_likes))
                    }

                    Row(
                        modifier = Modifier
                            .clickable { viewModel.summarizeArticle(article) },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.chatgpt),
                            contentDescription = stringResource(R.string.summarize),
                        )

                        Text(stringResource(R.string.summarize))
                    }
                }
            }

            item {
                Text(article.content)
            }
        }

        if (showSummary.value) {
            SummarizeDialog(summaryText.value) {
                showSummary.value = false
            }
        }
    }

    if (uiState.value == UIState.LOADING) {
        LoadingIndicator(removeTouch = true)
    }
}

@Composable
fun SummarizeDialog(
    summary: String,
    onDismiss: () -> Unit,
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
                .height(500.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.article_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier.weight(1f)
                    .verticalScroll(rememberScrollState()),
                text = summary,
            )

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.close),
            ) {
                onDismiss()
            }
        }
    }
}


