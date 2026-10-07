package com.example.taras.view.tcg.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.tcg.model.CardRole
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.view.tcg.components.Interactive3DCard
import com.example.taras.viewmodel.BinderCardItem
import com.example.taras.viewmodel.TcgViewModel

@Composable
fun BinderGridScreen(
    viewModel: TcgViewModel,
    onBackClick: () -> Unit = {},
    onOpenPackClick: () -> Unit = {},
    onBattleClick: (card1Id: String, card2Id: String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inspectedCard by remember { mutableStateOf<TcgCardEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Top Navigation & Actions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "F1 GRID TCG",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "COLLECTOR BINDER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Button(
                    onClick = onOpenPackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Open Pack",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "NEW PACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Collection Progress Card
            val progress = uiState.totalCollected.toFloat() / uiState.totalCards.toFloat()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COLLECTION PROGRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.totalCollected} / ${uiState.totalCards} (${(progress * 100).toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(top = 8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                }
            }

            // Rarity Tier Filters
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedTier == null,
                        onClick = { viewModel.selectTierFilter(null) },
                        label = { Text("All Tiers", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                items(RarityTier.entries) { tier ->
                    FilterChip(
                        selected = uiState.selectedTier == tier,
                        onClick = { viewModel.selectTierFilter(if (uiState.selectedTier == tier) null else tier) },
                        label = { Text("Tier ${tier.badgeLabel}", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // 3x3 Binder Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.filteredItems) { item ->
                    BinderCardCell(
                        item = item,
                        onClick = {
                            if (item.isUnlocked) {
                                inspectedCard = item.card
                            }
                        }
                    )
                }
            }
        }

        // Fullscreen 3D Tilt Inspector Modal
        if (inspectedCard != null) {
            val card = inspectedCard!!
            Dialog(
                onDismissRequest = { inspectedCard = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.75f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Close Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { inspectedCard = null },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Large Interactive 3D Card
                        Box(
                            modifier = Modifier
                                .width(310.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            Interactive3DCard(
                                card = card,
                                isHoloVariant = true,
                                enableFlip = true
                            )
                        }

                        // Hint
                        Text(
                            text = "Drag to tilt 3D • Tap card to flip",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        // Action: Go to Battle Arena with this Card
                        Button(
                            onClick = {
                                val secondCardId = if (card.id == "card_fer_44") "card_rbr_03" else "card_fer_44"
                                inspectedCard = null
                                onBattleClick(card.id, secondCardId)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "CHALLENGE WITH ${card.name.uppercase()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BinderCardCell(
    item: BinderCardItem,
    onClick: () -> Unit
) {
    val card = item.card
    val cardShape = RoundedCornerShape(12.dp)
    val context = LocalContext.current
    val teamPrimaryColor = remember(card.primaryColorHex) {
        com.example.taras.view.tcg.components.parseHexColor(card.primaryColorHex, Color(0xFFE10600))
    }
    val watermarkNumber = remember(card.driverNumber, card.code) {
        val cleaned = card.driverNumber.removePrefix("#").trim()
        if (cleaned.isNotBlank()) cleaned else card.code
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(5f / 7f)
            .shadow(4.dp, cardShape)
            .clip(cardShape)
            .clickable { onClick() }
            .then(
                if (item.isUnlocked) {
                    Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    teamPrimaryColor,
                                    teamPrimaryColor,
                                    com.example.taras.view.tcg.components.darkenColor(teamPrimaryColor, 0.70f)
                                )
                            )
                        )
                        .border(1.5.dp, card.tier.borderColor, cardShape)
                } else {
                    Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), cardShape)
                }
            )
    ) {
        if (item.isUnlocked) {
            if (card.role == CardRole.CHASSIS) {
                val teamLogoUrl = remember(card.teamId) {
                    com.example.taras.view.tcg.components.getTeamLogoWatermarkUrl(card.teamId)
                }
                val logoRequest = remember(teamLogoUrl) {
                    ImageRequest.Builder(context)
                        .data(teamLogoUrl)
                        .crossfade(true)
                        .build()
                }
                // Giant Watermark Team Logo behind car cutout
                AsyncImage(
                    model = logoRequest,
                    contentDescription = "${card.teamName} Logo Watermark",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                        .alpha(0.25f)
                )
            } else {
                // Giant Watermark Number behind driver cutout
                Text(
                    text = watermarkNumber,
                    fontSize = if (watermarkNumber.length <= 2) 56.sp else 34.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.24f),
                    letterSpacing = (-3).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(bottom = 6.dp)
                )
            }

            // Unlocked Card Thumbnail Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Tier badge & OVR in sleek glass pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(19.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, card.tier.borderColor, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = card.tier.badgeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = card.tier.borderColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 10.sp,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(0.6.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${card.ovr}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 8.5.sp,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }
                }

                // Driver Half Profile Cutout
                val highResAvatarUrl = remember(card.avatarUrl) {
                    com.example.taras.view.tcg.components.getOptimizedAvatarUrl(card.avatarUrl)
                }
                val imageRequest = remember(highResAvatarUrl) {
                    ImageRequest.Builder(context)
                        .data(highResAvatarUrl)
                        .crossfade(true)
                        .build()
                }

                AsyncImage(
                    model = imageRequest,
                    contentDescription = card.name,
                    contentScale = ContentScale.Fit,
                    alignment = if (card.role == CardRole.CHASSIS) Alignment.Center else Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                )

                // Bottom Driver Name & Duplicate Count Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.70f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                    horizontalArrangement = if (item.quantity > 1) Arrangement.SpaceBetween else Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = card.name.uppercase(),
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (item.quantity > 1) TextAlign.Start else TextAlign.Center,
                        modifier = if (item.quantity > 1) Modifier.weight(1f, fill = false) else Modifier
                    )
                    if (item.quantity > 1) {
                        Box(
                            modifier = Modifier
                                .padding(start = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(card.tier.borderColor.copy(alpha = 0.20f))
                                .border(0.8.dp, card.tier.borderColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 3.dp, vertical = 0.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "×${item.quantity}",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Black,
                                color = card.tier.borderColor,
                                lineHeight = 7.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.Both
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } else {
            // Locked Card Silhouette
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked Card",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "#${card.cardNumber.toString().padStart(2, '0')}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tier ${card.tier.badgeLabel}",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = card.tier.borderColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}
