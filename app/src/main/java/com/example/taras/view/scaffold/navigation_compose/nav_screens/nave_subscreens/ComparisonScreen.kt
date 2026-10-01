package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.luminance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.core.common.UiState
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.viewmodel.DetailedTeamUiModel
import com.example.taras.viewmodel.DriverDetailUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.DriversViewModelFactory
import com.example.taras.viewmodel.TeamsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ComparisonScreen(
    initialDriver1: String = "",
    initialDriver2: String = "",
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    driversViewModel: DriversViewModel = viewModel(
        factory = DriversViewModelFactory(AppDatabase.getDatabase(LocalContext.current).topThreeDriversDao())
    ),
    teamsViewModel: TeamsViewModel = viewModel()
) {
    val driversState by driversViewModel.combinedDetailedDrivers.collectAsStateWithLifecycle()
    val teamsState by teamsViewModel.combinedDetailedTeams.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Drivers", "Teams & Cars")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar & Tabs - matching GridScreen.kt visual structure exactly
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.weight(1f)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (selectedTabIndex == 0) {
                when (val state = driversState) {
                    is UiState.Loading -> LoadingView()
                    is UiState.Error -> ErrorView("Could not load drivers: ${state.message}")
                    is UiState.Success -> {
                        val drivers = state.data
                        if (drivers.size >= 2) {
                            DriverComparisonView(
                                drivers = drivers,
                                initialDriver1 = initialDriver1,
                                initialDriver2 = initialDriver2
                            )
                        } else {
                            EmptyComparisonView("Not enough driver data available")
                        }
                    }
                }
            } else {
                when (val state = teamsState) {
                    is UiState.Loading -> LoadingView()
                    is UiState.Error -> ErrorView("Could not load teams: ${state.message}")
                    is UiState.Success -> {
                        val teams = state.data
                        if (teams.size >= 2) {
                            TeamComparisonView(teams = teams)
                        } else {
                            EmptyComparisonView("Not enough team data available")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// DRIVER COMPARISON VIEW
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverComparisonView(
    drivers: List<DriverDetailUiModel>,
    initialDriver1: String,
    initialDriver2: String
) {
    // Sort drivers by championship rank: P1 is index 0 (#1 in list), P2 is index 1 (#2 in list)
    val sortedDrivers = remember(drivers) {
        drivers.sortedWith(
            compareBy<DriverDetailUiModel> { if (it.rank > 0) it.rank else 999 }
                .thenByDescending { it.championshipPoints }
                .thenBy { it.fullName }
        )
    }

    var selectedDriver1Number by rememberSaveable {
        mutableStateOf(
            if (initialDriver1.isNotBlank()) initialDriver1
            else sortedDrivers.getOrNull(0)?.driverNumber.orEmpty()
        )
    }

    var selectedDriver2Number by rememberSaveable {
        mutableStateOf(
            if (initialDriver2.isNotBlank() && initialDriver2 != initialDriver1) initialDriver2
            else sortedDrivers.getOrNull(1)?.driverNumber
                ?: sortedDrivers.getOrNull(0)?.driverNumber.orEmpty()
        )
    }

    val selectedDriver1 = remember(selectedDriver1Number, sortedDrivers) {
        sortedDrivers.find {
            it.driverNumber == selectedDriver1Number ||
            it.fullName.contains(selectedDriver1Number, ignoreCase = true) ||
            it.lastName.contains(selectedDriver1Number, ignoreCase = true)
        } ?: sortedDrivers[0]
    }

    val selectedDriver2 = remember(selectedDriver2Number, sortedDrivers) {
        sortedDrivers.find {
            it.driverNumber == selectedDriver2Number ||
            it.fullName.contains(selectedDriver2Number, ignoreCase = true) ||
            it.lastName.contains(selectedDriver2Number, ignoreCase = true)
        } ?: sortedDrivers.getOrElse(1) { sortedDrivers[0] }
    }

    var showPicker1 by remember { mutableStateOf(false) }
    var showPicker2 by remember { mutableStateOf(false) }

    val isSameTeam = selectedDriver1.teamName.equals(selectedDriver2.teamName, ignoreCase = true)

    val baseColor1 = selectedDriver1.teamColor.toComposeColor()
    val baseColor2 = selectedDriver2.teamColor.toComposeColor()

    // Authentic team colors: when teammates are compared, both get authentic team accents
    val (d1Color, d2Color) = if (isSameTeam) {
        getTeammateColors(selectedDriver1.teamName, baseColor1)
    } else {
        Pair(baseColor1, baseColor2)
    }

    if (showPicker1) {
        DriverSelectionSheet(
            title = "Select Driver 1",
            drivers = sortedDrivers,
            currentSelected = selectedDriver1,
            excludedDriverNumber = selectedDriver2.driverNumber,
            onSelect = {
                selectedDriver1Number = it.driverNumber
                showPicker1 = false
            },
            onDismiss = { showPicker1 = false }
        )
    }

    if (showPicker2) {
        DriverSelectionSheet(
            title = "Select Driver 2",
            drivers = sortedDrivers,
            currentSelected = selectedDriver2,
            excludedDriverNumber = selectedDriver1.driverNumber,
            onSelect = {
                selectedDriver2Number = it.driverNumber
                showPicker2 = false
            },
            onDismiss = { showPicker2 = false }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Driver Hero Card
        item(contentType = "Hero") {
            DriverVersusHero(
                driver1 = selectedDriver1,
                driver2 = selectedDriver2,
                color1 = d1Color,
                color2 = d2Color,
                isSameTeam = isSameTeam,
                onDriver1Click = { showPicker1 = true },
                onDriver2Click = { showPicker2 = true },
                onSwap = {
                    val temp = selectedDriver1Number
                    selectedDriver1Number = selectedDriver2Number
                    selectedDriver2Number = temp
                }
            )
        }

        // Section: 2026 Season Performance
        item(contentType = "SectionHeader") {
            SectionHeader(title = "SEASON PERFORMANCE", subtitle = "2026 World Championship")
        }

        val d1Stats = selectedDriver1.seasonStats
        val d2Stats = selectedDriver2.seasonStats

        item(contentType = "StatCard") {
            ComparisonMetricCard(
                label = "Championship Points",
                val1 = selectedDriver1.championshipPoints.toFloat(),
                val2 = selectedDriver2.championshipPoints.toFloat(),
                display1 = selectedDriver1.championshipPointsDisplay,
                display2 = selectedDriver2.championshipPointsDisplay,
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "PTS"
            )
        }

        item(contentType = "StatCard") {
            ComparisonMetricCard(
                label = "Standings Rank",
                val1 = (100 - selectedDriver1.rank).toFloat(),
                val2 = (100 - selectedDriver2.rank).toFloat(),
                display1 = "P${selectedDriver1.rank}",
                display2 = "P${selectedDriver2.rank}",
                color1 = d1Color,
                color2 = d2Color,
                lowerIsBetter = true,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "POS"
            )
        }

        item(contentType = "StatCard") {
            val wins1 = d1Stats?.grandPrixWins?.toFloatOrNull() ?: 0f
            val wins2 = d2Stats?.grandPrixWins?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Grand Prix Wins",
                val1 = wins1,
                val2 = wins2,
                display1 = d1Stats?.grandPrixWins ?: "0",
                display2 = d2Stats?.grandPrixWins ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "WINS"
            )
        }

        item(contentType = "StatCard") {
            val pod1 = d1Stats?.grandPrixPodiums?.toFloatOrNull() ?: 0f
            val pod2 = d2Stats?.grandPrixPodiums?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Podiums",
                val1 = pod1,
                val2 = pod2,
                display1 = d1Stats?.grandPrixPodiums ?: "0",
                display2 = d2Stats?.grandPrixPodiums ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "POD"
            )
        }

        item(contentType = "StatCard") {
            val pole1 = d1Stats?.grandPrixPoles?.toFloatOrNull() ?: 0f
            val pole2 = d2Stats?.grandPrixPoles?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Pole Positions",
                val1 = pole1,
                val2 = pole2,
                display1 = d1Stats?.grandPrixPoles ?: "0",
                display2 = d2Stats?.grandPrixPoles ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "POLES"
            )
        }

        item(contentType = "StatCard") {
            val fast1 = d1Stats?.dhlFastestLaps?.toFloatOrNull() ?: 0f
            val fast2 = d2Stats?.dhlFastestLaps?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Fastest Laps",
                val1 = fast1,
                val2 = fast2,
                display1 = d1Stats?.dhlFastestLaps ?: "0",
                display2 = d2Stats?.dhlFastestLaps ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "LAPS"
            )
        }

        item(contentType = "StatCard") {
            val top101 = d1Stats?.grandPrixTop10s?.toFloatOrNull() ?: 0f
            val top102 = d2Stats?.grandPrixTop10s?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Top 10 Finishes",
                val1 = top101,
                val2 = top102,
                display1 = d1Stats?.grandPrixTop10s ?: "0",
                display2 = d2Stats?.grandPrixTop10s ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "TOP 10"
            )
        }

        item(contentType = "StatCard") {
            val sprint1 = d1Stats?.sprintPoints?.toFloatOrNull() ?: 0f
            val sprint2 = d2Stats?.sprintPoints?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Sprint Points",
                val1 = sprint1,
                val2 = sprint2,
                display1 = d1Stats?.sprintPoints ?: "0",
                display2 = d2Stats?.sprintPoints ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "PTS"
            )
        }

        // Section: Career Totals
        item(contentType = "SectionHeader") {
            SectionHeader(title = "CAREER HIGHLIGHTS", subtitle = "All-Time Historic Records")
        }

        val c1 = selectedDriver1.careerStats
        val c2 = selectedDriver2.careerStats

        item(contentType = "StatCard") {
            val champ1 = c1?.worldChampionships?.toFloatOrNull() ?: 0f
            val champ2 = c2?.worldChampionships?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "World Championships",
                val1 = champ1,
                val2 = champ2,
                display1 = c1?.worldChampionships ?: "0",
                display2 = c2?.worldChampionships ?: "0",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "TITLES"
            )
        }

        item(contentType = "StatCard") {
            val races1 = c1?.grandsPrixEntered?.toFloatOrNull() ?: 0f
            val races2 = c2?.grandsPrixEntered?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Grand Prix Entries",
                val1 = races1,
                val2 = races2,
                display1 = c1?.grandsPrixEntered ?: "N/A",
                display2 = c2?.grandsPrixEntered ?: "N/A",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation,
                unit = "RACES"
            )
        }

        // Section: Driver Profile & Biometrics
        item(contentType = "SectionHeader") {
            SectionHeader(title = "PROFILE & BIOMETRICS", subtitle = "Driver Details & Background")
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Nationality",
                icon = Icons.Default.Flag,
                val1 = selectedDriver1.country.ifBlank { selectedDriver1.nationality },
                val2 = selectedDriver2.country.ifBlank { selectedDriver2.nationality },
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Age & Birthday",
                icon = Icons.Default.Person,
                val1 = formatBirthDateAndAge(selectedDriver1.dateOfBirth),
                val2 = formatBirthDateAndAge(selectedDriver2.dateOfBirth),
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Permanent Number",
                icon = Icons.Default.DirectionsCar,
                val1 = "#${selectedDriver1.driverNumber}",
                val2 = "#${selectedDriver2.driverNumber}",
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Constructor Team",
                icon = Icons.Default.Groups,
                val1 = selectedDriver1.teamName,
                val2 = selectedDriver2.teamName,
                color1 = d1Color,
                color2 = d2Color,
                tag1 = selectedDriver1.abbreviation,
                tag2 = selectedDriver2.abbreviation
            )
        }

        if (selectedDriver1.placeOfBirth.isNotBlank() || selectedDriver2.placeOfBirth.isNotBlank()) {
            item(contentType = "SpecCard") {
                PartitionedComparisonSpecCard(
                    title = "Place of Birth",
                    icon = Icons.Default.LocationOn,
                    val1 = selectedDriver1.placeOfBirth,
                    val2 = selectedDriver2.placeOfBirth,
                    color1 = d1Color,
                    color2 = d2Color,
                    tag1 = selectedDriver1.abbreviation,
                    tag2 = selectedDriver2.abbreviation
                )
            }
        }

        item(contentType = "Spacer") {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =========================================================================
// TEAM & CAR COMPARISON VIEW
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamComparisonView(teams: List<DetailedTeamUiModel>) {
    // Sort teams by constructor rank: P1 is index 0 (#1 in list), P2 is index 1 (#2 in list)
    val sortedTeams = remember(teams) {
        teams.sortedWith(
            compareBy<DetailedTeamUiModel> { if (it.rank > 0) it.rank else 999 }
                .thenByDescending { it.currentPoints }
                .thenBy { it.teamName }
        )
    }

    var selectedTeam1Name by rememberSaveable {
        mutableStateOf(sortedTeams.getOrNull(0)?.teamName.orEmpty())
    }

    var selectedTeam2Name by rememberSaveable {
        mutableStateOf(
            sortedTeams.getOrNull(1)?.teamName
                ?: sortedTeams.getOrNull(0)?.teamName.orEmpty()
        )
    }

    val selectedTeam1 = remember(selectedTeam1Name, sortedTeams) {
        sortedTeams.find { it.teamName.equals(selectedTeam1Name, ignoreCase = true) }
            ?: sortedTeams[0]
    }

    val selectedTeam2 = remember(selectedTeam2Name, sortedTeams) {
        sortedTeams.find { it.teamName.equals(selectedTeam2Name, ignoreCase = true) }
            ?: sortedTeams.getOrElse(1) { sortedTeams[0] }
    }

    var showPicker1 by remember { mutableStateOf(false) }
    var showPicker2 by remember { mutableStateOf(false) }

    val t1Color = selectedTeam1.teamColor.toComposeColor()
    val t2Color = selectedTeam2.teamColor.toComposeColor()

    if (showPicker1) {
        TeamSelectionSheet(
            title = "Select Team 1",
            teams = sortedTeams,
            currentSelected = selectedTeam1,
            excludedTeamName = selectedTeam2.teamName,
            onSelect = {
                selectedTeam1Name = it.teamName
                showPicker1 = false
            },
            onDismiss = { showPicker1 = false }
        )
    }

    if (showPicker2) {
        TeamSelectionSheet(
            title = "Select Team 2",
            teams = sortedTeams,
            currentSelected = selectedTeam2,
            excludedTeamName = selectedTeam1.teamName,
            onSelect = {
                selectedTeam2Name = it.teamName
                showPicker2 = false
            },
            onDismiss = { showPicker2 = false }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Team & Car Versus Hero
        item(contentType = "Hero") {
            TeamVersusHero(
                team1 = selectedTeam1,
                team2 = selectedTeam2,
                color1 = t1Color,
                color2 = t2Color,
                onTeam1Click = { showPicker1 = true },
                onTeam2Click = { showPicker2 = true },
                onSwap = {
                    val temp = selectedTeam1Name
                    selectedTeam1Name = selectedTeam2Name
                    selectedTeam2Name = temp
                }
            )
        }

        // Section: 2026 Constructors Championship
        item(contentType = "SectionHeader") {
            SectionHeader(title = "CONSTRUCTORS CHAMPIONSHIP", subtitle = "2026 Season Performance")
        }

        val s1 = selectedTeam1.seasonStats
        val s2 = selectedTeam2.seasonStats

        item(contentType = "StatCard") {
            ComparisonMetricCard(
                label = "Constructors Points",
                val1 = selectedTeam1.currentPoints.toFloat(),
                val2 = selectedTeam2.currentPoints.toFloat(),
                display1 = selectedTeam1.currentPointsDisplay,
                display2 = selectedTeam2.currentPointsDisplay,
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "PTS"
            )
        }

        item(contentType = "StatCard") {
            ComparisonMetricCard(
                label = "Standings Position",
                val1 = (100 - selectedTeam1.rank).toFloat(),
                val2 = (100 - selectedTeam2.rank).toFloat(),
                display1 = "P${selectedTeam1.rank}",
                display2 = "P${selectedTeam2.rank}",
                color1 = t1Color,
                color2 = t2Color,
                lowerIsBetter = true,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "POS"
            )
        }

        item(contentType = "StatCard") {
            val wins1 = s1?.grandPrixWins?.toFloatOrNull() ?: 0f
            val wins2 = s2?.grandPrixWins?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Grand Prix Wins",
                val1 = wins1,
                val2 = wins2,
                display1 = s1?.grandPrixWins ?: "0",
                display2 = s2?.grandPrixWins ?: "0",
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "WINS"
            )
        }

        item(contentType = "StatCard") {
            val pod1 = s1?.grandPrixPodiums?.toFloatOrNull() ?: 0f
            val pod2 = s2?.grandPrixPodiums?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Podiums",
                val1 = pod1,
                val2 = pod2,
                display1 = s1?.grandPrixPodiums ?: "0",
                display2 = s2?.grandPrixPodiums ?: "0",
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "POD"
            )
        }

        item(contentType = "StatCard") {
            val pole1 = s1?.grandPrixPoles?.toFloatOrNull() ?: 0f
            val pole2 = s2?.grandPrixPoles?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Pole Positions",
                val1 = pole1,
                val2 = pole2,
                display1 = s1?.grandPrixPoles ?: "0",
                display2 = s2?.grandPrixPoles ?: "0",
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "POLES"
            )
        }

        item(contentType = "StatCard") {
            val fast1 = s1?.dhlFastestLaps?.toFloatOrNull() ?: 0f
            val fast2 = s2?.dhlFastestLaps?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Fastest Laps",
                val1 = fast1,
                val2 = fast2,
                display1 = s1?.dhlFastestLaps ?: "0",
                display2 = s2?.dhlFastestLaps ?: "0",
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "LAPS"
            )
        }

        // Section: Historic Achievements
        item(contentType = "SectionHeader") {
            SectionHeader(title = "HISTORIC ACHIEVEMENTS", subtitle = "All-Time Constructor Records")
        }

        val sum1 = selectedTeam1.teamSummary
        val sum2 = selectedTeam2.teamSummary

        item(contentType = "StatCard") {
            val champ1 = sum1?.worldChampionships?.toFloatOrNull() ?: 0f
            val champ2 = sum2?.worldChampionships?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "World Championships",
                val1 = champ1,
                val2 = champ2,
                display1 = sum1?.worldChampionships ?: "0",
                display2 = sum2?.worldChampionships ?: "0",
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "TITLES"
            )
        }

        item(contentType = "StatCard") {
            val races1 = sum1?.grandsPrixEntered?.toFloatOrNull() ?: 0f
            val races2 = sum2?.grandsPrixEntered?.toFloatOrNull() ?: 0f
            ComparisonMetricCard(
                label = "Grands Prix Entered",
                val1 = races1,
                val2 = races2,
                display1 = sum1?.grandsPrixEntered ?: "N/A",
                display2 = sum2?.grandsPrixEntered ?: "N/A",
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase(),
                unit = "RACES"
            )
        }

        // Section: Technical Specifications & Management
        item(contentType = "SectionHeader") {
            SectionHeader(title = "TECHNICAL & MANAGEMENT", subtitle = "Car Specifications & Operations")
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Power Unit / Engine",
                icon = Icons.Default.PrecisionManufacturing,
                val1 = selectedTeam1.powerUnit,
                val2 = selectedTeam2.powerUnit,
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase()
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Chassis Code",
                icon = Icons.Default.DirectionsCar,
                val1 = selectedTeam1.chassis,
                val2 = selectedTeam2.chassis,
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase()
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Team Principal",
                icon = Icons.Default.Person,
                val1 = selectedTeam1.teamChief,
                val2 = selectedTeam2.teamChief,
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase()
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Technical Director",
                icon = Icons.Default.Settings,
                val1 = selectedTeam1.technicalChief,
                val2 = selectedTeam2.technicalChief,
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase()
            )
        }

        item(contentType = "SpecCard") {
            PartitionedComparisonSpecCard(
                title = "Factory Headquarters",
                icon = Icons.Default.LocationOn,
                val1 = selectedTeam1.baseLocation,
                val2 = selectedTeam2.baseLocation,
                color1 = t1Color,
                color2 = t2Color,
                tag1 = selectedTeam1.teamName.take(3).uppercase(),
                tag2 = selectedTeam2.teamName.take(3).uppercase()
            )
        }

        if (selectedTeam1.firstTeamEntryYear.isNotBlank() || selectedTeam2.firstTeamEntryYear.isNotBlank()) {
            item(contentType = "SpecCard") {
                PartitionedComparisonSpecCard(
                    title = "First F1 Entry",
                    icon = Icons.Default.Flag,
                    val1 = selectedTeam1.firstTeamEntryYear,
                    val2 = selectedTeam2.firstTeamEntryYear,
                    color1 = t1Color,
                    color2 = t2Color,
                    tag1 = selectedTeam1.teamName.take(3).uppercase(),
                    tag2 = selectedTeam2.teamName.take(3).uppercase()
                )
            }
        }

        if (selectedTeam1.reserveDriver.isNotBlank() || selectedTeam2.reserveDriver.isNotBlank()) {
            item(contentType = "SpecCard") {
                PartitionedComparisonSpecCard(
                    title = "Reserve Driver",
                    icon = Icons.Default.Person,
                    val1 = selectedTeam1.reserveDriver,
                    val2 = selectedTeam2.reserveDriver,
                    color1 = t1Color,
                    color2 = t2Color,
                    tag1 = selectedTeam1.teamName.take(3).uppercase(),
                    tag2 = selectedTeam2.teamName.take(3).uppercase()
                )
            }
        }

        if (selectedTeam1.fullTeamName.isNotBlank() || selectedTeam2.fullTeamName.isNotBlank()) {
            item(contentType = "SpecCard") {
                PartitionedComparisonSpecCard(
                    title = "Official Team Entry",
                    icon = Icons.Default.Groups,
                    val1 = selectedTeam1.fullTeamName,
                    val2 = selectedTeam2.fullTeamName,
                    color1 = t1Color,
                    color2 = t2Color,
                    tag1 = selectedTeam1.teamName.take(3).uppercase(),
                    tag2 = selectedTeam2.teamName.take(3).uppercase()
                )
            }
        }

        item(contentType = "Spacer") {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =========================================================================
// HERO COMPONENTS (DRIVERS & CARS)
// =========================================================================

@Composable
private fun DriverVersusHero(
    driver1: DriverDetailUiModel,
    driver2: DriverDetailUiModel,
    color1: Color,
    color2: Color,
    isSameTeam: Boolean,
    onDriver1Click: () -> Unit,
    onDriver2Click: () -> Unit,
    onSwap: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSameTeam) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TEAMMATE DUEL · ${driver1.teamName.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver 1 Profile Card - Team color background like DriverCard.kt!
            DriverProfileHeroCard(
                driver = driver1,
                containerColor = color1,
                onClick = onDriver1Click,
                modifier = Modifier.weight(1f)
            )

            // VS Badge & Swap Center
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = onSwap,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Driver 2 Profile Card - Team color background like DriverCard.kt!
            DriverProfileHeroCard(
                driver = driver2,
                containerColor = color2,
                onClick = onDriver2Click,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DriverProfileHeroCard(
    driver: DriverDetailUiModel,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (containerColor.luminance() > 0.5f) Color.Black else Color.White

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Driver Headshot with DriverCard.kt matching styling
            AsyncImage(
                model = driver.headshotUrl?.takeIf { it.isNotEmpty() }
                    ?: "https://f1tv.formula1.com/static/favicon.ico",
                contentDescription = driver.fullName,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Number Badge
            Surface(
                shape = CircleShape,
                color = contentColor.copy(alpha = 0.18f),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "#${driver.driverNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Driver Name - styled as in DriverCard.kt
            Text(
                text = driver.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Team Name - styled as in DriverCard.kt
            Text(
                text = driver.teamName,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = contentColor.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun TeamVersusHero(
    team1: DetailedTeamUiModel,
    team2: DetailedTeamUiModel,
    color1: Color,
    color2: Color,
    onTeam1Click: () -> Unit,
    onTeam2Click: () -> Unit,
    onSwap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team 1 Profile Card - Team color background like TeamCard.kt!
        TeamProfileHeroCard(
            team = team1,
            containerColor = color1,
            onClick = onTeam1Click,
            modifier = Modifier.weight(1f)
        )

        // VS Badge & Swap Center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            FilledTonalIconButton(
                onClick = onSwap,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Team 2 Profile Card - Team color background like TeamCard.kt!
        TeamProfileHeroCard(
            team = team2,
            containerColor = color2,
            onClick = onTeam2Click,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TeamProfileHeroCard(
    team: DetailedTeamUiModel,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (containerColor.luminance() > 0.5f) Color.Black else Color.White

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Team Logo
            AsyncImage(
                model = team.teamLogoUrl?.takeIf { it.isNotEmpty() }
                    ?: "https://f1tv.formula1.com/static/favicon.ico",
                contentDescription = "${team.teamName} Logo",
                modifier = Modifier.height(28.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(6.dp))

            // F1 Car Livery Image
            AsyncImage(
                model = team.teamCarUrl?.takeIf { it.isNotEmpty() }
                    ?: "https://f1tv.formula1.com/static/favicon.ico",
                contentDescription = "${team.teamName} Car",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Team Name
            Text(
                text = team.teamName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =========================================================================
// COMPARISON METRIC CARD
// =========================================================================

@Composable
private fun ComparisonMetricCard(
    label: String,
    val1: Float,
    val2: Float,
    display1: String,
    display2: String,
    color1: Color,
    color2: Color,
    tag1: String,
    tag2: String,
    lowerIsBetter: Boolean = false,
    unit: String = ""
) {
    val isBothZero = val1 <= 0f && val2 <= 0f
    val isTie = val1 == val2
    val d1Wins = if (isBothZero) false else if (lowerIsBetter) (val1 < val2 && val1 > 0f) else val1 > val2
    val d2Wins = if (isBothZero) false else if (lowerIsBetter) (val2 < val1 && val2 > 0f) else val2 > val1

    val diffValue = kotlin.math.abs(val1 - val2)
    val diffDisplay = if (diffValue % 1f == 0f) {
        diffValue.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", diffValue)
    }

    val ratio1 = when {
        isBothZero -> 0.5f // Perfectly balanced 50/50 when both have 0 championships/wins!
        isTie -> 0.5f
        lowerIsBetter -> {
            val total = val1 + val2
            if (total > 0f) (val1 / total).coerceIn(0.12f, 0.88f) else 0.5f
        }
        else -> {
            val total = val1 + val2
            if (total > 0f) (val1 / total).coerceIn(0.12f, 0.88f) else 0.5f
        }
    }

    val animatedRatio by animateFloatAsState(
        targetValue = ratio1,
        animationSpec = tween(durationMillis = 400),
        label = "splitBarRatio"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Row: Label & Winner Difference Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isBothZero) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "0 - NONE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (!isTie) {
                    val winnerTag = if (d1Wins) tag1 else tag2
                    val unitSuffix = if (unit.isNotBlank()) " $unit" else ""
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "$winnerTag +$diffDisplay$unitSuffix",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "TIED · EVEN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Values Row: Displaying Numbers with Delta difference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color1)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = display1,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (d1Wins) FontWeight.Black else FontWeight.Bold,
                        color = if (isBothZero) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!isBothZero && !isTie) {
                    Text(
                        text = "Δ $diffDisplay",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = display2,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (d2Wins) FontWeight.Black else FontWeight.Bold,
                        color = if (isBothZero) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dual Split Proportional Bar - ONLY place using team colors!
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (isBothZero) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize()
                            .background(color1.copy(alpha = 0.35f))
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize()
                            .background(color2.copy(alpha = 0.35f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(animatedRatio)
                            .fillMaxSize()
                            .background(color1)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f - animatedRatio)
                            .fillMaxSize()
                            .background(color2)
                    )
                }
            }
        }
    }
}

// =========================================================================
// PARTITIONED SPECIFICATION CARD (FOR DRIVERS & TEAMS)
// =========================================================================

@Composable
private fun PartitionedComparisonSpecCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    val1: String,
    val2: String,
    color1: Color,
    color2: Color,
    tag1: String,
    tag2: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Centered attribute badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two distinct partitioned columns separated by a vertical divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Partition 1 (Left: Driver 1 / Team 1)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.55f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(color1))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = tag1,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = val1.trim().ifBlank { "N/A" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Center Partition Divider
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .width(1.5.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                )

                // Partition 2 (Right: Driver 2 / Team 2)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.55f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tag2,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(color2))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = val2.trim().ifBlank { "N/A" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// SELECTION BOTTOM SHEETS (MODAL BOTTOM SHEET WITH SEARCH)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverSelectionSheet(
    title: String,
    drivers: List<DriverDetailUiModel>,
    currentSelected: DriverDetailUiModel,
    excludedDriverNumber: String = "",
    onSelect: (DriverDetailUiModel) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, drivers, excludedDriverNumber) {
        val available = if (excludedDriverNumber.isNotBlank()) {
            drivers.filter { it.driverNumber != excludedDriverNumber }
        } else {
            drivers
        }
        if (searchQuery.isBlank()) available
        else available.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
            it.teamName.contains(searchQuery, ignoreCase = true) ||
            it.driverNumber.contains(searchQuery)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search driver or team...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.driverNumber }) { driver ->
                    val isSelected = driver.driverNumber == currentSelected.driverNumber
                    val teamColor = driver.teamColor.toComposeColor()

                    Surface(
                        onClick = { onSelect(driver) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, teamColor, CircleShape)
                                    .background(Color.White.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = driver.headshotUrl ?: "https://f1tv.formula1.com/static/favicon.ico",
                                    contentDescription = driver.fullName,
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.TopCenter,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = driver.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "#${driver.driverNumber} · ${driver.teamName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSelectionSheet(
    title: String,
    teams: List<DetailedTeamUiModel>,
    currentSelected: DetailedTeamUiModel,
    excludedTeamName: String = "",
    onSelect: (DetailedTeamUiModel) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, teams, excludedTeamName) {
        val available = if (excludedTeamName.isNotBlank()) {
            teams.filter { !it.teamName.equals(excludedTeamName, ignoreCase = true) }
        } else {
            teams
        }
        if (searchQuery.isBlank()) available
        else available.filter {
            it.teamName.contains(searchQuery, ignoreCase = true) ||
            it.fullTeamName.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search team or constructor...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.teamName }) { team ->
                    val isSelected = team.teamName == currentSelected.teamName
                    val teamColor = team.teamColor.toComposeColor()

                    Surface(
                        onClick = { onSelect(team) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.5.dp, teamColor, RoundedCornerShape(8.dp))
                                    .background(teamColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = team.teamLogoUrl ?: "https://f1tv.formula1.com/static/favicon.ico",
                                    contentDescription = team.teamName,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = team.teamName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "P${team.rank} · ${team.currentPointsDisplay} PTS",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// HELPER UTILITIES
// =========================================================================

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyComparisonView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Returns distinct, authentic complementary colors when two drivers from the same team are compared.
 * Prevents assigning rival teams' signature primary colors (such as Ferrari red to a Red Bull driver).
 * Uses official team accent palettes (such as Red Bull Navy + Sun Yellow, Ferrari Rosso + Modena Yellow).
 */
private fun getTeammateColors(teamName: String, baseColor: Color): Pair<Color, Color> {
    val lower = teamName.lowercase()
    return when {
        lower.contains("ferrari") -> {
            // Driver 1: Ferrari Rosso Corsa, Driver 2: Ferrari Modena Yellow
            Pair(Color(0xFFE8002D), Color(0xFFFFB800))
        }
        lower.contains("red bull") -> {
            // Driver 1: Red Bull Navy Blue, Driver 2: Red Bull Sun Yellow
            Pair(Color(0xFF162B50), Color(0xFFF59E0B))
        }
        lower.contains("mclaren") -> {
            // Driver 1: Papaya Orange, Driver 2: McLaren Stealth Anthracite
            Pair(Color(0xFFFF8000), Color(0xFF374151))
        }
        lower.contains("mercedes") -> {
            // Driver 1: Petronas Emerald Teal, Driver 2: Mercedes Silver Slate
            Pair(Color(0xFF00A19B), Color(0xFF64748B))
        }
        lower.contains("aston martin") -> {
            // Driver 1: British Racing Green, Driver 2: AMR Lime Essence
            Pair(Color(0xFF00594F), Color(0xFFA3E635))
        }
        lower.contains("alpine") -> {
            // Driver 1: Alpine Racing Blue, Driver 2: BWT Racing Pink
            Pair(Color(0xFF0093CC), Color(0xFFFF87BC))
        }
        lower.contains("williams") -> {
            // Driver 1: Williams Royal Deep Blue, Driver 2: Williams Light Ice Blue
            Pair(Color(0xFF005AFF), Color(0xFF64C4FF))
        }
        lower.contains("racing bulls") || lower.contains("rb") || lower.contains("cash app") -> {
            // Driver 1: Royal Blue, Driver 2: Bull Gold
            Pair(Color(0xFF1634CB), Color(0xFFF59E0B))
        }
        lower.contains("sauber") || lower.contains("kick") -> {
            // Driver 1: Fluo Toxic Green, Driver 2: Gunmetal Carbon
            Pair(Color(0xFF52E252), Color(0xFF334155))
        }
        lower.contains("haas") -> {
            // Driver 1: Haas Graphite, Driver 2: Haas Racing Crimson
            Pair(Color(0xFF475569), Color(0xFFDC2626))
        }
        else -> {
            Pair(baseColor, Color(0xFFFFA000))
        }
    }
}

/**
 * Parses ISO date string (YYYY-MM-DD) and formats it into readable format with age in years.
 */
private fun formatBirthDateAndAge(dob: String): String {
    if (dob.isBlank()) return "N/A"
    return try {
        val parts = dob.trim().split("-")
        if (parts.size == 3) {
            val year = parts[0].toIntOrNull() ?: return dob
            val month = parts[1].toIntOrNull() ?: return dob
            val day = parts[2].toIntOrNull() ?: return dob
            val currentYear = 2026
            val age = currentYear - year
            val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthName = monthNames.getOrElse(month) { "$month" }
            "$day $monthName $year ($age yrs)"
        } else {
            dob
        }
    } catch (_: Exception) {
        dob
    }
}
