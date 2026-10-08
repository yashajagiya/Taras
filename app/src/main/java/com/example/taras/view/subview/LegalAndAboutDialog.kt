package com.example.taras.view.subview

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.taras.R
import com.example.taras.network_calls.ApiConstants

enum class LegalTab {
    ABOUT,
    PRIVACY,
    TERMS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalAndAboutDialog(
    initialTab: LegalTab = LegalTab.ABOUT,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val openOnline = {
        val targetUrl = when (selectedTab) {
            LegalTab.ABOUT -> ApiConstants.ABOUT_URL
            LegalTab.PRIVACY -> ApiConstants.PRIVACY_POLICY_URL
            LegalTab.TERMS -> ApiConstants.TERMS_OF_SERVICE_URL
        }
        try {
            uriHandler.openUri(targetUrl)
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE10600).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "🏎️ TARAS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFFE10600),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            val (badgeText, badgeColor) = when (selectedTab) {
                                LegalTab.ABOUT -> "तरस् · SPEED" to Color(0xFFE10600)
                                LegalTab.PRIVACY -> "DPDPA 2023 · PRIVATE" to Color(0xFF10B981)
                                LegalTab.TERMS -> "IT ACT 2000 · INDIA" to Color(0xFF3B82F6)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = badgeColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp,
                                    color = badgeColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        val headerTitle = when (selectedTab) {
                            LegalTab.ABOUT -> stringResource(R.string.about_title)
                            LegalTab.PRIVACY -> stringResource(R.string.privacy_policy_title)
                            LegalTab.TERMS -> stringResource(R.string.terms_title)
                        }

                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Tab Row for Navigation
                SecondaryScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 0.dp,
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
                                Text(stringResource(R.string.tab_about), fontWeight = FontWeight.SemiBold)
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
                                Text(stringResource(R.string.tab_privacy), fontWeight = FontWeight.SemiBold)
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
                                Text(stringResource(R.string.tab_terms), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Scrollable Content per Tab
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        LegalTab.ABOUT -> AboutTabContent(onContactGrievance = contactGrievanceOfficer)
                        LegalTab.PRIVACY -> PrivacyTabContent(onContactGrievance = contactGrievanceOfficer)
                        LegalTab.TERMS -> TermsTabContent(onContactGrievance = contactGrievanceOfficer)
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                Spacer(Modifier.height(12.dp))

                // Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = openOnline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.privacy_btn_view_online),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        onClick = contactGrievanceOfficer,
                        modifier = Modifier.weight(1.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Grievance",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.privacy_btn_got_it),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutTabContent(onContactGrievance: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tagline Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFFE10600).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsMotorsports,
                        contentDescription = null,
                        tint = Color(0xFFE10600),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Taras (तरस्) — Velocity & Power",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE10600)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.about_sec_story_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Developer & Philosophy
        LegalSectionCard(
            icon = Icons.Default.Person,
            title = stringResource(R.string.about_sec_developer_title)
        ) {
            Text(
                text = stringResource(R.string.about_sec_developer_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Core Pillars
        LegalSectionCard(
            icon = Icons.Default.Security,
            title = stringResource(R.string.about_sec_pillars_title)
        ) {
            LegalBulletItem(
                title = stringResource(R.string.about_pillar1_title),
                desc = stringResource(R.string.about_pillar1_desc)
            )
            LegalBulletItem(
                title = stringResource(R.string.about_pillar2_title),
                desc = stringResource(R.string.about_pillar2_desc)
            )
            LegalBulletItem(
                title = stringResource(R.string.about_pillar3_title),
                desc = stringResource(R.string.about_pillar3_desc)
            )
        }

        // Tech Stack
        LegalSectionCard(
            icon = Icons.Default.Code,
            title = stringResource(R.string.about_sec_tech_title)
        ) {
            Text(
                text = stringResource(R.string.about_sec_tech_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Indian Legal Compliance & Grievance Contact
        LegalSectionCard(
            icon = Icons.Default.Gavel,
            title = stringResource(R.string.about_sec_compliance_title)
        ) {
            Text(
                text = stringResource(R.string.about_sec_compliance_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalBulletItem(title = stringResource(R.string.grievance_officer_name), desc = "")
            LegalBulletItem(title = stringResource(R.string.grievance_officer_email), desc = "")
            LegalBulletItem(title = stringResource(R.string.grievance_officer_location), desc = "")
        }

        // GitHub Repository Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "GitHub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PrivacyTabContent(onContactGrievance: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Core Commitment Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFF10B981).copy(alpha = 0.45f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_core_commitment),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.privacy_core_commitment_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Section: Indian DPDPA 2023
        LegalSectionCard(
            icon = Icons.Default.Security,
            title = stringResource(R.string.privacy_sec_dpdpa_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_dpdpa_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Data Principal Rights under DPDPA 2023
        LegalSectionCard(
            icon = Icons.Default.CheckCircle,
            title = stringResource(R.string.privacy_sec_principal_rights_title)
        ) {
            LegalBulletItem(title = stringResource(R.string.privacy_right_access), desc = "")
            LegalBulletItem(title = stringResource(R.string.privacy_right_correction), desc = "")
            LegalBulletItem(title = stringResource(R.string.privacy_right_erasure), desc = "")
            LegalBulletItem(title = stringResource(R.string.privacy_right_grievance), desc = "")
        }

        // Section: Information Not Collected
        LegalSectionCard(
            icon = Icons.Default.Lock,
            title = stringResource(R.string.privacy_sec_no_data_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_no_data_p1),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalCheckItem(text = stringResource(R.string.privacy_sec_no_data_item1))
            LegalCheckItem(text = stringResource(R.string.privacy_sec_no_data_item2))
            LegalCheckItem(text = stringResource(R.string.privacy_sec_no_data_item3))
            LegalCheckItem(text = stringResource(R.string.privacy_sec_no_data_item4))
        }

        // Section: Strictly On-Device Storage
        LegalSectionCard(
            icon = Icons.Default.Storage,
            title = stringResource(R.string.privacy_sec_storage_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_storage_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Public Feeds & Security
        LegalSectionCard(
            icon = Icons.Default.Public,
            title = stringResource(R.string.privacy_sec_network_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_network_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Children's Privacy
        LegalSectionCard(
            icon = Icons.Default.Person,
            title = stringResource(R.string.privacy_sec_children_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_children_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Device Permissions
        LegalSectionCard(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.privacy_sec_notifications_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_notifications_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Grievance Officer
        LegalSectionCard(
            icon = Icons.Default.Email,
            title = stringResource(R.string.privacy_sec_grievance_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_grievance_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalBulletItem(title = stringResource(R.string.grievance_officer_name), desc = "")
            LegalBulletItem(title = stringResource(R.string.grievance_officer_email), desc = "")
            LegalBulletItem(title = stringResource(R.string.grievance_officer_location), desc = "")
        }

        // Section: Open Source
        LegalSectionCard(
            icon = Icons.Default.Language,
            title = stringResource(R.string.privacy_sec_opensource_title)
        ) {
            Text(
                text = stringResource(R.string.privacy_sec_opensource_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TermsTabContent(onContactGrievance: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Indian Law Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.45f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.terms_badge_it_act),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B82F6)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.terms_sec_agreement_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Section 2: Non-commercial license
        LegalSectionCard(
            icon = Icons.Default.Description,
            title = stringResource(R.string.terms_sec_license_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_license_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 3: Trademark Fair Use
        LegalSectionCard(
            icon = Icons.Default.SportsMotorsports,
            title = stringResource(R.string.terms_sec_disclaimer_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_disclaimer_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 4: Intermediary RSS feeds
        LegalSectionCard(
            icon = Icons.Default.Public,
            title = stringResource(R.string.terms_sec_intermediary_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_intermediary_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 5: TCG Virtual Cards
        LegalSectionCard(
            icon = Icons.Default.Style,
            title = stringResource(R.string.terms_sec_tcg_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_tcg_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 6: Prohibited activities
        LegalSectionCard(
            icon = Icons.Default.Lock,
            title = stringResource(R.string.terms_sec_conduct_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_conduct_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 7: Warranties & liability
        LegalSectionCard(
            icon = Icons.Default.Security,
            title = stringResource(R.string.terms_sec_warranty_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_warranty_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 8: Governing Law
        LegalSectionCard(
            icon = Icons.Default.Gavel,
            title = stringResource(R.string.terms_sec_jurisdiction_title)
        ) {
            Text(
                text = stringResource(R.string.terms_sec_jurisdiction_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            LegalBulletItem(title = stringResource(R.string.grievance_officer_name), desc = "")
            LegalBulletItem(title = stringResource(R.string.grievance_officer_email), desc = "")
        }
    }
}

@Composable
private fun LegalSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun LegalBulletItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier
                .size(15.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )
            if (desc.isNotEmpty()) {
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun LegalCheckItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier
                .size(15.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}
