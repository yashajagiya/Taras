package com.example.taras.view.tcg.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
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
import android.graphics.Bitmap
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.Transformation
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.tcg.model.CardMove
import com.example.taras.core.tcg.model.CardRole
import com.example.taras.core.tcg.model.RarityTier

/**
 * Ensures Formula 1 CDN URLs use the high-resolution master crop (top 1080x1350)
 * directly from Cloudinary, bypassing compressed thumbnails and client-side blurring.
 */
fun getOptimizedAvatarUrl(rawUrl: String): String {
    return if (rawUrl.contains("media.formula1.com/image/upload/")) {
        rawUrl.replace(
            Regex("image/upload/(?:c_[^/]+/)?(?:q_[^/]+/)?(?:d_[^/]+/)?"),
            "image/upload/c_crop,g_north,w_1080,h_1350/q_auto:best/"
        )
    } else {
        rawUrl
    }
}

/**
 * Coil Transformation kept for backwards compatibility.
 * Note: RosterSeedData now uses server-side 1080x1350 waist-up cropping.
 */
class DriverWaistCropTransformation : Transformation() {
    override val cacheKey: String = "DriverWaistCropTransformation_v3"

    override suspend fun transform(input: Bitmap, size: coil3.size.Size): Bitmap {
        if (input.height <= 1350 && input.width >= 1000) {
            return input
        }
        val targetHeight = (input.height * 0.50f).toInt().coerceIn(1, input.height)
        return Bitmap.createBitmap(input, 0, 0, input.width, targetHeight)
    }
}

val DriverNavelCropScale = ContentScale.Fit
val DriverWaistCropScale = ContentScale.Fit

@Composable
fun CardFrontLayout(
    card: TcgCardEntity,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)
    val teamPrimaryColor = remember(card.primaryColorHex) {
        parseHexColor(card.primaryColorHex, Color(0xFFE10600))
    }
    val teamSecondaryColor = remember(card.secondaryColorHex) {
        parseHexColor(card.secondaryColorHex, Color.White)
    }

    // Minimal pastel holographic rainbow sweep gradient anchored by the tier metal
    val borderBrush = when (card.tier) {
        RarityTier.S_TIER -> Brush.sweepGradient(
            listOf(
                Color(0xFFFFD700), // Gold Base
                Color(0xFFFFB3BA), // Soft Iridescent Rose
                Color(0xFFFFDFBA), // Warm Solar Gold
                Color(0xFFBAFFC9), // Soft Mint Sheen
                Color(0xFFBAE1FF), // Soft Sky Iridescence
                Color(0xFFE8BAFF), // Soft Lilac Shimmer
                Color(0xFFFFD700)  // Gold Base
            )
        )
        RarityTier.A_TIER -> Brush.sweepGradient(
            listOf(
                Color(0xFFE0E0E0), // Chrome Silver Base
                Color(0xFFFFC6D0), // Soft Prism Pink
                Color(0xFFFFF6B8), // Soft Lemon Gold
                Color(0xFFC8F7DC), // Soft Prism Mint
                Color(0xFFBCE7FD), // Soft Prism Ice Blue
                Color(0xFFE4D2F7), // Soft Prism Lavender
                Color(0xFFE0E0E0)  // Chrome Silver Base
            )
        )
        RarityTier.B_TIER -> Brush.sweepGradient(
            listOf(
                Color(0xFFCD7F32), // Bronze Base
                Color(0xFFFFA07A), // Soft Salmon
                Color(0xFFF7DC6F), // Warm Gold
                Color(0xFFA2D9CE), // Soft Opal Mint
                Color(0xFF85C1E9), // Soft Sky Blue
                Color(0xFFBB8FCE), // Soft Amethyst
                Color(0xFFCD7F32)  // Bronze Base
            )
        )
        RarityTier.C_TIER -> Brush.sweepGradient(
            listOf(
                Color(0xFF7F8C8D), // Matte Steel Base
                Color(0xFFB2BABB), // Light Slate
                Color(0xFFD5DBDB), // Platinum Mist
                Color(0xFFA3E4D7), // Faint Prism Mint
                Color(0xFFD7BDE2), // Faint Lilac
                Color(0xFF7F8C8D)  // Matte Steel Base
            )
        )
    }

    val watermarkNumber = remember(card.driverNumber, card.code) {
        val cleaned = card.driverNumber.removePrefix("#").trim()
        if (cleaned.isNotBlank()) cleaned else card.code
    }

    Box(
        modifier = modifier
            .aspectRatio(5f / 7f)
            .shadow(16.dp, cardShape)
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        teamPrimaryColor,
                        darkenColor(teamPrimaryColor, 0.65f),
                        Color(0xFF0A0D15)
                    )
                )
            )
            .border(3.dp, borderBrush, cardShape)
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ================= 1. POKÉMON-STYLE TOP HEADER BAR =================
            TopHeaderBar(card = card, tierColor = card.tier.borderColor)

            // ================= 2. ARTWORK WINDOW WITH GIANT NUMBER WATERMARK =================
            ArtworkWindow(
                card = card,
                teamPrimaryColor = teamPrimaryColor,
                watermarkNumber = watermarkNumber,
                tierColor = card.tier.borderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
                    .padding(vertical = 3.dp)
            )

            // ================= 3. POKÉDEX-STYLE F1 TECHNICAL SPECS STRIP =================
            SpecsStrip(card = card)

            // ================= 4. DETAILED RACING ATTACKS & TELEMETRY SECTION =================
            MovesSection(
                move1 = card.move1,
                move2 = card.move2,
                tierColor = card.tier.borderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            )

            // ================= 5. COMBAT MATCHUPS BAR (WEAKNESS / RESISTANCE / PIT) =================
            MatchupBar(card = card)

            // ================= 6. COLLECTOR FOOTER =================
            CollectorFooter(card = card)
        }
    }
}

