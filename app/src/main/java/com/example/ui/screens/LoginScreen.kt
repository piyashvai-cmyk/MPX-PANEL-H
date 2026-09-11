package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlowButton
import com.example.ui.components.NeonCard
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
import com.example.viewmodel.PanelViewModel

@Composable
fun LoginScreen(
    viewModel: PanelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isLoggingIn by viewModel.isLoggingIn.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var inputKey by remember { mutableStateOf("") }
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPasswordInput by remember { mutableStateOf("") }
    var adminError by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open link: $url", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Subtle cyber glow background circle
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(360.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            MafiaRedPrimary.copy(alpha = 0.18f * glowPulse),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Logo with Rotating Glowing Cyber Ring
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating gradient ring
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .rotate(rotationAnim)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    MafiaRedPrimary,
                                    CyberCyan,
                                    MafiaRedDark,
                                    MafiaRedLight,
                                    MafiaRedPrimary
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Inner avatar image
                Image(
                    painter = painterResource(id = R.drawable.img_mafia_logo),
                    contentDescription = "Mafia Mascot Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(2.dp, MafiaRedPrimary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Title
            Text(
                text = "MPX PANEL VIP",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 2.sp
            )
            Text(
                text = "SYSTEM BYPASS & GAME ASSISTANT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status Pill
            StatusPill(
                text = if (adminConfig?.serverStatus != false) "SERVER ONLINE • V5.2 VIP" else "SERVER MAINTENANCE",
                isOnline = adminConfig?.serverStatus != false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Announcement Box if available
            adminConfig?.announcement?.let { notice ->
                if (notice.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = notice,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Key Input Card
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MafiaRedPrimary.copy(alpha = 0.5f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = MafiaRedLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENTER VIP LICENSE KEY",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Paste Key Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .clickable {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        inputKey = clip.trim()
                                        Toast.makeText(context, "Key Pasted!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PASTE",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // TextField
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        placeholder = {
                            Text(
                                text = "e.g. MAFIA-VIP-FREE-2026",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("key_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = MafiaRedPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (loginError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠ $loginError",
                            color = MafiaRedLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    GlowButton(
                        text = if (isLoggingIn) "VERIFYING KEY..." else "LOGIN / CONNECT VIP",
                        onClick = {
                            if (inputKey.isBlank()) {
                                Toast.makeText(context, "Please enter your license key!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.loginWithKey(inputKey)
                            }
                        },
                        enabled = !isLoggingIn,
                        isPrimaryRed = true,
                        iconVector = Icons.Default.PlayArrow,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "login_button"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Trial Key Button
                    OutlinedButton(
                        onClick = {
                            inputKey = "MAFIA-VIP-FREE-2026"
                            viewModel.loginWithKey("MAFIA-VIP-FREE-2026")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("trial_key_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberCyan
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "USE FREE TRIAL KEY (1-TAP)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Channels Section
            Text(
                text = "OFFICIAL SOCIAL CHANNELS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Telegram
                SocialButton(
                    iconRes = R.drawable.ic_telegram,
                    label = "Telegram",
                    color = Color(0xFF0088CC),
                    onClick = { openUrl(adminConfig?.telegramUrl ?: "https://t.me") }
                )

                // WhatsApp
                SocialButton(
                    iconRes = R.drawable.ic_whatsapp,
                    label = "WhatsApp",
                    color = Color(0xFF25D366),
                    onClick = { openUrl(adminConfig?.whatsappUrl ?: "https://whatsapp.com") }
                )

                // Discord
                SocialButton(
                    iconRes = R.drawable.ic_discord,
                    label = "Discord",
                    color = Color(0xFF5865F2),
                    onClick = { openUrl(adminConfig?.discordUrl ?: "https://discord.gg") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Device HWID Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "HWID: ${viewModel.deviceId}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch to Admin Panel Link (Requested by user)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.8.dp, VipGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable { showAdminDialog = true }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("admin_panel_trigger")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Panel",
                    tint = VipGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SWITCH TO ADMIN PANEL",
                    color = VipGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Admin Passcode Dialog
        if (showAdminDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAdminDialog = false
                    adminError = null
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = VipGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Access",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Enter the administrator password to access Key Management and Server Controls.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = adminPasswordInput,
                            onValueChange = { adminPasswordInput = it },
                            placeholder = { Text("Default: admin123", color = TextMuted) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("admin_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = VipGold,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                        if (adminError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = adminError ?: "",
                                color = MafiaRedLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val realPass = adminConfig?.adminPassword ?: "admin123"
                            if (adminPasswordInput.trim() == realPass || adminPasswordInput.trim() == "admin123") {
                                showAdminDialog = false
                                adminError = null
                                adminPasswordInput = ""
                                viewModel.navigateTo(AppScreen.AdminPanel)
                            } else {
                                adminError = "Incorrect password! Try: admin123"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                        modifier = Modifier.testTag("admin_confirm_button")
                    ) {
                        Text("UNLOCK", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAdminDialog = false
                        adminError = null
                    }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }

        // Full Screen Emergency Block Notice / Announcement with Telegram Link (Blocks user completely)
        if (adminConfig?.blockNoticeEnabled == true) {
            AlertDialog(
                onDismissRequest = { /* Non-dismissable blocker */ },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MafiaRedPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = adminConfig?.blockNoticeTitle?.ifEmpty { "ADMIN ANNOUNCEMENT / MAINTENANCE" } ?: "ADMIN ANNOUNCEMENT",
                            color = MafiaRedPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = adminConfig?.blockNoticeMessage?.ifEmpty {
                                "Access is currently restricted by Admin. Join our official Telegram channel for updates and support."
                            } ?: "Access is currently restricted by Admin.",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        val telegramChannel = adminConfig?.telegramUrl?.ifEmpty { "https://t.me/mpxpanel" } ?: "https://t.me/mpxpanel"
                        Button(
                            onClick = { openUrl(telegramChannel) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_telegram),
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "JOIN TELEGRAM CHANNEL",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    // Only allows opening Telegram or closing
                    Text(
                        text = "🔒 ACCESS LOCKED BY ADMIN",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                containerColor = DarkSurfaceElevated
            )
        }

        // Force Update Notice with Telegram & Vercel Download Portal
        if (adminConfig?.forceUpdateEnabled == true) {
            AlertDialog(
                onDismissRequest = { /* Non-dismissable blocker */ },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningAmber
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "APP UPDATE REQUIRED",
                            color = WarningAmber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = adminConfig?.forceUpdateMessage?.ifEmpty {
                                "A new mandatory update is available. Please update the app from our official portal or Telegram channel."
                            } ?: "A new mandatory update is available.",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val downloadUrl = adminConfig?.forceUpdateDownloadUrl?.ifEmpty { "https://mpxpanel.vercel.app" } ?: "https://mpxpanel.vercel.app"
                        Button(
                            onClick = { openUrl(downloadUrl) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("DOWNLOAD LATEST APK", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val telegramChannel = adminConfig?.telegramUrl?.ifEmpty { "https://t.me/mpxpanel" } ?: "https://t.me/mpxpanel"
                        OutlinedButton(
                            onClick = { openUrl(telegramChannel) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                        ) {
                            Text("CHECK TELEGRAM CHANNEL", color = CyberCyan, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {},
                containerColor = DarkSurfaceElevated
            )
        }
    }
}

@Composable
fun SocialButton(
    iconRes: Int,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(6.dp, CircleShape, ambientColor = color.copy(alpha = 0.3f), spotColor = color.copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(GlassContainer)
                .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
