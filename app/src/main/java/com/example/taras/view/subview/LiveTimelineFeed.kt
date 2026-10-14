package com.example.taras.view.subview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.SportsScore
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.example.taras.core.media.AudioPlaybackState
import com.example.taras.core.media.F1AudioPlayerManager
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview

/**
 * Pinned Overview Hero Card displayed at the top of the Live Feed.
 */
@Composable
fun LiveTimelineOverviewCard(
    overview: TimelineOverview?,
    discovery: LiveDiscoveryResponse?,
    onViewFullFeedClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val grandPrix = overview?.grandPrix?.ifBlank { discovery?.grandPrix } ?: "FORMULA 1 LIVE RACE"
    val session = overview?.session?.ifBlank { discovery?.session } ?: "Live Session"
    val leader = discovery?.leader?.takeIf { it.isNotBlank() }
        ?: overview?.podiumOrLeader?.firstOrNull() ?: "P1"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Live Indicator & Session Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE53935))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "● LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = session.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "LEADER: $leader",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = grandPrix,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick KPI Counters Deck
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill(
                    icon = "📻",
                    label = "${overview?.totalRadioMessages ?: 0} Radios",
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    icon = "⚠️",
                    label = "${overview?.totalIncidents ?: 0} Incidents",
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    icon = "💬",
                    label = "${overview?.totalQuotes ?: 0} Quotes",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = onViewFullFeedClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "VIEW COMPLETE LIVE COMMENTARY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Formula 1 Tyre Compound representation with official Pirelli styling.
 */
enum class F1TyreCompound(
    val code: String,
    val label: String,
    val color: Color
) {
    SOFT("S", "SOFT", Color(0xFFE53935)),
    MEDIUM("M", "MEDIUM", Color(0xFFFFD54F)),
    HARD("H", "HARD", Color(0xFFFFFFFF)),
    INTERMEDIATE("I", "INTER", Color(0xFF43A047)),
    WET("W", "WET", Color(0xFF1E88E5))
}

/**
 * Resolves which tyre compound is being used for a live lap-by-lap timeline update.
 */
fun resolveTyreCompound(
    item: TimelineItem,
    driverTyres: Map<String, String>? = null
): F1TyreCompound? {
    // 1. Direct tag check
    for (tag in item.tags) {
        val tagUpper = tag.uppercase()
        when {
            tagUpper.contains("SOFT") -> return F1TyreCompound.SOFT
            tagUpper.contains("MEDIUM") -> return F1TyreCompound.MEDIUM
            tagUpper.contains("HARD") -> return F1TyreCompound.HARD
            tagUpper.contains("INTER") -> return F1TyreCompound.INTERMEDIATE
            tagUpper.contains("WET") -> return F1TyreCompound.WET
        }
    }

    // 2. Driver TLA lookup in driverTyres map
    val tla = item.driverTla?.uppercase()
    if (!tla.isNullOrBlank() && driverTyres != null) {
        val tyreFromDriver = driverTyres[tla]
        if (!tyreFromDriver.isNullOrBlank() && tyreFromDriver != "UNKNOWN") {
            mapRawTyreToCompound(tyreFromDriver)?.let { return it }
        }
    }

    // 3. Match driver in driverTyres against author or title
    if (driverTyres != null && driverTyres.isNotEmpty()) {
        val authorUpper = item.author?.uppercase() ?: ""
        val titleUpper = item.title?.uppercase() ?: ""
        for ((driverCode, tyre) in driverTyres) {
            if (tyre.isNotBlank() && tyre != "UNKNOWN") {
                if (authorUpper.contains(driverCode) || titleUpper.contains(driverCode)) {
                    mapRawTyreToCompound(tyre)?.let { return it }
                }
            }
        }
    }

    // 4. Scan title and text for tyre keywords
    val fullContent = "${item.title ?: ""} ${item.text}".lowercase()
    if (fullContent.contains("tyre") || fullContent.contains("tire") || fullContent.contains("pit") || fullContent.contains("compound") || fullContent.contains("set of") || fullContent.contains("stint") || fullContent.contains("onto the") || fullContent.contains("box box")) {
        when {
            fullContent.contains("soft") -> return F1TyreCompound.SOFT
            fullContent.contains("medium") -> return F1TyreCompound.MEDIUM
            fullContent.contains("hard") -> return F1TyreCompound.HARD
            fullContent.contains("intermediate") || fullContent.contains("inter ") || fullContent.contains("inters") -> return F1TyreCompound.INTERMEDIATE
            fullContent.contains("wet") -> return F1TyreCompound.WET
        }
    }

    return null
}

private fun mapRawTyreToCompound(raw: String): F1TyreCompound? {
    return when (raw.uppercase()) {
        "SOFT", "S" -> F1TyreCompound.SOFT
        "MEDIUM", "M" -> F1TyreCompound.MEDIUM
        "HARD", "H" -> F1TyreCompound.HARD
        "INTERMEDIATE", "INTER", "I" -> F1TyreCompound.INTERMEDIATE
        "WET", "W" -> F1TyreCompound.WET
        else -> null
    }
}

/**
 * Authentic Formula 1 Tyre Graphic (Tread circle + Sidewall ring + Compound code).
 */
@Composable
fun F1TyreGraphic(
    compound: F1TyreCompound,
    modifier: Modifier = Modifier.size(20.dp)
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF191A1E))
            .border(BorderStroke(2.2.dp, compound.color), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.62f)
                .clip(CircleShape)
                .background(Color(0xFF0D0E11)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = compound.code,
                color = compound.color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                fontSize = 8.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Compact Tyre Badge pill displaying the F1 Tyre graphic + compound name.
 */
@Composable
fun F1TyreBadge(
    compound: F1TyreCompound,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = compound.color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, compound.color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 3.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            F1TyreGraphic(compound = compound, modifier = Modifier.size(16.dp))
            Text(
                text = compound.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = if (compound == F1TyreCompound.HARD) MaterialTheme.colorScheme.onSurface else compound.color,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Individual Live Lap-by-Lap Card adapted seamlessly to the Taras app design language.
 */
@Composable
fun LiveTimelineCard(
    item: TimelineItem,
    baseUrl: String?,
    driverTyres: Map<String, String>? = null,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val teamColor = remember(item.teamColor) {
        if (!item.teamColor.isNullOrBlank()) {
            try {
                Color(android.graphics.Color.parseColor(item.teamColor))
            } catch (_: Exception) {
                Color(0xFFE10600)
            }
        } else {
            Color(0xFFE10600)
        }
    }

    val tyreCompound = remember(item, driverTyres) {
        resolveTyreCompound(item, driverTyres)
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            // Left Team Accent Indicator Line (spans full card height dynamically)
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(teamColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Top Meta Row: Lap Badge + Special Event Pill + Tyre Badge + Timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        if (!item.lap.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "LAP ${item.lap}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (!item.driverTla.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = teamColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = item.driverTla,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = teamColor,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        CategoryBadge(item)

                        if (tyreCompound != null) {
                            F1TyreBadge(compound = tyreCompound)
                        }
                    }

                    val timeLabel = item.timestampFormatted
                    if (!timeLabel.isNullOrBlank()) {
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Card Title
                if (!item.title.isNullOrBlank()) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Card Text Body
                if (item.text.isNotBlank()) {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = if (item.isRadio || item.isQuote) FontStyle.Italic else FontStyle.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Expert Quote / Radio Author Badge
                if ((item.isQuote || item.isRadio) && !item.author.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (item.isRadio) Icons.Rounded.Radio else Icons.Rounded.FormatQuote,
                            contentDescription = null,
                            tint = teamColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.author}${if (!item.authorRole.isNullOrBlank()) " • ${item.authorRole}" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = teamColor
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Photo Display with clean styling (no HD tag overlay)
                if (item.hasImage && !item.imageUrl.isNullOrBlank()) {
                    val thumbnailUrl = remember(item.imageUrl, baseUrl) {
                        upgradeF1LiveImageUrl(item.imageUrl, baseUrl, isThumbnail = true)
                    }
                    val masterImageUrl = remember(item.imageUrl, baseUrl) {
                        upgradeF1LiveImageUrl(item.imageUrl, baseUrl, isThumbnail = false)
                    }
                    val imageRequest = remember(thumbnailUrl) {
                        ImageRequest.Builder(context)
                            .data(thumbnailUrl)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .networkCachePolicy(CachePolicy.ENABLED)
                            .build()
                    }

                    var showFullScreenImage by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)), RoundedCornerShape(16.dp))
                            .clickable { showFullScreenImage = true }
                    ) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = item.title ?: "F1 Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (showFullScreenImage && (masterImageUrl != null || thumbnailUrl != null)) {
                        FullScreenImageDialog(
                            imageUrl = masterImageUrl ?: thumbnailUrl!!,
                            title = item.title,
                            subtitle = item.lap ?: item.timestampFormatted,
                            onDismiss = { showFullScreenImage = false }
                        )
                    }
                }

                // Team Radio Audio Player Widget (self-contained state for performance)
                if (item.isRadio && !item.audioUrl.isNullOrBlank()) {
                    AudioPlayerBar(
                        itemId = item.id,
                        audioUrl = item.audioUrl,
                        baseUrl = baseUrl
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(item: TimelineItem) {
    val (label, icon, color) = when {
        item.isRadio -> Triple("RADIO", Icons.Rounded.Radio, Color(0xFF00D2BE))
        item.isQuote -> Triple("QUOTE", Icons.Rounded.FormatQuote, Color(0xFFFF8000))
        item.isFlag -> Triple("RACE CONTROL", Icons.Rounded.Warning, Color(0xFFE10600))
        item.type.name == "INCIDENT" -> Triple("INCIDENT", Icons.Rounded.Warning, Color(0xFFFF9800))
        else -> return
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(BorderStroke(0.5.dp, color.copy(alpha = 0.35f)), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Built-in Audio Player Bar for Team Radio cards with performance-optimized state observation.
 */
@Composable
fun AudioPlayerBar(
    itemId: String,
    audioUrl: String,
    baseUrl: String?,
    modifier: Modifier = Modifier
) {
    val audioManager = remember { F1AudioPlayerManager.instance }
    val currentlyPlayingId by audioManager.currentlyPlayingId.collectAsStateWithLifecycle()
    val playbackState by audioManager.playbackState.collectAsStateWithLifecycle()

    val isPlayingThis = currentlyPlayingId == itemId && playbackState == AudioPlaybackState.PLAYING
    val isBufferingThis = currentlyPlayingId == itemId && playbackState == AudioPlaybackState.BUFFERING

    // Optimized: Only observe progressMs when this card is actually playing
    val progressMs by if (currentlyPlayingId == itemId) {
        audioManager.progressMs.collectAsStateWithLifecycle()
    } else {
        remember { mutableStateOf(0L) }
    }

    val durationMs by if (currentlyPlayingId == itemId) {
        audioManager.durationMs.collectAsStateWithLifecycle()
    } else {
        remember { mutableStateOf(0L) }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { audioManager.playOrPause(itemId, audioUrl, baseUrl) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (isPlayingThis) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlayingThis) "Pause Radio" else "Play Radio",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBufferingThis) "Buffering radio..." else "Team Radio",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val progressSec = progressMs / 1000
                    val durationSec = durationMs / 1000
                    Text(
                        text = "%02d:%02d / %02d:%02d".format(
                            progressSec / 60, progressSec % 60,
                            durationSec / 60, durationSec % 60
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val progressFraction = if (durationMs > 0) {
                    (progressMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

/**
 * Bottom Sheet displaying the complete chronological lap-by-lap history with category filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTimelineBottomSheet(
    items: List<TimelineItem>,
    baseUrl: String?,
    driverTyres: Map<String, String>? = null,
    onDismiss: () -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredItems = remember(items, selectedFilter) {
        when (selectedFilter) {
            "RADIO" -> items.filter { it.isRadio }
            "QUOTES" -> items.filter { it.isQuote }
            "INCIDENTS" -> items.filter { it.isFlag || it.type.name == "INCIDENT" }
            else -> items
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE RACE COMMENTARY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${filteredItems.size} live updates",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close")
                }
            }

            // Filter Chips
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    Triple("ALL", "All Updates", Icons.Rounded.FilterList),
                    Triple("RADIO", "Radios", Icons.Rounded.Radio),
                    Triple("INCIDENTS", "Incidents", Icons.Rounded.Warning),
                    Triple("QUOTES", "Quotes", Icons.Rounded.FormatQuote)
                )
                items(filters) { (id, label, icon) ->
                    val isSelected = selectedFilter == id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = id },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Timeline Items List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    filteredItems,
                    key = { it.id }
                ) { item ->
                    LiveTimelineCard(
                        item = item,
                        baseUrl = baseUrl,
                        driverTyres = driverTyres,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

/**
 * Embedded card section for the latest update from the live webhook,
 * displayed cleanly inside the Singapore Grand Prix session card.
 */
@Composable
fun LiveLatestWebhookUpdateCard(
    item: TimelineItem,
    baseUrl: String?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val teamColor = remember(item.teamColor) {
        if (!item.teamColor.isNullOrBlank()) {
            try {
                Color(android.graphics.Color.parseColor(item.teamColor))
            } catch (_: Exception) {
                Color(0xFFE10600)
            }
        } else {
            Color(0xFFE10600)
        }
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Pulsing Dot + "LATEST UPDATE" + Category Badge + Lap/Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                    )
                    Text(
                        text = "LATEST UPDATE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFFE53935)
                    )
                    CategoryBadge(item)
                }

                val lapOrTime = item.lap ?: item.timestampFormatted
                if (!lapOrTime.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = lapOrTime,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Title
            if (!item.title.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Body text
            if (item.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = if (item.isRadio || item.isQuote) FontStyle.Italic else FontStyle.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expert Quote attribution
            if (item.isQuote && !item.author.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "— ${item.author}${if (!item.authorRole.isNullOrBlank()) ", ${item.authorRole}" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = teamColor
                )
            }

            // In-App Team Radio Audio Player Widget (self-contained state for performance)
            if (item.isRadio && (!item.audioUrl.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(8.dp))
                AudioPlayerBar(
                    itemId = item.id,
                    audioUrl = item.audioUrl,
                    baseUrl = baseUrl
                )
            }
        }
    }
}

/**
 * Compact horizontal card for the live lap-by-lap stream carousel on PaddockScreen.
 */
@Composable
fun LiveTimelineHorizontalCard(
    item: TimelineItem,
    baseUrl: String?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val teamColor = remember(item.teamColor) {
        if (!item.teamColor.isNullOrBlank()) {
            try {
                Color(android.graphics.Color.parseColor(item.teamColor))
            } catch (_: Exception) {
                Color(0xFFE10600)
            }
        } else {
            Color(0xFFE10600)
        }
    }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Category Badge + Lap / Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(item)

                val lapOrTime = item.lap ?: item.timestampFormatted
                if (!lapOrTime.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = lapOrTime,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!item.title.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = if (item.isRadio || item.isQuote) FontStyle.Italic else FontStyle.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.isQuote && !item.author.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "— ${item.author}${if (!item.authorRole.isNullOrBlank()) ", ${item.authorRole}" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = teamColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.isRadio && (!item.audioUrl.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(8.dp))
                AudioPlayerBar(
                    itemId = item.id,
                    audioUrl = item.audioUrl,
                    baseUrl = baseUrl
                )
            }
        }
    }
}

/**
 * Full-screen immersive dialog showing the complete lap-by-lap timeline stream,
 * filterable by category (ALL, RADIO, INCIDENTS, QUOTES), with in-app audio
 * player widgets and tap-to-expand lightbox photos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenLiveTimelineDialog(
    items: List<TimelineItem>,
    baseUrl: String?,
    grandPrixName: String? = null,
    sessionName: String? = null,
    trackStatus: String? = "GREEN",
    leader: String? = null,
    driverTyres: Map<String, String>? = null,
    onDismiss: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredItems = remember(items, selectedFilter) {
        when (selectedFilter) {
            "RADIO" -> items.filter { it.isRadio }
            "QUOTES" -> items.filter { it.isQuote }
            "INCIDENTS" -> items.filter { it.isFlag || it.type.name == "INCIDENT" }
            else -> items
        }
    }

    val (trackText, trackBg, trackFg) = when ((trackStatus ?: "GREEN").uppercase()) {
        "SAFETY_CAR" -> Triple("SAFETY CAR", Color(0xFFFF9800), Color.White)
        "VIRTUAL_SAFETY_CAR" -> Triple("VSC", Color(0xFFFF9800), Color.White)
        "RED_FLAG" -> Triple("RED FLAG", Color(0xFFE53935), Color.White)
        "YELLOW" -> Triple("YELLOW FLAG", Color(0xFFFFEB3B), Color.Black)
        "FINISHED", "CHEQUERED", "CHECKERED" -> Triple("CHEQUERED FLAG", Color(0xFF37474F), Color.White)
        else -> Triple("TRACK CLEAR", Color(0xFF4CAF50), Color.White)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = grandPrixName ?: "Formula 1 Live",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = (sessionName ?: "Live Lap-by-Lap").uppercase() + " • ${items.size} UPDATES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close full-screen timeline"
                            )
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = trackBg,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = trackText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = trackFg,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Dynamic Track Status Alert Banner when condition is not CLEAR
                val statusUpper = (trackStatus ?: "GREEN").uppercase()
                if (statusUpper != "GREEN" && statusUpper != "CLEAR") {
                    val (statusTitle, statusDesc, statusIcon) = when (statusUpper) {
                        "SAFETY_CAR" -> Triple("SAFETY CAR DEPLOYED", "Track neutralized • Physical Safety Car on track • No overtaking", Icons.Rounded.Warning)
                        "VIRTUAL_SAFETY_CAR" -> Triple("VIRTUAL SAFETY CAR (VSC)", "Mandatory delta time in effect • Reduce speed • No overtaking", Icons.Rounded.Warning)
                        "RED_FLAG" -> Triple("SESSION SUSPENDED (RED FLAG)", "Severe hazard on track • All cars entering pit lane", Icons.Rounded.Flag)
                        "YELLOW" -> Triple("YELLOW FLAG IN SECTOR", "Caution on track • Hazard ahead • Reduce speed", Icons.Rounded.Warning)
                        "FINISHED", "CHEQUERED", "CHECKERED" -> Triple("CHEQUERED FLAG", "Session finished • Results finalized", Icons.Rounded.SportsScore)
                        else -> Triple("TRACK CAUTION", "Caution conditions in effect on circuit", Icons.Rounded.Info)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = trackBg.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, trackBg.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = if (trackBg == Color(0xFFFFEB3B)) Color(0xFFE65100) else trackBg,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = statusTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = if (trackBg == Color(0xFFFFEB3B)) Color(0xFFE65100) else trackBg
                                )
                                Text(
                                    text = statusDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Filter Chips Row with Native Material Vector Icons
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        Triple("ALL", "All (${items.size})", Icons.Rounded.FilterList),
                        Triple("RADIO", "Radios (${items.count { it.isRadio }})", Icons.Rounded.Radio),
                        Triple("INCIDENTS", "Incidents (${items.count { it.isFlag || it.type.name == "INCIDENT" }})", Icons.Rounded.Warning),
                        Triple("QUOTES", "Quotes (${items.count { it.isQuote }})", Icons.Rounded.FormatQuote)
                    )
                    items(filters) { (id, label, icon) ->
                        val isSelected = selectedFilter == id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = id },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (items.isEmpty()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Connecting to Live Lap-by-Lap Stream...",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Awaiting race telemetry, incidents, and team radio from trackside.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = "No updates available for this filter",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            filteredItems,
                            key = { it.id }
                        ) { item ->
                            LiveTimelineCard(
                                item = item,
                                baseUrl = baseUrl,
                                driverTyres = driverTyres,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen immersive lightbox dialog for trackside and race commentary photos.
 * Provides uncropped, crystal-clear high-definition view with dark backdrop.
 */
@Composable
fun FullScreenImageDialog(
    imageUrl: String,
    title: String?,
    subtitle: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val fullScreenRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .size(Size.ORIGINAL)
            .build()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.96f))
                .clickable { onDismiss() }
        ) {
            // Main High-Res Image
            AsyncImage(
                model = fullScreenRequest,
                contentDescription = title ?: "Full Screen F1 Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 64.dp),
                contentScale = ContentScale.Fit
            )

            // Top Bar with Close Button and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!title.isNullOrBlank()) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Transforms and upgrades Formula 1, Monterosa, and motorsport live stream image URLs
 * to their highest quality master resolution (1080p / 1920p / 4K), bypassing compressed thumbnails,
 * low-DPI view constraints, and mobile web downscaling.
 */
fun upgradeF1LiveImageUrl(rawUrl: String?, baseUrl: String? = null, isThumbnail: Boolean = false): String? {
    if (rawUrl.isNullOrBlank()) return null

    // 1. Resolve relative URLs (e.g. /assets/..., assets/...)
    var url = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
        rawUrl
    } else {
        val base = baseUrl?.trimEnd('/') ?: ""
        if (base.isNotBlank()) {
            if (rawUrl.startsWith("/")) "$base$rawUrl" else "$base/$rawUrl"
        } else {
            rawUrl
        }
    }

    val targetWidth = if (isThumbnail) "960" else "1920"
    val targetQuality = if (isThumbnail) "q_auto:good" else "q_auto:best"

    // 2. Cloudinary / Formula 1 Official Media CDN (media.formula1.com & res.cloudinary.com)
    if (url.contains("formula1.com/image/upload/") || (url.contains("cloudinary.com/") && url.contains("/image/upload/"))) {
        val cloudinaryUploadRegex = Regex("""image/upload/(?:(?:c_[^/]+|w_\d+|h_\d+|q_[^/]+|dpr_[^/]+|f_[^/]+|t_[^/]+)[,/]?)+/""")
        url = if (cloudinaryUploadRegex.containsMatchIn(url)) {
            url.replace(cloudinaryUploadRegex, "image/upload/c_limit,w_$targetWidth,$targetQuality,f_auto/")
        } else {
            url.replace("image/upload/", "image/upload/c_limit,w_$targetWidth,$targetQuality,f_auto/")
        }
    }

    // 3. Monterosa Cloud CDN / Interaction Cloud CDN
    if (url.contains("monterosa.cloud") || url.contains("interactioncloud.formula1.com")) {
        url = if (isThumbnail) {
            url.replace("/thumbnail/", "/medium/")
                .replace("/small/", "/medium/")
                .replace(Regex("""[?&](?:w|width)=\d+"""), "?width=960")
        } else {
            url.replace("/thumbnail/", "/original/")
                .replace("/small/", "/original/")
                .replace("/medium/", "/large/")
                .replace(Regex("""[?&](?:w|width)=\d+"""), "?width=1920")
        }
        url = url.replace(Regex("""[?&](?:h|height)=\d+"""), "")
            .replace(Regex("""[?&]scale=\d+"""), "")
    }

    // 4. Query Parameter Dimensions (Fastly, Akamai, Cloudflare image resizing)
    if (url.contains("?")) {
        url = url.replace(Regex("""([?&](?:w|width)=)\d+"""), "$1$targetWidth")
            .replace(Regex("""([?&](?:q|quality)=)\d+"""), "$1" + (if (isThumbnail) "80" else "100"))
            .replace(Regex("""[&?](?:h|height)=\d+"""), "")
    }

    // 5. WordPress / Editorial News CMS Thumbnail Suffixes (e.g. -300x200.jpg, -768x432.webp)
    url = url.replace(Regex("""-\d+x\d+(\.[a-zA-Z0-9]+)$"""), "$1")

    return url
}