@Composable
private fun TopHeaderBar(
    card: TcgCardEntity,
    tierColor: Color
) {
    val headerShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(headerShape)
            .background(Color(0x55000000))
            .border(1.dp, tierColor.copy(alpha = 0.50f), headerShape)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Boxy Tier Badge ("S", "A", "B", "C") in the top corner
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(tierColor.copy(alpha = 0.30f))
                .border(1.5.dp, tierColor, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = card.tier.badgeLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = tierColor,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        }

        // Center: Driver Name & Team Name in middle top of the card
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${card.name.uppercase()} ${card.driverNumber}".trim(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.3.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = card.teamName.uppercase(),
                fontSize = 7.2.sp,
                fontWeight = FontWeight.Bold,
                color = tierColor.copy(alpha = 0.90f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        // Right: OVR Pace Rating with Electric Bolt Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${card.ovr}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "PACE",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                color = tierColor
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = "Pace Energy",
                tint = tierColor,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
private fun ArtworkWindow(
    card: TcgCardEntity,
    teamPrimaryColor: Color,
    watermarkNumber: String,
    tierColor: Color,
    modifier: Modifier = Modifier
) {
    val windowShape = RoundedCornerShape(10.dp)
    val context = LocalContext.current
    val highResAvatarUrl = remember(card.avatarUrl) {
        getOptimizedAvatarUrl(card.avatarUrl)
    }

    val imageRequest = remember(highResAvatarUrl) {
        ImageRequest.Builder(context)
            .data(highResAvatarUrl)
            .crossfade(true)
            .build()
    }

    Box(
        modifier = modifier
            .clip(windowShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        teamPrimaryColor,
                        teamPrimaryColor,
                        darkenColor(teamPrimaryColor, 0.70f)
                    )
                )
            )
            .border(1.2.dp, tierColor.copy(alpha = 0.85f), windowShape)
    ) {
        // GIANT WATERMARK DRIVER NUMBER - In the back of the driver
        Text(
            text = watermarkNumber,
            fontSize = if (watermarkNumber.length <= 2) 160.sp else 100.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = Color.White.copy(alpha = 0.28f),
            letterSpacing = (-4).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = 4.dp)
        )

        // Driver Portrait - Head down to waist (top 50% high-res 1080x1350 crop)
        AsyncImage(
            model = imageRequest,
            contentDescription = card.name,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp)
                .clip(windowShape)
        )
    }
}

