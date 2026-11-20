package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.SlotConstants
import com.example.model.SlotSymbol
import com.example.model.SlotUiState
import com.example.model.SpinOutcome
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldWin
import com.example.ui.theme.IvoryText
import com.example.ui.theme.MidnightVelvet
import com.example.ui.theme.MutedLavender
import com.example.ui.theme.ReelWellDark
import com.example.ui.theme.RubyDeep
import com.example.ui.theme.RubyGlow
import com.example.ui.theme.RubyNeon
import com.example.ui.theme.SoftCoral
import com.example.ui.theme.VegasGold
import com.example.ui.theme.VegasGoldBright
import com.example.ui.theme.VegasGoldDeep

@Composable
fun MarqueeHeader(
    isMuted: Boolean,
    isSpinning: Boolean,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marquee_bulbs")
    val bulbPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpinning) 320 else 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bulb_phase",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DeepObsidian,
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.horizontalGradient(
                listOf(VegasGoldDeep, VegasGoldBright, RubyNeon, VegasGoldBright, VegasGoldDeep)
            ),
        ),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BulbStrip(phase = bulbPhase, count = 11)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.size(48.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.marquee_title),
                        style = MaterialTheme.typography.displaySmall,
                        color = VegasGoldBright,
                    )
                    Text(
                        text = stringResource(R.string.marquee_subtitle),
                        style = MaterialTheme.typography.labelMedium,
                        color = RubyGlow,
                    )
                }
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("mute_toggle_button"),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = ElevatedCharcoal,
                        contentColor = if (isMuted) MutedLavender else VegasGold,
                    ),
                ) {
                    Icon(
                        imageVector = if (isMuted) {
                            Icons.AutoMirrored.Filled.VolumeOff
                        } else {
                            Icons.AutoMirrored.Filled.VolumeUp
                        },
                        contentDescription = stringResource(
                            if (isMuted) R.string.unmute_sound_cd else R.string.mute_sound_cd
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            BulbStrip(phase = 1f - bulbPhase, count = 11)
        }
    }
}

@Composable
private fun BulbStrip(phase: Float, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val lit = if (index % 2 == 0) phase > 0.45f else phase <= 0.55f
            val color = when {
                lit && index % 3 == 0 -> RubyGlow
                lit -> VegasGoldBright
                else -> ElevatedCharcoal
            }
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Composable
fun BalanceHudCard(
    balance: Int,
    modifier: Modifier = Modifier,
) {
    val animatedBalance by animateIntAsState(
        targetValue = balance,
        animationSpec = tween(durationMillis = 360),
        label = "balance_counter",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ElevatedCharcoal,
        border = BorderStroke(1.5.dp, VegasGold.copy(alpha = 0.55f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(VegasGoldBright, VegasGoldDeep))
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = null,
                        tint = MidnightVelvet,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.balance_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedLavender,
                    )
                    Text(
                        text = "$animatedBalance ${stringResource(R.string.coins_unit)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                        ),
                        color = VegasGoldBright,
                        modifier = Modifier.testTag("balance_text"),
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = RubyDeep.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, RubyGlow.copy(alpha = 0.7f)),
            ) {
                Text(
                    text = stringResource(R.string.spin_cost_badge),
                    style = MaterialTheme.typography.labelMedium,
                    color = IvoryText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
fun SlotCabinet(
    reels: List<SlotSymbol>,
    lockedReels: List<Boolean>,
    winningIndices: Set<Int>,
    outcome: SpinOutcome,
    modifier: Modifier = Modifier,
) {
    val cabinetBorderColor by animateColorAsState(
        targetValue = when (outcome) {
            SpinOutcome.THREE_MATCH -> EmeraldWin
            SpinOutcome.TWO_MATCH -> VegasGoldBright
            SpinOutcome.SPINNING -> RubyGlow
            else -> VegasGold
        },
        animationSpec = tween(250),
        label = "cabinet_border",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF28173D), DeepObsidian, Color(0xFF1A0D29))
                )
            )
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    listOf(VegasGoldDeep, cabinetBorderColor, VegasGoldDeep)
                ),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            reels.forEachIndexed { index, symbol ->
                val isLocked = lockedReels.getOrElse(index) { true }
                val isWinning = index in winningIndices
                ReelSlotWindow(
                    symbol = symbol,
                    isLocked = isLocked,
                    isWinning = isWinning,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reel_$index"),
                )
            }
        }
    }
}

