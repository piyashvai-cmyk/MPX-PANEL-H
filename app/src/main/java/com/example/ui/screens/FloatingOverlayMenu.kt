package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlowButton
import com.example.ui.components.ModSliderRow
import com.example.ui.components.ModSwitchRow
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GlassOverlay
import com.example.ui.theme.MafiaRedLight
import com.example.ui.theme.MafiaRedPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PanelViewModel
import kotlin.math.roundToInt

@Composable
fun FloatingOverlayMenu(
    viewModel: PanelViewModel,
    modifier: Modifier = Modifier
) {
    val modFeatures by viewModel.modFeatures.collectAsState()

    // Floating bubble draggable coordinates
    var bubbleOffsetX by remember { mutableFloatStateOf(40f) }
    var bubbleOffsetY by remember { mutableFloatStateOf(350f) }

    // Floating menu window draggable offset
    var menuOffsetX by remember { mutableFloatStateOf(0f) }
    var menuOffsetY by remember { mutableFloatStateOf(0f) }

    var activeTab by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "snowflakePulse")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    if (!modFeatures.isOverlayActive) return

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // 1. Draggable Floating Snowflake Bubble
        Box(
            modifier = Modifier
                .offset { IntOffset(bubbleOffsetX.roundToInt(), bubbleOffsetY.roundToInt()) }
                .size(62.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        bubbleOffsetX += dragAmount.x
                        bubbleOffsetY += dragAmount.y
                    }
                }
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = CyberCyan.copy(alpha = 0.5f),
                    spotColor = CyberCyan.copy(alpha = 0.8f)
                )
                .clip(CircleShape)
                .background(DarkBackground.copy(alpha = 0.9f))
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            CyberCyan,
                            MafiaRedPrimary,
                            CyberCyan
                        )
                    ),
                    shape = CircleShape
                )
                .clickable {
                    viewModel.toggleFloatingMenuExpanded()
                }
                .testTag("floating_bubble_trigger"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_snowflake_icon),
                contentDescription = "Floating Snowflake Menu",
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .scale(glowPulse),
                contentScale = ContentScale.Fit
            )
        }

        // 2. Expanded Floating Mod Menu Panel
        AnimatedVisibility(
            visible = modFeatures.isMenuExpanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(menuOffsetX.roundToInt(), menuOffsetY.roundToInt()) }
        ) {
            Box(
                modifier = Modifier
                    .width(340.dp)
                    .heightIn(max = 520.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = MafiaRedPrimary.copy(alpha = 0.4f),
                        spotColor = CyberCyan.copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(GlassOverlay.copy(alpha = modFeatures.overlayOpacity))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                MafiaRedPrimary,
                                CyberCyan.copy(alpha = 0.6f),
                                DarkSurfaceBorder
                            )
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .testTag("floating_mod_window")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header with Drag Handle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceElevated.copy(alpha = 0.95f))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    menuOffsetX += dragAmount.x
                                    menuOffsetY += dragAmount.y
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Drag Window",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.img_snowflake_icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MPX VIP MOD",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Minimize
                            IconButton(
                                onClick = { viewModel.setFloatingMenuExpanded(false) },
                                modifier = Modifier.size(28.dp).testTag("floating_minimize_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Minimize,
                                    contentDescription = "Minimize",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // Close/Hide
                            IconButton(
                                onClick = { viewModel.setFloatingMenuExpanded(false) },
                                modifier = Modifier.size(28.dp).testTag("floating_close_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MafiaRedLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Compact Tabs
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = DarkSurface,
                        contentColor = MafiaRedPrimary
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("AIM", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("ESP", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeTab == 2,
                            onClick = { activeTab = 2 },
                            text = { Text("BOOST", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeTab == 3,
                            onClick = { activeTab = 3 },
                            text = { Text("CONFIG", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    // Content Body Scrollable
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (activeTab) {
                            0 -> {
                                // AIMBOT
                                ModSwitchRow(
                                    title = "Auto Headshot 90%",
                                    subtitle = "Auto snaps to head target",
                                    checked = modFeatures.autoHeadshot,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(autoHeadshot = chk) }
                                    },
                                    badge = "VVIP"
                                )
                                ModSwitchRow(
                                    title = "Aim Lock",
                                    subtitle = "Keeps crosshair locked on enemy",
                                    checked = modFeatures.aimLock,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(aimLock = chk) }
                                    }
                                )
                                ModSwitchRow(
                                    title = "Aim When Scoping",
                                    subtitle = "Trigger only in ADS mode",
                                    checked = modFeatures.aimScope,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(aimScope = chk) }
                                    }
                                )
                                ModSliderRow(
                                    title = "Aim FOV Radius",
                                    value = modFeatures.aimFov,
                                    onValueChange = { fov ->
                                        viewModel.updateModFeature { it.copy(aimFov = fov) }
                                    },
                                    valueRange = 30f..360f,
                                    unit = "°"
                                )
                                ModSwitchRow(
                                    title = "Zero Recoil",
                                    subtitle = "Laser gun accuracy",
                                    checked = modFeatures.noRecoil,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(noRecoil = chk) }
                                    }
                                )
                            }
                            1 -> {
                                // ESP
                                ModSwitchRow(
                                    title = "ESP Laser Line",
                                    subtitle = "Line pointer to enemies",
                                    checked = modFeatures.espLine,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(espLine = chk) }
                                    },
                                    badgeColor = CyberCyan,
                                    badge = "ESP"
                                )
                                ModSwitchRow(
                                    title = "ESP 2D Box",
                                    subtitle = "Target bounding box",
                                    checked = modFeatures.espBox,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(espBox = chk) }
                                    }
                                )
                                ModSwitchRow(
                                    title = "ESP Health & HP",
                                    subtitle = "Health status indicator",
                                    checked = modFeatures.espHealth,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(espHealth = chk) }
                                    }
                                )
                                ModSwitchRow(
                                    title = "ESP Distance (M)",
                                    subtitle = "Real-time range in meters",
                                    checked = modFeatures.espDistance,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(espDistance = chk) }
                                    }
                                )
                                ModSwitchRow(
                                    title = "ESP Player Name",
                                    subtitle = "Display username tags",
                                    checked = modFeatures.espName,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(espName = chk) }
                                    }
                                )
                            }
                            2 -> {
                                // BOOST
                                ModSwitchRow(
                                    title = "100% Anti-Ban Shield",
                                    subtitle = "Safe memory spoofing",
                                    checked = modFeatures.antiBanProtection,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(antiBanProtection = chk) }
                                    },
                                    badgeColor = NeonGreen,
                                    badge = "ACTIVE"
                                )
                                ModSwitchRow(
                                    title = "Anti-Blacklist",
                                    subtitle = "Prevents device ban",
                                    checked = modFeatures.antiBlacklist,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(antiBlacklist = chk) }
                                    }
                                )
                                ModSwitchRow(
                                    title = "Ultra 120 FPS",
                                    subtitle = "Game frame rate unlock",
                                    checked = modFeatures.ultra120Fps,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(ultra120Fps = chk) }
                                    }
                                )
                                ModSwitchRow(
                                    title = "Ping Stabilizer",
                                    subtitle = "Low jitter game UDP",
                                    checked = modFeatures.pingStabilizer,
                                    onCheckedChange = { chk ->
                                        viewModel.updateModFeature { it.copy(pingStabilizer = chk) }
                                    }
                                )
                            }
                            3 -> {
                                // CONFIG
                                ModSliderRow(
                                    title = "Menu Transparency",
                                    value = modFeatures.overlayOpacity * 100f,
                                    onValueChange = { alpha ->
                                        viewModel.updateModFeature { it.copy(overlayOpacity = (alpha / 100f).coerceIn(0.4f, 1f)) }
                                    },
                                    valueRange = 40f..100f,
                                    unit = "%"
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                GlowButton(
                                    text = "CLOSE MOD INJECTOR",
                                    onClick = {
                                        viewModel.setOverlayActive(false)
                                    },
                                    isPrimaryRed = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
