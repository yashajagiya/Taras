package com.example.taras.view.subview

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.rss.RssItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import androidx.compose.material3.CardDefaults
import com.example.taras.core.helpercore.getNewsColor
import com.example.taras.viewmodel.DriverUiModel
import com.example.taras.viewmodel.TeamUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCarousel(
    newsState: UiState<ImmutableList<com.example.taras.network_calls.rss.RssItem>>,
    modifier: Modifier = Modifier,
    drivers: ImmutableList<DriverUiModel> = persistentListOf(),
    teams: ImmutableList<TeamUiModel> = persistentListOf()
) {
    if (newsState !is UiState.Success) return

    val items = newsState.data
    val context = LocalContext.current
    val carouselState = rememberCarouselState { items.size }

    HorizontalMultiBrowseCarousel(
        state = carouselState,
        preferredItemWidth = 320.dp,
        itemSpacing = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
    ) { index ->
        val item = items[index]
        val cardColor = getNewsColor(
            newsItem = item,
            defaultColor = MaterialTheme.colorScheme.surfaceContainer,
            drivers = drivers,
            teams = teams
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier
                .maskClip(MaterialTheme.shapes.extraLarge)
                .clickable {
                    item.link?.let {
                        val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                        context.startActivity(intent)
                    }
                }
        ) {
            Column {
                AsyncImage(
                    model = item.image ?: "https://f1tv.formula1.com/static/favicon.ico",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.title ?: "No Title",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.description ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
