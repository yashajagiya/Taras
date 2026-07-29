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
import androidx.compose.material3.HorizontalDivider
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
import com.example.taras.core.helpercore.toGetDate
import com.example.taras.core.helpercore.toGetMonths
import com.example.taras.core.helpercore.toMonthes // Note: Consider renaming this typo in your helpercore to toMonths()
import com.example.taras.viewmodel.CurrentRound
import com.example.taras.viewmodel.RaceClearData
import kotlinx.collections.immutable.ImmutableList

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

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = racesList1,
            key = { _, race -> race.circuitId }
        ) { index, race ->
            val isCurrentRace = race.roundNumber == currentRound

            if (isCurrentRace) {
                CurrentRaceExtended(
                    index = index,
                    race = race,
                    onCircuitClick = onCircuitClick
                )
            } else {
                CollapsedRaceCard(
                    index = index,
                    race = race,
                    onCircuitClick = onCircuitClick
                )
            }
        }
    }
}

@Composable
fun CollapsedRaceCard(
    index: Int,
    race: RaceClearData,
    onCircuitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "*",
            modifier = Modifier
                .weight(0.3f)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primary),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 5.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4.7f)
                .clickable { onCircuitClick(race.circuitId) }
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .7f)
                    )
                    .padding(bottom = if (race.name.isNotEmpty()) 12.dp else 0.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "R${index + 1}",
                        modifier = Modifier
                            .weight(0.9f)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
//                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier
                            .weight(3.2f)
                            .padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = race.gpName,
                            color = Color.Black.copy(alpha = .9f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            overflow = TextOverflow.Clip
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(.8f),
                            thickness = 2.dp
                        )
                        Text(
                            text = race.circuitName,
                            color = Color.Black.copy(alpha = .7f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Clip
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.9f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = race.race.date.toString().toGetMonths().toMonthes(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
//                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Clip,
                            maxLines = 2
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(.7f),
                            thickness = 2.dp
                        )
                        Text(
                            text = race.race.date.toString().toGetDate(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge,
//                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Clip,
                            maxLines = 2

                        )
                    }
                }

                if (race.name.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "🏆 ${race.name}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
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
            .clickable { onCircuitClick(race.circuitId) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = race.trackImage.ifEmpty { "https://f1tv.formula1.com/static/favicon.ico" },
                contentDescription = "map",
                modifier = Modifier
                    .matchParentSize()
                    .padding(24.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.1f
            )

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEXT RACE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = race.gpName.ifBlank { race.raceName.ifBlank { "Upcoming Grand Prix" } },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 28.sp
                        )
                        if (race.gpName.isNotBlank() && race.raceName.isNotBlank() && race.gpName != race.raceName) {
                            Text(
                                text = race.raceName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "R${index + 1}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(
                        modifier = Modifier.width(40.dp),
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = race.circuitName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = race.race.date.toString().toGetMonths().toMonthes(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = race.race.date.toString().toGetDate(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "SCHEDULE",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
            }
        }
    }
}


        //@Composable
        //fun WeekendSchedule(modifier: Modifier = Modifier) {
        //    Card(modifier = modifier.fillMaxSize()) {
        //        Column(modifier = Modifier.padding(16.dp)) { // Added padding so text doesn't hit edges
        //            Text(
        //                text = "CHAMPIONSHIP",
        //                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
        //                style = MaterialTheme.typography.labelSmall,
        //                fontWeight = FontWeight.Bold,
        //                letterSpacing = 1.sp
        //            )
        //        }
        //    }
        //}