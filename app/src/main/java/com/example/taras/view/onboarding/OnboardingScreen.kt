package com.example.taras.view.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.taras.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taras.core.helpercore.RefreshHapticFeedback
import com.example.taras.core.notification.NotificationScheduler
import com.example.taras.ui.theme.TeamAlpine
import com.example.taras.ui.theme.TeamAstonMartin
import com.example.taras.ui.theme.TeamFerrari
import com.example.taras.ui.theme.TeamHaas
import com.example.taras.ui.theme.TeamKickSauber
import com.example.taras.ui.theme.TeamMcLaren
import com.example.taras.ui.theme.TeamMercedes
import com.example.taras.ui.theme.TeamRedBull
import com.example.taras.ui.theme.TeamVisaCashApp
import com.example.taras.ui.theme.TeamWilliams
import com.example.taras.viewmodel.AppearanceViewModel
import com.example.taras.viewmodel.UserViewModel
import kotlinx.coroutines.launch

data class OnboardingDriver(
    val number: String,
    val name: String,
    val team: String,
    val teamColor: Color
)

val ONBOARDING_DRIVERS = listOf(
    OnboardingDriver("1", "Max Verstappen", "Red Bull", TeamRedBull),
    OnboardingDriver("44", "Lewis Hamilton", "Ferrari", TeamFerrari),
    OnboardingDriver("4", "Lando Norris", "McLaren", TeamMcLaren),
    OnboardingDriver("16", "Charles Leclerc", "Ferrari", TeamFerrari),
    OnboardingDriver("81", "Oscar Piastri", "McLaren", TeamMcLaren),
    OnboardingDriver("63", "George Russell", "Mercedes", TeamMercedes),
    OnboardingDriver("14", "Fernando Alonso", "Aston Martin", TeamAstonMartin),
    OnboardingDriver("55", "Carlos Sainz Jr.", "Williams", TeamWilliams),
    OnboardingDriver("12", "Kimi Antonelli", "Mercedes", TeamMercedes),
    OnboardingDriver("23", "Alexander Albon", "Williams", TeamWilliams),
    OnboardingDriver("10", "Pierre Gasly", "Alpine", TeamAlpine),
    OnboardingDriver("22", "Yuki Tsunoda", "Racing Bulls", TeamVisaCashApp),
    OnboardingDriver("27", "Nico Hülkenberg", "Kick Sauber", TeamKickSauber),
    OnboardingDriver("30", "Liam Lawson", "Red Bull", TeamRedBull),
    OnboardingDriver("31", "Esteban Ocon", "Haas", TeamHaas),
    OnboardingDriver("87", "Oliver Bearman", "Haas", TeamHaas)
)

data class OnboardingTeam(
    val name: String,
    val shortName: String,
    val color: Color
)

val ONBOARDING_TEAMS = listOf(
    OnboardingTeam("Oracle Red Bull Racing", "Red Bull", TeamRedBull),
    OnboardingTeam("Scuderia Ferrari", "Ferrari", TeamFerrari),
    OnboardingTeam("McLaren F1 Team", "McLaren", TeamMcLaren),
    OnboardingTeam("Mercedes-AMG PETRONAS", "Mercedes", TeamMercedes),
    OnboardingTeam("Aston Martin Aramco", "Aston Martin", TeamAstonMartin),
    OnboardingTeam("BWT Alpine F1 Team", "Alpine", TeamAlpine),
    OnboardingTeam("Williams Racing", "Williams", TeamWilliams),
    OnboardingTeam("Visa Cash App RB", "Racing Bulls", TeamVisaCashApp),
    OnboardingTeam("Kick Sauber F1 Team", "Kick Sauber", TeamKickSauber),
    OnboardingTeam("MoneyGram Haas F1 Team", "Haas", TeamHaas)
)

/**
 * Modern Material 3 First-Run Onboarding Experience.
 * Guides new users through:
 * 1. Name & Racer Call-sign
 * 2. Favorite Driver and Constructor
 * 3. Notification Preferences & Live Theme Selection
 *
 * Saves all user preferences directly to DataStore and marks [hasSeenWelcome] = true.
 */