@Composable
private fun ReelSlotWindow(
    symbol: SlotSymbol,
    isLocked: Boolean,
    isWinning: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isWinning -> 1.07f
            isLocked -> 1f
            else -> 0.94f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "reel_scale",
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isWinning -> EmeraldWin
            !isLocked -> RubyGlow.copy(alpha = 0.7f)
            else -> VegasGold.copy(alpha = 0.4f)
        },
        animationSpec = tween(200),
        label = "reel_border",
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF06030C),
                        ReelWellDark,
                        Color(0xFF1B132B),
                        ReelWellDark,
                        Color(0xFF06030C),
                    )
                )
            )
            .border(
                width = if (isWinning) 2.5.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(vertical = 16.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            SlotSymbolArtwork(
                symbol = symbol,
                iconSize = 60.dp,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = symbol.displayName.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = if (isWinning) EmeraldWin else MutedLavender,
            maxLines = 1,
        )
    }
}

@Composable
fun ResultBanner(
    outcome: SpinOutcome,
    message: String,
    modifier: Modifier = Modifier,
) {
    val (bgColor, borderColor, textColor) = when (outcome) {
        SpinOutcome.THREE_MATCH -> Triple(EmeraldContainer, EmeraldWin, EmeraldWin)
        SpinOutcome.TWO_MATCH -> Triple(EmeraldContainer.copy(alpha = 0.75f), VegasGoldBright, VegasGoldBright)
        SpinOutcome.OUT_OF_COINS -> Triple(CoralContainer, SoftCoral, SoftCoral)
        SpinOutcome.SPINNING -> Triple(ElevatedCharcoal, RubyGlow.copy(alpha = 0.6f), IvoryText)
        else -> Triple(ElevatedCharcoal, VegasGold.copy(alpha = 0.35f), IvoryText)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("result_message"),
        )
    }
}

@Composable
fun ActionControls(
    uiState: SlotUiState,
    onSpinClick: () -> Unit,
    onResetCoinsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onSpinClick,
            enabled = uiState.canSpin,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .minimumInteractiveComponentSize()
                .testTag("spin_button"),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RubyNeon,
                contentColor = IvoryText,
                disabledContainerColor = ElevatedCharcoal,
                disabledContentColor = MutedLavender.copy(alpha = 0.6f),
            ),
            border = BorderStroke(
                width = 2.dp,
                color = if (uiState.canSpin) VegasGoldBright else MutedLavender.copy(alpha = 0.25f),
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp,
                disabledElevation = 0.dp,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Casino,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (uiState.isSpinning) {
                    stringResource(R.string.spinning_button)
                } else {
                    "${stringResource(R.string.spin_button)}  •  -${SlotConstants.SPIN_COST} ${stringResource(R.string.coins_unit)}"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        if (uiState.isOutOfCoins) {
            Button(
                onClick = onResetCoinsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("reset_coins_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VegasGold,
                    contentColor = MidnightVelvet,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.reset_coins_cd),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.reset_coins_button),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
fun PaytableCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("paytable_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
        border = BorderStroke(1.5.dp, VegasGold.copy(alpha = 0.45f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.paytable_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = VegasGoldBright,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SlotSymbol.entries.forEach { symbol ->
                        SlotSymbolArtwork(
                            symbol = symbol,
                            iconSize = 24.dp,
                            includeContentDescription = false,
                        )
                    }
                }
            }

            HorizontalDivider(color = VegasGold.copy(alpha = 0.22f))

            PaytableRow(
                label = stringResource(R.string.paytable_three_match),
                value = stringResource(R.string.paytable_three_payout),
                valueColor = EmeraldWin,
            )
            PaytableRow(
                label = stringResource(R.string.paytable_two_match),
                value = stringResource(R.string.paytable_two_payout),
                valueColor = VegasGoldBright,
            )
            PaytableRow(
                label = stringResource(R.string.paytable_spin_cost),
                value = stringResource(R.string.paytable_spin_cost_value),
                valueColor = SoftCoral,
            )
        }
    }
}

@Composable
private fun PaytableRow(
    label: String,
    value: String,
    valueColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = IvoryText,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = valueColor,
        )
    }
}
