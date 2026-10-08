package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taras.R
import com.example.taras.network_calls.ApiConstants
import com.example.taras.view.subview.LegalTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalHubScreen(
    initialTabName: String = "about",
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val initialTab = when (initialTabName.lowercase()) {
        "privacy" -> LegalTab.PRIVACY
        "terms" -> LegalTab.TERMS
        else -> LegalTab.ABOUT
    }

    var selectedTab by remember { mutableStateOf(initialTab) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "" to stringResource(R.string.lang_system_default),
        "en" to stringResource(R.string.lang_en),
        "hi" to stringResource(R.string.lang_hi),
        "es" to stringResource(R.string.lang_es),
        "de" to stringResource(R.string.lang_de),
        "fr" to stringResource(R.string.lang_fr),
        "ja" to stringResource(R.string.lang_ja),
        "pt" to stringResource(R.string.lang_pt)
    )

    val currentLocaleTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? android.app.LocaleManager
        localeManager?.applicationLocales?.toLanguageTags()?.split(",")?.firstOrNull() ?: ""
    } else {
        java.util.Locale.getDefault().language
    }

    val currentLangLabel = languages.firstOrNull { it.first == currentLocaleTag }?.second
        ?: languages.firstOrNull { currentLocaleTag.startsWith(it.first) && it.first.isNotEmpty() }?.second
        ?: stringResource(R.string.lang_system_default)

    val openOnline = {
        val url = when (selectedTab) {
            LegalTab.ABOUT -> ApiConstants.ABOUT_URL
            LegalTab.PRIVACY -> ApiConstants.PRIVACY_POLICY_URL
            LegalTab.TERMS -> ApiConstants.TERMS_OF_SERVICE_URL
        }
        try {
            uriHandler.openUri(url)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
        }
    }

    val contactGrievanceOfficer = {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${ApiConstants.GRIEVANCE_EMAIL}")
                putExtra(Intent.EXTRA_SUBJECT, "Taras App — Legal & Privacy Inquiry")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Grievance Contact: ${ApiConstants.GRIEVANCE_EMAIL}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.legal_hub_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (selectedTab) {
                                LegalTab.ABOUT -> stringResource(R.string.drawer_about_us)
                                LegalTab.PRIVACY -> stringResource(R.string.drawer_privacy_policy)
                                LegalTab.TERMS -> stringResource(R.string.drawer_terms_of_service)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE10600),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    // Language Switcher in Top Bar
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { languageMenuExpanded = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = currentLangLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            languages.forEach { (tag, name) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = name,
                                            fontWeight = if (tag == currentLocaleTag) FontWeight.Bold else FontWeight.Normal,
                                            color = if (tag == currentLocaleTag) Color(0xFFE10600) else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        languageMenuExpanded = false
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? android.app.LocaleManager
                                            if (tag.isEmpty()) {
                                                localeManager?.applicationLocales = android.os.LocaleList.getEmptyLocaleList()
                                            } else {
                                                localeManager?.applicationLocales = android.os.LocaleList.forLanguageTags(tag)
                                            }
                                        } else {
                                            val locale = if (tag.isEmpty()) java.util.Locale.getDefault() else java.util.Locale.forLanguageTag(tag)
                                            java.util.Locale.setDefault(locale)
                                            val config = context.resources.configuration
                                            config.setLocale(locale)
                                            @Suppress("DEPRECATION")
                                            context.resources.updateConfiguration(config, context.resources.displayMetrics)
                                            (context as? Activity)?.recreate()
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = openOnline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.privacy_btn_view_online),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = contactGrievanceOfficer,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Grievance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Hero Header Card with gradient and badges
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE10600).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "🏎️ TARAS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFFE10600),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            val (badgeText, badgeColor) = when (selectedTab) {
                                LegalTab.ABOUT -> "तरस् · SPEED" to Color(0xFFE10600)
                                LegalTab.PRIVACY -> "DPDPA 2023 · INDIA" to Color(0xFF10B981)
                                LegalTab.TERMS -> "IT ACT 2000 · COMPLIANT" to Color(0xFF3B82F6)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = badgeColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = badgeColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "100% PRIVATE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = when (selectedTab) {
                            LegalTab.ABOUT -> stringResource(R.string.about_tagline)
                            LegalTab.PRIVACY -> stringResource(R.string.privacy_core_commitment)
                            LegalTab.TERMS -> stringResource(R.string.terms_badge_it_act)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tabs Selector
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(selectedTab.ordinal),
                        color = Color(0xFFE10600)
                    )
                },
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                }
            ) {
                Tab(
                    selected = selectedTab == LegalTab.ABOUT,
                    onClick = { selectedTab = LegalTab.ABOUT },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.tab_about), fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == LegalTab.PRIVACY,
                    onClick = { selectedTab = LegalTab.PRIVACY },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.tab_privacy), fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == LegalTab.TERMS,
                    onClick = { selectedTab = LegalTab.TERMS },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.tab_terms), fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Tab Content with Animated Transition
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    },
                    label = "LegalTabTransition"
                ) { tab ->
                    when (tab) {
                        LegalTab.ABOUT -> FullAboutContent(onContactGrievance = contactGrievanceOfficer)
                        LegalTab.PRIVACY -> FullPrivacyContent(onContactGrievance = contactGrievanceOfficer)
                        LegalTab.TERMS -> FullTermsContent(onContactGrievance = contactGrievanceOfficer)
                    }
                }
            }
        }
    }
}

