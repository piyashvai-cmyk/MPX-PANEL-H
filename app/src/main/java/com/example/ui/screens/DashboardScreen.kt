package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlowButton
import com.example.ui.components.ModSliderRow
import com.example.ui.components.ModSwitchRow
import com.example.ui.components.NeonCard
import com.example.ui.components.StatBadgeCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GlassContainer
import com.example.ui.theme.MafiaRedDark
import com.example.ui.theme.MafiaRedLight
import com.example.ui.theme.MafiaRedPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameProfile
import com.example.viewmodel.PanelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PanelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentKey by viewModel.currentSessionKey.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val modFeatures by viewModel.modFeatures.collectAsState()
    val systemStats by viewModel.systemStats.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Aimbot, 1: ESP, 2: Boost
    var showNoticeDialog by remember { mutableStateOf(false) }

    // Pulsing animation for injector circle
    val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val expiryString = remember(currentKey) {
        if (currentKey?.expiresAt ?: 0L > 0L) {
            dateFormat.format(Date(currentKey!!.expiresAt))
        } else {
            "Lifetime Unlimited"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_mafia_logo),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MafiaRedPrimary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MPX VIP",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(VipGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, VipGold, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = currentKey?.tier ?: "VIP",
                                    color = VipGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Exp: $expiryString",
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Top Actions: Bell, Admin, Logout
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showNoticeDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notification),
                            contentDescription = "Notification",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.AdminPanel) },
                        modifier = Modifier.size(36.dp).testTag("admin_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = VipGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.size(36.dp).testTag("logout_button")
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Logout",
                            tint = MafiaRedLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Featured Cyber Gaming Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MafiaRedPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_gaming_banner),
                    contentDescription = "Gaming VIP Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    DarkBackground.copy(alpha = 0.85f),
                                    DarkBackground.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "VIP INJECTOR ENGINE V5.2",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "100% Anti-Ban Bypass • Safe for Main IDs",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(NeonGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STATUS: BYPASS ACTIVE",
                            color = NeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // LIVE SYSTEM STATS ROW
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "DEVICE TELEMETRY",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBadgeCard(
                        title = "RAM",
                        value = "${systemStats.ramUsedPercent}% (${String.format("%.1f", systemStats.ramUsedGb)}GB)",
                        iconRes = R.drawable.ic_memory,
                        accentColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        title = "PING",
                        value = "${systemStats.pingMs} ms",
                        iconRes = R.drawable.ic_signal,
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBadgeCard(
                        title = "STORAGE",
                        value = "${systemStats.storageFreeGb} GB Free",
                        iconRes = R.drawable.ic_storage,
                        accentColor = VipGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        title = "FPS BOOST",
                        value = "${systemStats.fps} FPS Max",
                        iconRes = R.drawable.ic_controller,
                        accentColor = MafiaRedPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GAME SELECTOR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "TARGET GAME SELECTOR",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.gamesList) { game ->
                        val isSelected = game.id == selectedGame.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MafiaRedPrimary.copy(alpha = 0.18f) else DarkSurfaceElevated)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MafiaRedPrimary else DarkSurfaceBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.selectGame(game) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_controller),
                                        contentDescription = null,
                                        tint = if (isSelected) MafiaRedPrimary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = game.name,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = game.arch,
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${game.antiBanStatus}",
                                        color = NeonGreen,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MASTER INJECTOR ACTION CARD
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                borderColor = if (modFeatures.isOverlayActive) NeonGreen.copy(alpha = 0.7f) else MafiaRedPrimary.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (modFeatures.isOverlayActive) "MOD INJECTOR RUNNING" else "INJECTOR ENGINE STANDBY",
                        color = if (modFeatures.isOverlayActive) NeonGreen else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (modFeatures.isOverlayActive) "Floating Mod Menu is ACTIVE. Tap floating snowflake or button below to view controls."
                        else "Tap the button to inject scripts into ${selectedGame.name}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Big Pulsing Action Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        // Outer Pulsing Ring
                        if (modFeatures.isOverlayActive) {
                            Box(
                                modifier = Modifier
                                    .size(94.dp)
                                    .scale(pulseScale)
                                    .border(2.dp, NeonGreen.copy(alpha = 0.4f), CircleShape)
                            )
                        }

                        // Core Button
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    ambientColor = if (modFeatures.isOverlayActive) NeonGreen else MafiaRedPrimary,
                                    spotColor = if (modFeatures.isOverlayActive) NeonGreen else MafiaRedPrimary
                                )
                                .clip(CircleShape)
                                .background(
                                    if (modFeatures.isOverlayActive)
                                        Brush.radialGradient(listOf(NeonGreen, Color(0xFF00793D)))
                                    else
                                        Brush.radialGradient(listOf(MafiaRedLight, MafiaRedPrimary, MafiaRedDark))
                                )
                                .clickable {
                                    viewModel.toggleInjector()
                                    Toast.makeText(
                                        context,
                                        if (!modFeatures.isOverlayActive) "Injector Started! Floating menu active." else "Injector Stopped.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                                .testTag("injector_toggle_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = if (modFeatures.isOverlayActive) R.drawable.ic_stop else R.drawable.ic_play),
                                contentDescription = "Injector Action",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Quick Toggle Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (modFeatures.isOverlayActive) {
                            GlowButton(
                                text = if (modFeatures.isMenuExpanded) "HIDE FLOATING MENU" else "OPEN FLOATING MOD MENU",
                                onClick = { viewModel.toggleFloatingMenuExpanded() },
                                isPrimaryRed = false,
                                iconVector = Icons.Default.Visibility,
                                modifier = Modifier.fillMaxWidth(0.9f),
                                testTag = "open_floating_menu_btn"
                            )
                        } else {
                            GlowButton(
                                text = "START MOD INJECTOR",
                                onClick = {
                                    viewModel.toggleInjector()
                                },
                                isPrimaryRed = true,
                                iconVector = Icons.Default.PlayArrow,
                                modifier = Modifier.fillMaxWidth(0.9f),
                                testTag = "start_injector_btn"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // MOD FEATURES TABS & LIST
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "CONFIGURABLE MOD MODULES",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceElevated,
                    contentColor = MafiaRedPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "AIMBOT",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) MafiaRedPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "ESP VISUALS",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) CyberCyan else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "SECURITY & BOOST",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) NeonGreen else TextMuted
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // AIMBOT TAB
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModSwitchRow(
                                title = "Auto Aim Head (90% Headshot)",
                                subtitle = "Instantly snaps crosshair to enemy head hitbox",
                                checked = modFeatures.autoHeadshot,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(autoHeadshot = chk) }
                                },
                                badge = "VVIP"
                            )

                            ModSwitchRow(
                                title = "Aim Lock",
                                subtitle = "Locks target when firing weapons",
                                checked = modFeatures.aimLock,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(aimLock = chk) }
                                }
                            )

                            ModSwitchRow(
                                title = "Scope Trigger",
                                subtitle = "Only activate aim tracking when ADS scoping",
                                checked = modFeatures.aimScope,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(aimScope = chk) }
                                }
                            )

                            ModSliderRow(
                                title = "Aim Field of View (FOV)",
                                value = modFeatures.aimFov,
                                onValueChange = { fov ->
                                    viewModel.updateModFeature { it.copy(aimFov = fov) }
                                },
                                valueRange = 30f..360f,
                                unit = "°"
                            )

                            ModSwitchRow(
                                title = "Zero Weapon Recoil",
                                subtitle = "Eliminates gun spray and bullet dispersion",
                                checked = modFeatures.noRecoil,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(noRecoil = chk) }
                                },
                                badge = "STABLE"
                            )

                            ModSwitchRow(
                                title = "Magic Bullet Damage",
                                subtitle = "Increased bullet penetration & damage multiplier",
                                checked = modFeatures.magicBullet,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(magicBullet = chk) }
                                },
                                badge = "HOT"
                            )
                        }
                    }

                    1 -> {
                        // ESP VISUALS TAB
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModSwitchRow(
                                title = "ESP Line (Laser Tracer)",
                                subtitle = "Draws directional laser beam to enemy positions",
                                checked = modFeatures.espLine,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(espLine = chk) }
                                },
                                badgeColor = CyberCyan,
                                badge = "ESP"
                            )

                            ModSwitchRow(
                                title = "ESP 2D/3D Box",
                                subtitle = "Renders bounding box around enemy hitboxes",
                                checked = modFeatures.espBox,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(espBox = chk) }
                                }
                            )

                            ModSwitchRow(
                                title = "ESP Distance (Meters)",
                                subtitle = "Displays real-time distance to nearby enemies",
                                checked = modFeatures.espDistance,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(espDistance = chk) }
                                }
                            )

                            ModSwitchRow(
                                title = "ESP Health Bar",
                                subtitle = "Shows real-time remaining HP bar above enemy",
                                checked = modFeatures.espHealth,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(espHealth = chk) }
                                }
                            )

                            ModSwitchRow(
                                title = "ESP Player Name & Team",
                                subtitle = "Displays enemy in-game username",
                                checked = modFeatures.espName,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(espName = chk) }
                                }
                            )

                            ModSwitchRow(
                                title = "Grenade & Danger Alert",
                                subtitle = "Visual warning when thrown explosives approach",
                                checked = modFeatures.espGrenadeAlert,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(espGrenadeAlert = chk) }
                                },
                                badge = "SAFE"
                            )
                        }
                    }

                    2 -> {
                        // SECURITY & BOOST TAB
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModSwitchRow(
                                title = "Anti-Ban 100% Protection",
                                subtitle = "Real-time memory checksum spoofing & report blocker",
                                checked = modFeatures.antiBanProtection,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(antiBanProtection = chk) }
                                },
                                badgeColor = NeonGreen,
                                badge = "SHIELD"
                            )

                            ModSwitchRow(
                                title = "Anti-Blacklist Guard",
                                subtitle = "Prevents device IMEI and hardware ID blacklisting",
                                checked = modFeatures.antiBlacklist,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(antiBlacklist = chk) }
                                },
                                badgeColor = NeonGreen,
                                badge = "SECURE"
                            )

                            ModSwitchRow(
                                title = "Extreme 120 FPS Unlocker",
                                subtitle = "Overrides device frame rate limits for ultra smoothness",
                                checked = modFeatures.ultra120Fps,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(ultra120Fps = chk) }
                                },
                                badge = "BOOST"
                            )

                            ModSwitchRow(
                                title = "Ping & Jitter Stabilizer",
                                subtitle = "Flushes DNS cache & prioritizes game UDP packets",
                                checked = modFeatures.pingStabilizer,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(pingStabilizer = chk) }
                                }
                            )

                            ModSwitchRow(
                                title = "Speed Run 2X",
                                subtitle = "Character movement speed multiplier",
                                checked = modFeatures.speedRun2x,
                                onCheckedChange = { chk ->
                                    viewModel.updateModFeature { it.copy(speedRun2x = chk) }
                                }
                            )

                            // Quick Reset Guest Action
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        Toast.makeText(context, "Guest Account Reset Completed Successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = "Reset Guest Account",
                                            color = WarningAmber,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Instantly clears banned or flagged guest IDs",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = WarningAmber
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Announcement Notification Dialog
        if (showNoticeDialog) {
            AlertDialog(
                onDismissRequest = { showNoticeDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Server Announcement", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        text = adminConfig?.announcement ?: "No announcements right now.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showNoticeDialog = false }) {
                        Text("OK", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }

        // Force Update Modal (Blocks app until updated)
        if (adminConfig?.forceUpdateEnabled == true) {
            AlertDialog(
                onDismissRequest = { /* Non-dismissable */ },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = WarningAmber
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("MANDATORY UPDATE", color = WarningAmber, fontWeight = FontWeight.ExtraBold)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = adminConfig?.forceUpdateMessage?.ifEmpty { "A new version of MPX PANEL is required to continue." }
                                ?: "A new version of MPX PANEL is required to continue.",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please download the latest build from the official portal.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val targetUrl = adminConfig?.forceUpdateDownloadUrl?.ifEmpty { "https://mpxpanel.vercel.app" }
                                ?: "https://mpxpanel.vercel.app"
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(targetUrl)
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                    ) {
                        Text("UPDATE NOW", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }

        // Emergency Block Notice Modal
        if (adminConfig?.blockNoticeEnabled == true) {
            AlertDialog(
                onDismissRequest = { /* Non-dismissable */ },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MafiaRedPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = adminConfig?.blockNoticeTitle?.ifEmpty { "ACCESS RESTRICTED" } ?: "ACCESS RESTRICTED",
                            color = MafiaRedPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                text = {
                    Text(
                        text = adminConfig?.blockNoticeMessage?.ifEmpty { "Service is temporarily suspended. Check our official channel for updates." }
                            ?: "Service is temporarily suspended. Check our official channel for updates.",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = MafiaRedPrimary)
                    ) {
                        Text("EXIT APP", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }
    }
}