@Composable
private fun SpecsStrip(card: TcgCardEntity) {
    val specsText = if (card.role == CardRole.DRIVER) {
        "Debut: ${card.debutYear} • ${card.nationality.uppercase()}"
    } else {
        "Engine: ${card.powertrain} • Debut: ${card.debutYear}"
    }
    val specsShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(specsShape)
            .background(Color(0x99000000))
            .border(0.5.dp, Color(0x33FFFFFF), specsShape)
            .padding(vertical = 2.5.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = specsText,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFDDDDDD),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MovesSection(
    move1: CardMove,
    move2: CardMove,
    tierColor: Color,
    modifier: Modifier = Modifier
) {
    val movesShape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .clip(movesShape)
            .background(Color(0x88000000))
            .border(1.dp, Color(0x22FFFFFF), movesShape)
            .padding(horizontal = 7.dp, vertical = 5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            DetailedMoveItem(move = move1, tierColor = tierColor)
            HorizontalDivider(thickness = 0.6.dp, color = Color(0x25FFFFFF))
            DetailedMoveItem(move = move2, tierColor = tierColor)
        }
    }
}

@Composable
private fun DetailedMoveItem(move: CardMove, tierColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Energy Badges + Attack Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (energy in move.energyCosts) {
                        EnergyIcon(energyType = energy, size = 14.dp)
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = move.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            // Power / Damage Number
            Text(
                text = "${move.power}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = tierColor
            )
        }
        // Attack Telemetry & Racing Lore Description
        Text(
            text = move.description,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 9.5.sp,
            color = Color(0xFFCCCCCC),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

@Composable
private fun MatchupBar(card: TcgCardEntity) {
    val matchupShape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(matchupShape)
            .background(Color(0x66000000))
            .border(0.5.dp, Color(0x22FFFFFF), matchupShape)
            .padding(horizontal = 6.dp, vertical = 2.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shortWeakness = card.weaknessText.take(18)
        val shortResistance = card.resistanceText.take(18)
        Text(
            text = "Weak: $shortWeakness",
            fontSize = 6.8.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFF6B6B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Res: $shortResistance",
            fontSize = 6.8.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4ECDC4),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Pit: ${card.pitCost}x",
            fontSize = 6.8.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFFD700),
            maxLines = 1
        )
    }
}

@Composable
private fun CollectorFooter(card: TcgCardEntity) {
    val cardNumberFormatted = card.cardNumber.toString().padStart(3, '0')
    val rarityName = when (card.tier) {
        RarityTier.S_TIER -> "Gold Rare"
        RarityTier.A_TIER -> "Silver Chrome"
        RarityTier.B_TIER -> "Bronze Metal"
        RarityTier.C_TIER -> "Matte Steel"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Illus. Taras TCG Studio",
            fontSize = 6.5.sp,
            color = Color(0xFF888888)
        )
        Text(
            text = "$cardNumberFormatted/023 $rarityName",
            fontSize = 6.5.sp,
            fontWeight = FontWeight.Bold,
            color = card.tier.borderColor
        )
        Text(
            text = "© 2026 Taras F1",
            fontSize = 6.5.sp,
            color = Color(0xFF888888)
        )
    }
}

fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorLong = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(0xFF000000 or colorLong)
        } else if (cleanHex.length == 8) {
            Color(colorLong)
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

fun darkenColor(color: Color, factor: Float): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}
