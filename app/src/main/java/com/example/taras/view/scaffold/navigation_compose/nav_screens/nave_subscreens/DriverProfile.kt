package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.network_calls.taras.model.DriverPerRace
import com.example.taras.network_calls.taras.model.Quote
import com.example.taras.viewmodel.DriverDetailUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.DriversViewModelFactory
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.LineChartDefaults
import kotlinx.collections.immutable.ImmutableList

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun DriverProfile(
    driverNumber: String,
    modifier: Modifier = Modifier,
    driversViewModel: DriversViewModel = viewModel(
        factory = DriversViewModelFactory(AppDatabase.getDatabase(LocalContext.current).topThreeDriversDao())
    )
) {
    val driverDetailsState by driversViewModel.combinedDetailedDrivers.collectAsStateWithLifecycle()

    DriverProfileContent(
        driverNumber = driverNumber,
        driverDetailsState = driverDetailsState,
        onRefresh = { driversViewModel.fetchDriverData() },
        modifier = modifier
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun DriverProfileContent(
    driverNumber: String,
    onRefresh: () -> Unit,
    driverDetailsState: UiState<ImmutableList<DriverDetailUiModel>>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        var isRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()

        LaunchedEffect(driverDetailsState, driverNumber) {
            if (driverDetailsState !is UiState.Loading && driverNumber.isNotBlank()) {
                isRefreshing = false
            } else if (driverDetailsState is UiState.Error && driverNumber.isEmpty()) {

                isRefreshing = false
            }
        }


        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
            isRefreshing = true
            onRefresh()
        }, state = pullToRefreshState, indicator = {
            if (!isRefreshing) {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = false,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }) {
            Surface(color = Color.Transparent) {
                val isAnyLoading = driverDetailsState is UiState.Loading

                val isAnyError = driverDetailsState is UiState.Error || driverNumber.isEmpty()


                if (!isAnyError && isAnyLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoadingIndicator()
                        Spacer(Modifier.requiredHeight(30.dp))
                        Text("Loading...")
                    }

                } else if (isAnyError && !isRefreshing) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Something went wrong",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("Retry Again")
                        }
                    }
                } else {
                    val driverDetails = (driverDetailsState as? UiState.Success)?.data
                    val driver = driverDetails?.find { it.driverNumber == driverNumber }
                    if (driver == null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Driver not found",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item{
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = driver.teamColor.toComposeColor()
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            AsyncImage(model = driver.carNumberImage.takeIf { !it.isNullOrEmpty() }
                                                ?: "https://f1tv.formula1.com/static/favicon.ico",
                                                contentDescription = "Car Number",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(16.dp),
                                                alignment = Alignment.Center,
                                                contentScale = ContentScale.Fit,
                                                alpha = 0.4f)

                                            AsyncImage(model = driver.headshotUrl.takeIf { !it.isNullOrEmpty() }
                                                ?: "https://f1tv.formula1.com/static/favicon.ico",
                                                contentDescription = "Driver Image",
                                                modifier = Modifier.size(400.dp),
                                                alignment = Alignment.TopCenter,
                                                contentScale = ContentScale.Crop)
                                        }
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            bottom = 16.dp, start = 16.dp, end = 16.dp
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = driver.teamColor.toComposeColor()
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(
                                        bottomStart = 28.dp,
                                        bottomEnd = 28.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 8.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = driver.fullName,
                                            maxLines = 2,
                                            fontSize = 36.sp,
                                            letterSpacing = 1.sp,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Flag,
                                                    contentDescription = "Country",
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.padding(4.dp))
                                                Text(
                                                    text = driver.country,
                                                    fontSize = 16.sp,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White
                                                )
                                            }
                                            Text(
                                                text = driver.teamName,
                                                fontSize = 16.sp,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    )
