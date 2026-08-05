package com.example.taras.view.subview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.taras.core.helpercore.formatToLocalSession
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import com.example.taras.core.helpercore.toGetDate
import com.example.taras.core.helpercore.toGetMonths
import com.example.taras.core.helpercore.toMonthes
import com.example.taras.viewmodel.CurrentRound
import com.example.taras.viewmodel.RaceClearData
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.taras.viewmodel.SessionTime
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace
import androidx.compose.ui.text.font.FontStyle
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault

@Composable
fun RacesCards(
    racesList1: ImmutableList<RaceClearData>?,
    racesCurrentState: CurrentRound?,
    onCircuitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (racesList1 == null || racesCurrentState == null) return

    val currentRound = racesCurrentState.roundNumber
    val listState = rememberLazyListState()

    val currentIndex = remember(racesList1, racesCurrentState) {
        racesList1.indexOfFirst { it.roundNumber == currentRound }.coerceAtLeast(0)
    }

    LaunchedEffect(currentIndex) {
        if (currentIndex > 0) {
            listState.scrollToItem(
                index = currentIndex, scrollOffset = -80
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(start = 35.dp)
                .fillMaxHeight()
                .width(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            itemsIndexed(
                items = racesList1,
                key = { _, race -> race.circuitId }
            ) { index, race ->
                val isCurrentRace = race.roundNumber == currentRound
                val isPastRace = race.roundNumber < currentRound

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .padding(top = 28.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrentRace) MaterialTheme.colorScheme.primary
                                    else if (isPastRace) MaterialTheme.colorScheme.surfaceVariant
                                    else Color.White
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isCurrentRace) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                        )
                    }

                    if (isCurrentRace) {
                        CurrentRaceExtended(
                            index = index,
                            race = race,
                            onCircuitClick = onCircuitClick,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        CollapsedRaceCard(
                            index = index,
                            race = race,
                            onCircuitClick = onCircuitClick,
                            isPast = isPastRace,
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer(alpha = if (isPastRace) 1f else 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollapsedRaceCard(
    index: Int,
    race: RaceClearData,
    onCircuitClick: (String) -> Unit,
    isPast: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCircuitClick(race.circuitId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "R${index + 1}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = race.gpName,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = race.circuitName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val localRaceDateTime = remember(race.race) {
                        parseSessionTimeToInstant(race.race.date, race.race.time)
                            ?.toLocalDateTime(currentSystemDefault())
                    }
                    
                    Text(
                        text = localRaceDateTime?.month?.name?.take(3)?.uppercase()
                            ?: race.race.date.toString().toGetMonths().toMonthes().uppercase(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Monospace
                    )
                    Text(
                        text = localRaceDateTime?.dayOfMonth?.toString()
                            ?: race.race.date.toString().toGetDate(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Monospace
                    )
                }
            }

            if (isPast && race.winnerName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏆", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = race.winnerName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentRaceExtended(
    index: Int,
    race: RaceClearData,
    onCircuitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCircuitClick(race.circuitId) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            AsyncImage(
                model = race.trackImage.ifEmpty { "https://f1tv.formula1.com/static/favicon.ico" },
                contentDescription = "track",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "R${index + 1}",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Monospace
                            )
                        }
                        Text(
                            text = "CIRCUIT EXPLORER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upcoming",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                val nameParts = race.gpName.split(" ")
                val firstName = nameParts.firstOrNull() ?: ""
                val remainingName = remember { nameParts.drop(1).joinToString(" ") }

                Column {
                    Text(
                        text = firstName.uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = remainingName.uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 44.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = race.circuitName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "WEEKEND SCHEDULE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            letterSpacing = 2.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val isSprint = race.fp2.date == null && race.fp3.date == null
                        val sessions = if (isSprint) {
                            listOf(
                                "FP1" to race.fp1,
                                "SPRINT QUALY" to race.sprintQualy,
                                "SPRINT RACE" to race.sprintRace,
                                "QUALI" to race.qualy
                            )
                        } else {
                            listOf(
                                "FP1" to race.fp1,
                                "FP2" to race.fp2,
                                "FP3" to race.fp3,
                                "QUALI" to race.qualy
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SessionItem(
                                    sessions[0].first,
                                    sessions[0].second,
                                    modifier = Modifier.weight(1f)
                                )
                                SessionItem(
                                    sessions[1].first,
                                    sessions[1].second,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SessionItem(
                                    sessions[2].first,
                                    sessions[2].second,
                                    modifier = Modifier.weight(1f)
                                )
                                SessionItem(
                                    sessions[3].first,
                                    sessions[3].second,
                                    modifier = Modifier.weight(1f),
                                    isHighlight = true,

                                    )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onCircuitClick(race.circuitId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = "FULL RACE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = formatToLocalSession(race.race.date, race.race.time),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionItem(
    name: String,
    session: SessionTime,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.6f
            ),
            fontFamily = Monospace
        )
        Text(
            text = formatToLocalSession(session.date, session.time),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}