@Composable
fun OnboardingScreen(
    userViewModel: UserViewModel,
    appearanceViewModel: AppearanceViewModel,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    val currentUserName by userViewModel.userName.collectAsStateWithLifecycle()
    val savedFavDriverNumber by userViewModel.favoriteDriverNumber.collectAsStateWithLifecycle()
    val savedFavTeam by userViewModel.favoriteTeam.collectAsStateWithLifecycle()
    val currentAppearance by appearanceViewModel.appearanceData.collectAsStateWithLifecycle()
    val savedNotificationsEnabled by userViewModel.notificationsEnabled.collectAsStateWithLifecycle()

    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val totalSteps = 3

    // Step 1: Name state
    var nameInput by rememberSaveable(currentUserName) {
        mutableStateOf(if (currentUserName == "Guest") "" else currentUserName)
    }

    // Step 2: Selected Driver & Team state
    var selectedDriverNumber by rememberSaveable(savedFavDriverNumber) {
        mutableStateOf(savedFavDriverNumber)
    }
    var selectedTeamName by rememberSaveable(savedFavTeam) {
        mutableStateOf(savedFavTeam)
    }

    // Step 3: Theme & Notification state
    var selectedTheme by rememberSaveable(currentAppearance) {
        mutableStateOf(currentAppearance)
    }
    var notificationsEnabled by rememberSaveable(savedNotificationsEnabled) {
        mutableStateOf(savedNotificationsEnabled)
    }

    // Notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        if (isGranted) {
            NotificationScheduler.scheduleNotificationSync(context)
        }
    }

    fun completeOnboarding() {
        RefreshHapticFeedback.triggerSuccessHaptic(context, view)

        // 1. Save Name (default to Guest if empty)
        val finalName = nameInput.trim().ifEmpty { "Guest" }
        userViewModel.updateName(finalName)

        // 2. Save Favorite Driver
        val driver = ONBOARDING_DRIVERS.find { it.number == selectedDriverNumber }
        if (driver != null) {
            userViewModel.setFavoriteDriver(
                driverNumber = driver.number,
                driverName = driver.name,
                teamName = driver.team
            )
        }

        // 3. Save Favorite Team
        if (!selectedTeamName.isNullOrBlank()) {
            userViewModel.setFavoriteTeam(selectedTeamName)
        }

        // 4. Save Theme & Widget Theme (Light is main app theme, Dark is main widget theme)
        appearanceViewModel.updateAppearance(selectedTheme)
        appearanceViewModel.updateWidgetTheme("Dark")

        // 5. Save Notifications
        userViewModel.setNotificationsEnabled(notificationsEnabled)
        if (notificationsEnabled) {
            NotificationScheduler.scheduleNotificationSync(context)
        } else {
            NotificationScheduler.cancelNotificationSync(context)
        }

        // 6. Mark onboarding complete in DataStore so it is shown only once
        userViewModel.setHasSeenWelcome(true)
        onFinish()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top Navigation & Step Indicator
            OnboardingHeader(
                currentStep = currentStep,
                totalSteps = totalSteps,
                onSkip = { completeOnboarding() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "OnboardingStepAnimation"
                ) { step ->
                    when (step) {
                        0 -> StepIdentity(
                            name = nameInput,
                            onNameChange = { nameInput = it },
                            onNext = {
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                                currentStep = 1
                            }
                        )
                        1 -> StepFavorites(
                            selectedDriverNumber = selectedDriverNumber,
                            onSelectDriver = { num ->
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                                selectedDriverNumber = if (selectedDriverNumber == num) null else num
                            },
                            selectedTeamName = selectedTeamName,
                            onSelectTeam = { team ->
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                                selectedTeamName = if (selectedTeamName == team) null else team
                            }
                        )
                        2 -> StepPreferences(
                            selectedTheme = selectedTheme,
                            onSelectTheme = { theme ->
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                                selectedTheme = theme
                                appearanceViewModel.updateAppearance(theme)
                            },
                            notificationsEnabled = notificationsEnabled,
                            onToggleNotifications = { enabled ->
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                                notificationsEnabled = enabled
                                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (!hasPermission) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Bottom Navigation Buttons
            Spacer(modifier = Modifier.height(16.dp))
            OnboardingBottomBar(
                currentStep = currentStep,
                totalSteps = totalSteps,
                onBack = {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    if (currentStep > 0) currentStep--
                },
                onNext = {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    if (currentStep < totalSteps - 1) {
                        currentStep++
                    } else {
                        completeOnboarding()
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingHeader(
    currentStep: Int,
    totalSteps: Int,
    onSkip: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = (currentStep + 1) / totalSteps.toFloat(),
        label = "ProgressAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TARAS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.onboarding_step_indicator, currentStep + 1, totalSteps),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(stringResource(R.string.onboarding_skip), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/**
 * Step 1: User Name & Identity
 */
@Composable
private fun StepIdentity(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Hero Badge
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsMotorsports,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.onboarding_step1_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_step1_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name input card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_step1_name_prompt),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.onboarding_step1_name_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 30) onNameChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text(stringResource(R.string.onboarding_step1_name_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (name.isNotEmpty()) {
                            IconButton(onClick = { onNameChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onNext()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                if (name.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.onboarding_step1_greeting, name),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Step 2: Favorite Driver and Constructor
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepFavorites(
    selectedDriverNumber: String?,
    onSelectDriver: (String) -> Unit,
    selectedTeamName: String?,
    onSelectTeam: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_step2_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.onboarding_step2_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- Favorite Driver Section ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.onboarding_step2_fav_driver),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ONBOARDING_DRIVERS.forEach { driver ->
                val isSelected = selectedDriverNumber == driver.number
                DriverSelectionChip(
                    driver = driver,
                    isSelected = isSelected,
                    onClick = { onSelectDriver(driver.number) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Favorite Team Section ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SportsScore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.onboarding_step2_fav_constructor),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ONBOARDING_TEAMS.forEach { team ->
                val isSelected = selectedTeamName?.contains(team.shortName, ignoreCase = true) == true
                TeamSelectionChip(
                    team = team,
                    isSelected = isSelected,
                    onClick = { onSelectTeam(team.shortName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DriverSelectionChip(
    driver: OnboardingDriver,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = "${driver.name} #${driver.number}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(driver.teamColor)
            )
        },
        trailingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = if (isSelected) {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = true,
                borderColor = driver.teamColor,
                borderWidth = 1.5.dp
            )
        } else null
    )
}

@Composable
private fun TeamSelectionChip(
    team: OnboardingTeam,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = team.shortName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(team.color)
            )
        },
        trailingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = if (isSelected) {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = true,
                borderColor = team.color,
                borderWidth = 1.5.dp
            )
        } else null
    )
}

/**
 * Step 3: Theme and Notification Preferences
 */
@Composable
private fun StepPreferences(
    selectedTheme: String,
    onSelectTheme: (String) -> Unit,
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_step3_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.onboarding_step3_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Theme Section ---
        Text(
            text = stringResource(R.string.onboarding_step3_theme),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemeOptionCard(
                title = stringResource(R.string.theme_light),
                subtitle = stringResource(R.string.onboarding_theme_light_sub),
                icon = Icons.Default.LightMode,
                isSelected = selectedTheme == "Light",
                modifier = Modifier.weight(1f),
                onClick = { onSelectTheme("Light") }
            )

            ThemeOptionCard(
                title = stringResource(R.string.theme_dark),
                subtitle = stringResource(R.string.onboarding_theme_dark_sub),
                icon = Icons.Default.DarkMode,
                isSelected = selectedTheme == "Dark",
                modifier = Modifier.weight(1f),
                onClick = { onSelectTheme("Dark") }
            )

            ThemeOptionCard(
                title = stringResource(R.string.theme_system),
                subtitle = stringResource(R.string.onboarding_theme_system_sub),
                icon = Icons.Default.BrightnessAuto,
                isSelected = selectedTheme == "System Default",
                modifier = Modifier.weight(1f),
                onClick = { onSelectTheme("System Default") }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- Notifications Section ---
        Text(
            text = stringResource(R.string.onboarding_step3_alerts),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleNotifications(!notificationsEnabled) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (notificationsEnabled) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = if (notificationsEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.onboarding_step3_reminders),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.onboarding_step3_reminders_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { onToggleNotifications(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OnboardingBottomBar(
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.onboarding_back),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.onboarding_back), fontWeight = FontWeight.SemiBold)
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (currentStep == totalSteps - 1) stringResource(R.string.onboarding_enter_paddock) else stringResource(R.string.onboarding_continue),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (currentStep == totalSteps - 1) Icons.Default.SportsScore else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