//                                    elevation = CardDefaults.cardElevation(
//                                        defaultElevation = 4.dp
//                                    )
                                )
                                {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        DriverStatItem(
                                            label = "RANK",
                                            value = "P${driver.rank}"
                                        )
                                        VerticalDivider(
                                            modifier = Modifier.padding(8.dp),
                                            thickness = 3.dp,
                                            color = Color.Black.copy(alpha = .7f)
                                        )
                                        DriverStatItem(
                                            label = "CODE",
                                            value = driver.abbreviation
                                        )
                                        VerticalDivider(
                                            modifier = Modifier.padding(8.dp),
                                            thickness = 3.dp,
                                            color = Color.Black.copy(alpha = .7f)
                                        )
                                        DriverStatItem(
                                            label = "POINTS",
                                            value = driver.championshipPoints.toString()
                                        )
                                    }
                                }
                            }
                            item {
                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {
                                    Text(
                                        text = "2026 Performance",
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = Color.Black,
                                        textAlign = TextAlign.Start,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    val stats = driver.seasonStats
                                    val performanceStats = listOf(
                                        "Grand Prix Races" to (stats?.grandPrixRaces ?: "0"),
                                        "Wins" to (stats?.grandPrixWins ?: "0"),
                                        "Poles" to (stats?.grandPrixPoles ?: "0"),
                                        "Podiums" to (stats?.grandPrixPodiums ?: "0"),
                                        "Fastest Laps" to (stats?.dhlFastestLaps ?: "0"),
                                        "Top 10s" to (stats?.grandPrixTop10s ?: "0"),
                                        "DNF" to (stats?.dnfs ?: "0"),
                                        "Sprint Races" to (stats?.sprintRaces ?: "0"),
                                        "Sprint Wins" to (stats?.sprintWins ?: "0"),
                                        "Sprint Podiums" to (stats?.sprintPodiums ?: "0"),
                                        "Sprint Poles" to (stats?.sprintPodiums ?: "0")
                                    )

                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        performanceStats.forEach { (label, value) ->
                                            StateMiniCard(
                                                label = label,
                                                value = value,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .defaultMinSize(minWidth = 100.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(24.dp))
                                }
                            }

                            item {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {
                                    Text(
                                        text = "Career Stats",
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = Color.Black,
                                        textAlign = TextAlign.Start,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    driver.careerStats?.worldChampionships?.toIntOrNull()?.let {
                                        if (it > 0) {
                                            CareerStatsCard(
                                                driver.careerStats.worldChampionships,
                                                "World Championships",
                                                Icons.Default.EmojiEvents,
                                                MaterialTheme.colorScheme.onPrimary,
                                                MaterialTheme.colorScheme.onPrimary,
                                                MaterialTheme.colorScheme.onPrimary,
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            )
                                            Spacer(
                                                modifier = Modifier
                                                    .height(16.dp)
                                                    .fillMaxWidth()
                                            )
                                        }
                                    }
                                    CareerStatsCard(
                                        driver.careerStats?.highestRaceFinish ?: "0",
                                        "Grand Prix Wins",
                                        Icons.Default.FlagCircle,
                                        MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                                        MaterialTheme.colorScheme.primary,
                                        Color.Black,
                                        MaterialTheme.colorScheme.surfaceContainer
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(16.dp)
                                            .fillMaxWidth()
                                    )
                                    CareerStatsCard(
                                        driver.careerStats?.podiums ?: "0",
                                        "Podium Finishes",
                                        Icons.Default.BarChart,
                                        MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                                        MaterialTheme.colorScheme.primary,
                                        Color.Black,
                                        MaterialTheme.colorScheme.surfaceContainer
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(16.dp)
                                            .fillMaxWidth()
                                    )

                                    Row {
                                        CareerStatsMiniCard(
                                            title = "GP Entered",
                                            value = driver.careerStats?.grandsPrixEntered ?: "0",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        CareerStatsMiniCard(
                                            title = "Pole",
                                            value = driver.careerStats?.polePositions ?: "0",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .height(16.dp)
                                            .fillMaxWidth()
                                    )

                                    Row {
                                        CareerStatsMiniCard(
                                            title = "Career Points",
                                            value = driver.careerStats?.careerPoints ?: "0",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        CareerStatsMiniCard(
                                            title = "DNFs",
                                            value = driver.careerStats?.dnfs ?: "0",
                                            modifier = Modifier.weight(1f)
                                        )

                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Recent Form",
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = Color.Black,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ChartPerRace(
                                        driver.races,
                                        driver.championshipPoints.toString()
                                    )


                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Biography",
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = Color.Black,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    )
                                    BioCard(
                                        driver.bioText,
                                        driver.quote,
                                        driver.dateOfBirth,
                                        driver.placeOfBirth
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
fun ChartPerRace(
    perRace: ImmutableList<DriverPerRace>,
    points: String
) {

    val dataSet = remember(perRace, points) {
        perRace.map { it.value }.toChartDataSet(
            title = "Points Progression +$points",
            labels = perRace.map { it.name }
        )
    }
    LineChart(
        dataSet = dataSet,
        style = LineChartDefaults.style(
            pointVisible = true,
            pointColor = MaterialTheme.colorScheme.primary,
            pointSize = 8f,
            xAxisLabelsVisible = true,
            yAxisLabelsVisible = true,
            xAxisLabelMaxCount = perRace.size
        )
    )
}


@Composable
fun CareerStatsCard(
    count: String,
    title: String,
    icon: ImageVector,
    imageColor: Color,
    valueColor: Color,
    titleColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = "image",
                    tint = imageColor,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                        .padding(16.dp)
                )

            }
            Column(
                modifier = Modifier
                    .weight(2f),
            ) {
                Text(
                    text = count,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor,
                    fontSize = 45.sp,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp,
                    maxLines = 2
                )
            }
        }
    }
}


@Composable
fun CareerStatsMiniCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StateMiniCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DriverStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BioCard(
    bioText: ImmutableList<String>,
    quoteText: Quote?,
    birthDate: String,
    birthPlace: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(24.dp)
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 4.dp
//        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            bioText.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            BirthPlaceDate("Born", birthDate)
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(thickness = 2.dp, color = Color.Gray.copy(alpha = .7f))
            Spacer(Modifier.height(16.dp))
            BirthPlaceDate("Birthplace", birthPlace)
            Spacer(Modifier.height(16.dp))

            if (quoteText != null && quoteText.text.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = .2f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .2f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "\"${quoteText.text}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        if (quoteText.author.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "- ${quoteText.author}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }


        }
    }
}

@Composable
fun BirthPlaceDate(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )

    }

}