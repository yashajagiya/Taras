package com.example.taras.view.tcg.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.tcg.seed.RosterSeedData
import com.example.taras.view.tcg.components.Interactive3DCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleArenaScreen(
    initialCard1Id: String = "card_fer_44",
    initialCard2Id: String = "card_rbr_03",
    onBackClick: () -> Unit = {}
) {
    val allCards = remember { RosterSeedData.allCards }
    var card1 by remember {
        mutableStateOf(allCards.find { it.id == initialCard1Id } ?: allCards[0])
    }
    var card2 by remember {
        mutableStateOf(allCards.find { it.id == initialCard2Id } ?: allCards[3])
    }

    var selectingForSlot by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()

    val team1Color = remember(card1.primaryColorHex) {
        parseColorHex(card1.primaryColorHex, Color(0xFFE8002D))
    }
    val team2Color = remember(card2.primaryColorHex) {
        parseColorHex(card2.primaryColorHex, Color(0xFF001A30))
    }

    // Infinite shimmer for the electric lightning divider
    val infiniteTransition = rememberInfiniteTransition(label = "lightningShimmer")
    val lightningOffset by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightningOffset"
    )
    val vsPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vsPulseScale"
    )

    val bgColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Capture container for 9:16 Social Share
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                }
        ) {
            // ================= 1. DIAGONAL DUAL-TEAM BACKGROUND =================
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Top-Left Team 1 Polygon
                val path1 = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(0f, h * 0.65f)
                    close()
                }
                drawPath(
                    path = path1,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            team1Color.copy(alpha = 0.55f),
                            team1Color.copy(alpha = 0.20f),
                            bgColor
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w, h * 0.65f)
                    )
                )

                // Bottom-Right Team 2 Polygon
                val path2 = Path().apply {
                    moveTo(w, 0f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    lineTo(0f, h * 0.65f)
                    close()
                }
                drawPath(
                    path = path2,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            bgColor,
                            team2Color.copy(alpha = 0.20f),
                            team2Color.copy(alpha = 0.55f)
                        ),
                        start = Offset(0f, h * 0.65f),
                        end = Offset(w, h)
                    )
                )

                // ================= 2. ZIGZAG ELECTRIC LIGHTNING SEAM =================
                val lightningPath = Path().apply {
                    moveTo(w + 10f, -10f)
                    val steps = 14
                    for (i in 1..steps) {
                        val fraction = i / steps.toFloat()
                        val baseX = w * (1f - fraction)
                        val baseY = (h * 0.65f) * fraction
                        val jitter = if (i % 2 == 0) lightningOffset else -lightningOffset
                        lineTo(baseX + jitter, baseY + jitter * 0.5f)
                    }
                    lineTo(-10f, h * 0.65f)
                }

                // Lightning Glow
                drawPath(
                    path = lightningPath,
                    color = Color(0xFF00F0FF).copy(alpha = 0.35f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f)
                )
                // Lightning Core Stroke
                drawPath(
                    path = lightningPath,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFFFFF), Color(0xFFFFEA00), Color(0xFF00F0FF))
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
                )
            }

            // ================= 3. SCROLLABLE ARENA CONTENT =================
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 64.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Title
                item {
                    Text(
                        text = "F1 2026 BATTLE ARENA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "HEAD-TO-HEAD TELEMETRY & ATTACK SHOWDOWN",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )
                }

                // Dual Angled Cards & Central VS Emblem
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Left Card (Tilted -10 degrees)
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(185.dp)
                                .rotate(-10f)
                                .clickable { selectingForSlot = 1 }
                        ) {
                            Interactive3DCard(
                                card = card1,
                                enableFlip = false,
                                isHoloVariant = true
                            )
                        }

                        // Right Card (Tilted +10 degrees)
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(185.dp)
                                .rotate(10f)
                                .clickable { selectingForSlot = 2 }
                        ) {
                            Interactive3DCard(
                                card = card2,
                                enableFlip = false,
                                isHoloVariant = true
                            )
                        }

                        // Central Pulsating "VS" Badge
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(vsPulseScale)
                                .shadow(24.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFFFF1801), Color(0xFF6B0000), Color(0xFF1A0000))
                                    )
                                )
                                .border(
                                    3.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFD700), Color.White, Color(0xFFFFD700))
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VS",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                color = Color.White
                            )
                        }
                    }
                }

                // Swap Combatant Buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FilledTonalButton(
                            onClick = { selectingForSlot = 1 },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Swap Left", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalButton(
                            onClick = { selectingForSlot = 2 },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Swap Right", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Telemetry Comparison Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "TELEMETRY RADAR BENCHMARK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Telemetry Stats Bars
                            ComparisonStatRow("Qualifying Pace", card1.radarStats.qualifyingPace, card2.radarStats.qualifyingPace, team1Color, team2Color)
                            ComparisonStatRow("Racecraft", card1.radarStats.racecraft, card2.radarStats.racecraft, team1Color, team2Color)
                            ComparisonStatRow("Tyre Management", card1.radarStats.tyreManagement, card2.radarStats.tyreManagement, team1Color, team2Color)
                            ComparisonStatRow("Wet Skill", card1.radarStats.wetSkill, card2.radarStats.wetSkill, team1Color, team2Color)
                            ComparisonStatRow("Consistency", card1.radarStats.consistency, card2.radarStats.consistency, team1Color, team2Color)
                            ComparisonStatRow("Overall (OVR)", card1.ovr, card2.ovr, team1Color, team2Color)
                        }
                    }
                }

                // Attack Moves Comparison
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "SIGNATURE MOVE SHOWDOWN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            MoveShowdownRow(card1.name, card1.move1.title, card1.move1.power, card2.name, card2.move1.title, card2.move1.power)
                            Spacer(Modifier.height(8.dp))
                            MoveShowdownRow(card1.name, card1.move2.title, card1.move2.power, card2.name, card2.move2.title, card2.move2.power)
                        }
                    }
                }

                // 9:16 Social Share Button
                item {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                shareBattleStory(context, graphicsLayer)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "SHARE 9:16 STORY SHOWDOWN",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        shareBattleStory(context, graphicsLayer)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Story",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Modal Bottom Sheet to Swap Combatant
        if (selectingForSlot != null) {
            ModalBottomSheet(
                onDismissRequest = { selectingForSlot = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SELECT FIGHTER FOR SLOT #${selectingForSlot}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(allCards) { selectableCard ->
                            Box(
                                modifier = Modifier
                                    .width(130.dp)
                                    .clickable {
                                        if (selectingForSlot == 1) {
                                             card1 = selectableCard
                                        } else {
                                             card2 = selectableCard
                                        }
                                        selectingForSlot = null
                                    }
                            ) {
                                Interactive3DCard(
                                    card = selectableCard,
                                    enableFlip = false,
                                    isHoloVariant = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonStatRow(
    statName: String,
    val1: Int,
    val2: Int,
    color1: Color,
    color2: Color
) {
    val maxVal = 100f
    val animVal1 by animateFloatAsState(targetValue = val1 / maxVal, label = "animVal1")
    val animVal2 by animateFloatAsState(targetValue = val2 / maxVal, label = "animVal2")

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$val1",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (val1 > val2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (val1 > val2) {
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Leader",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
            Text(
                text = statName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (val2 > val1) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Leader",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = "$val2",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (val2 > val1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Left Bar (Card 1)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animVal1)
                        .align(Alignment.CenterEnd)
                        .background(color1)
                )
            }

            // Right Bar (Card 2)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animVal2)
                        .align(Alignment.CenterStart)
                        .background(color2)
                )
            }
        }
    }
}

@Composable
private fun MoveShowdownRow(
    driver1: String,
    move1Title: String,
    move1Power: Int,
    driver2: String,
    move2Title: String,
    move2Power: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(driver1, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(move1Title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Power: $move1Power", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(20.dp)
        )

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(driver2, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(move2Title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Power: $move2Power", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private suspend fun shareBattleStory(
    context: Context,
    graphicsLayer: androidx.compose.ui.graphics.layer.GraphicsLayer
) {
    try {
        val imageBitmap = graphicsLayer.toImageBitmap()
        val androidBitmap = imageBitmap.asAndroidBitmap()

        withContext(Dispatchers.IO) {
            val imagesFolder = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(imagesFolder, "tcg_battle_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                clipData = android.content.ClipData.newRawUri(null, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "F1 GridTCG 2026 Head-to-Head Clash")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                context.startActivity(Intent.createChooser(shareIntent, "Share Battle Story"))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun parseColorHex(hex: String, fallback: Color): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorLong = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorLong)
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}
