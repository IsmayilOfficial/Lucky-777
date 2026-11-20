package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActionControls
import com.example.ui.components.BalanceHudCard
import com.example.ui.components.MarqueeHeader
import com.example.ui.components.PaytableCard
import com.example.ui.components.ResultBanner
import com.example.ui.components.SlotCabinet
import com.example.ui.theme.MidnightVelvet
import com.example.ui.theme.PlumSpotlight
import com.example.viewmodel.SlotMachineViewModel

@Composable
fun SlotMachineScreen(
    viewModel: SlotMachineViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(PlumSpotlight, MidnightVelvet),
                    radius = 1200f,
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.TopCenter,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            val isWideScreen = maxWidth >= 640.dp

            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 960.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1.15f),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        MarqueeHeader(
                            isMuted = uiState.isMuted,
                            isSpinning = uiState.isSpinning,
                            onToggleMute = viewModel::toggleMute,
                        )
                        SlotCabinet(
                            reels = uiState.reels,
                            lockedReels = uiState.lockedReels,
                            winningIndices = uiState.winningIndices,
                            outcome = uiState.outcome,
                        )
                        ResultBanner(
                            outcome = uiState.outcome,
                            message = uiState.resultMessage,
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.85f),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        BalanceHudCard(balance = uiState.balance)
                        ActionControls(
                            uiState = uiState,
                            onSpinClick = viewModel::spin,
                            onResetCoinsClick = viewModel::resetCoins,
                        )
                        PaytableCard()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MarqueeHeader(
                        isMuted = uiState.isMuted,
                        isSpinning = uiState.isSpinning,
                        onToggleMute = viewModel::toggleMute,
                    )
                    BalanceHudCard(balance = uiState.balance)
                    SlotCabinet(
                        reels = uiState.reels,
                        lockedReels = uiState.lockedReels,
                        winningIndices = uiState.winningIndices,
                        outcome = uiState.outcome,
                    )
                    ResultBanner(
                        outcome = uiState.outcome,
                        message = uiState.resultMessage,
                    )
                    ActionControls(
                        uiState = uiState,
                        onSpinClick = viewModel::spin,
                        onResetCoinsClick = viewModel::resetCoins,
                    )
                    PaytableCard()
                }
            }
        }
    }
}