@Composable
private fun FullAboutContent(onContactGrievance: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sanskrit Philosophy Hero Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFFE10600).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsMotorsports,
                        contentDescription = null,
                        tint = Color(0xFFE10600),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.about_sec_story_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE10600)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.about_sec_story_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Developer & Philosophy
        FullLegalCard(
            icon = Icons.Default.Person,
            title = stringResource(R.string.about_sec_developer_title)
        ) {
            Text(
                text = stringResource(R.string.about_sec_developer_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Core Pillars
        FullLegalCard(
            icon = Icons.Default.Security,
            title = stringResource(R.string.about_sec_pillars_title)
        ) {
            LegalBentoRow(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.about_pillar1_title),
                desc = stringResource(R.string.about_pillar1_desc),
                tint = Color(0xFF10B981)
            )
            Spacer(Modifier.height(8.dp))
            LegalBentoRow(
                icon = Icons.Default.Gavel,
                title = stringResource(R.string.about_pillar2_title),
                desc = stringResource(R.string.about_pillar2_desc),
                tint = Color(0xFF3B82F6)
            )
            Spacer(Modifier.height(8.dp))
            LegalBentoRow(
                icon = Icons.Default.Code,
                title = stringResource(R.string.about_pillar3_title),
                desc = stringResource(R.string.about_pillar3_desc),
                tint = Color(0xFFE10600)
            )
        }

        // Tech Stack
        FullLegalCard(
            icon = Icons.Default.Code,
            title = stringResource(R.string.about_sec_tech_title)
        ) {
            Text(
                text = stringResource(R.string.about_sec_tech_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Indian Legal Compliance & Grievance Contact
        FullLegalCard(
            icon = Icons.Default.Gavel,
            title = stringResource(R.string.about_sec_compliance_title)
        ) {
            Text(
                text = stringResource(R.string.about_sec_compliance_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_name))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_email))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_location))
        }

        // GitHub Repository Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.about_version),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "github.com/yashajagiya/Taras",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = { uriHandler.openUri(ApiConstants.GITHUB_REPO_URL) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "GitHub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FullPrivacyContent(onContactGrievance: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Core Commitment Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFF10B981).copy(alpha = 0.45f), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.privacy_core_commitment),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.privacy_core_commitment_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Section: Indian DPDPA 2023
        FullLegalCard(
            icon = Icons.Default.Security,
            title = stringResource(R.string.privacy_sec_dpdpa_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_dpdpa_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Data Principal Rights under DPDPA 2023
        FullLegalCard(
            icon = Icons.Default.CheckCircle,
            title = stringResource(R.string.privacy_sec_principal_rights_title)
        ) {
            LegalBulletPoint(title = stringResource(R.string.privacy_right_access))
            LegalBulletPoint(title = stringResource(R.string.privacy_right_correction))
            LegalBulletPoint(title = stringResource(R.string.privacy_right_erasure))
            LegalBulletPoint(title = stringResource(R.string.privacy_right_grievance))
        }

        // Section: Information Not Collected
        FullLegalCard(
            icon = Icons.Default.Lock,
            title = stringResource(R.string.privacy_sec_no_data_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_no_data_p1),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalCheckItemFull(text = stringResource(R.string.privacy_sec_no_data_item1))
            LegalCheckItemFull(text = stringResource(R.string.privacy_sec_no_data_item2))
            LegalCheckItemFull(text = stringResource(R.string.privacy_sec_no_data_item3))
            LegalCheckItemFull(text = stringResource(R.string.privacy_sec_no_data_item4))
        }

        // Section: Strictly On-Device Storage
        FullLegalCard(
            icon = Icons.Default.Storage,
            title = stringResource(R.string.privacy_sec_storage_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_storage_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Public Feeds & Security
        FullLegalCard(
            icon = Icons.Default.Public,
            title = stringResource(R.string.privacy_sec_network_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_network_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Children's Privacy
        FullLegalCard(
            icon = Icons.Default.Person,
            title = stringResource(R.string.privacy_sec_children_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_children_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Device Permissions
        FullLegalCard(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.privacy_sec_notifications_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_notifications_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Grievance Officer
        FullLegalCard(
            icon = Icons.Default.Email,
            title = stringResource(R.string.privacy_sec_grievance_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_grievance_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_name))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_email))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_location))
        }

        // Section: Open Source
        FullLegalCard(
            icon = Icons.Default.Language,
            title = stringResource(R.string.privacy_sec_opensource_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_opensource_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FullTermsContent(onContactGrievance: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Indian Law Banner
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.45f), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.terms_badge_it_act),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B82F6)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.terms_sec_agreement_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Section 2: Non-commercial license
        FullLegalCard(
            icon = Icons.Default.Description,
            title = stringResource(R.string.terms_sec_license_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_license_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 3: Trademark Fair Use
        FullLegalCard(
            icon = Icons.Default.SportsMotorsports,
            title = stringResource(R.string.terms_sec_disclaimer_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_disclaimer_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 4: Intermediary RSS feeds
        FullLegalCard(
            icon = Icons.Default.Public,
            title = stringResource(R.string.terms_sec_intermediary_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_intermediary_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 5: TCG Virtual Cards
        FullLegalCard(
            icon = Icons.Default.Style,
            title = stringResource(R.string.terms_sec_tcg_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_tcg_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 6: Prohibited activities
        FullLegalCard(
            icon = Icons.Default.Lock,
            title = stringResource(R.string.terms_sec_conduct_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_conduct_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 7: Warranties & liability
        FullLegalCard(
            icon = Icons.Default.Security,
            title = stringResource(R.string.terms_sec_warranty_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_warranty_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 8: Governing Law
        FullLegalCard(
            icon = Icons.Default.Gavel,
            title = stringResource(R.string.terms_sec_jurisdiction_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_jurisdiction_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_name))
            LegalBulletPoint(title = stringResource(R.string.grievance_officer_email))
        }
    }
}

@Composable
private fun FullLegalCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun LegalBentoRow(
    icon: ImageVector,
    title: String,
    desc: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LegalBulletPoint(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun LegalCheckItemFull(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}
