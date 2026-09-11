package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.ui.draw.drawBehind
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.AdminConfig
import com.example.data.entity.LicenseKey
import com.example.ui.components.GlowButton
import com.example.ui.components.ModSwitchRow
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: PanelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val allKeys by viewModel.allKeys.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()

    var adminSectionTab by remember { mutableIntStateOf(0) } // 0: Keys, 1: Generator, 2: Server Settings

    // Key Generation Form State
    var durationUnit by remember { mutableStateOf("DAYS") } // "MINUTES", "HOURS", "DAYS", "LIFETIME"
    var durationValue by remember { mutableIntStateOf(7) }
    var selectedTier by remember { mutableStateOf("VIP") }
    var generateCount by remember { mutableIntStateOf(1) }
    var customPrefix by remember { mutableStateOf("MPX") }
    var customKeyName by remember { mutableStateOf("") }
    var maxDeviceLimit by remember { mutableIntStateOf(1) }
    var recentlyGeneratedKeys by remember { mutableStateOf<List<LicenseKey>>(emptyList()) }

    // Server Config Form State
    var announcementText by remember(adminConfig) { mutableStateOf(adminConfig?.announcement ?: "") }
    var telegramUrl by remember(adminConfig) { mutableStateOf(adminConfig?.telegramUrl ?: "") }
    var whatsappUrl by remember(adminConfig) { mutableStateOf(adminConfig?.whatsappUrl ?: "") }
    var discordUrl by remember(adminConfig) { mutableStateOf(adminConfig?.discordUrl ?: "") }
    var adminPassword by remember(adminConfig) { mutableStateOf(adminConfig?.adminPassword ?: "") }
    var serverStatus by remember(adminConfig) { mutableStateOf(adminConfig?.serverStatus ?: true) }
    var forceUpdateEnabled by remember(adminConfig) { mutableStateOf(adminConfig?.forceUpdateEnabled ?: false) }
    var forceUpdateMessage by remember(adminConfig) { mutableStateOf(adminConfig?.forceUpdateMessage ?: "A mandatory security update is available. Please update to continue using MPX PANEL.") }
    var blockNoticeEnabled by remember(adminConfig) { mutableStateOf(adminConfig?.blockNoticeEnabled ?: false) }
    var blockNoticeMessage by remember(adminConfig) { mutableStateOf(adminConfig?.blockNoticeMessage ?: "Server under emergency maintenance. Please check Telegram.") }

    // Key list filter
    var keyFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "USED", "REVOKED"
    var searchQuery by remember { mutableStateOf("") }

    val filteredKeys = remember(allKeys, keyFilter, searchQuery) {
        allKeys.filter { key ->
            val matchesSearch = searchQuery.isEmpty() || key.key.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (keyFilter) {
                "ACTIVE" -> !key.isUsed && key.isActive
                "USED" -> key.isUsed && key.isActive
                "REVOKED" -> !key.isActive
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    val totalKeysCount = allKeys.size
    val activeKeysCount = allKeys.count { it.isActive && (!it.isUsed || !it.isExpired) }
    val usedKeysCount = allKeys.count { it.isUsed }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 30.dp)
        ) {
            // ADMIN HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated)
                    .drawBehind {
                        drawLine(
                            color = VipGold.copy(alpha = 0.3f),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                            strokeWidth = 2f
                        )
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (viewModel.currentSessionKey.value != null) {
                                viewModel.navigateTo(AppScreen.Dashboard)
                            } else {
                                viewModel.navigateTo(AppScreen.Login)
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = VipGold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "ADMIN CONTROL PANEL",
                            color = VipGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "MPX PANEL VIP SYSTEM MANAGEMENT",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                StatusPill(
                    text = if (adminConfig?.serverStatus != false) "SERVER ONLINE" else "MAINTENANCE",
                    isOnline = adminConfig?.serverStatus != false
                )
            }

            // SUMMARY CARDS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "TOTAL KEYS",
                    count = totalKeysCount.toString(),
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "ACTIVE / VALID",
                    count = activeKeysCount.toString(),
                    color = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "USED KEYS",
                    count = usedKeysCount.toString(),
                    color = MafiaRedPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            // ADMIN SECTION TABS
            TabRow(
                selectedTabIndex = adminSectionTab,
                containerColor = DarkSurface,
                contentColor = VipGold,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Tab(
                    selected = adminSectionTab == 0,
                    onClick = { adminSectionTab = 0 },
                    text = { Text("MANAGE KEYS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = adminSectionTab == 1,
                    onClick = { adminSectionTab = 1 },
                    text = { Text("KEY GENERATOR", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = adminSectionTab == 2,
                    onClick = { adminSectionTab = 2 },
                    text = { Text("SERVER CONFIG", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (adminSectionTab) {
                0 -> {
                    // MANAGE KEYS SECTION
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        // Search box
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by key name...", color = TextMuted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("key_search_field"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = VipGold,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ALL", "ACTIVE", "USED", "REVOKED").forEach { filterTag ->
                                val isSelected = keyFilter == filterTag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VipGold.copy(alpha = 0.2f) else DarkSurface)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) VipGold else DarkSurfaceBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { keyFilter = filterTag }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = filterTag,
                                        color = if (isSelected) VipGold else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Key Items List
                        if (filteredKeys.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No keys matching filter.",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            filteredKeys.forEach { keyItem ->
                                KeyItemCard(
                                    keyItem = keyItem,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(keyItem.key))
                                        Toast.makeText(context, "Key copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    onToggleActive = {
                                        viewModel.toggleKeyStatus(keyItem)
                                    },
                                    onDelete = {
                                        viewModel.deleteKey(keyItem.key)
                                        Toast.makeText(context, "Key deleted!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                1 -> {
                    // KEY GENERATOR STUDIO
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        NeonCard(
                            borderColor = VipGold.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "GENERATE NEW VIP LICENSE KEYS",
                                    color = VipGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Create unique license keys with custom validity and VIP tiers.",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Duration Unit Selector (Minutes, Hours, Days, Lifetime)
                                Text(
                                    text = "SELECT DURATION UNIT:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "MINUTES" to "Minutes (M)",
                                        "HOURS" to "Hours (H)",
                                        "DAYS" to "Days (D)",
                                        "LIFETIME" to "Lifetime"
                                    ).forEach { (unit, label) ->
                                        val isSelected = durationUnit == unit
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else DarkSurface)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) CyberCyan else DarkSurfaceBorder,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    durationUnit = unit
                                                    durationValue = when (unit) {
                                                        "MINUTES" -> 30
                                                        "HOURS" -> 2
                                                        "LIFETIME" -> 9999
                                                        else -> 7
                                                    }
                                                }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (isSelected) CyberCyan else TextSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Duration Value Presets
                                if (durationUnit != "LIFETIME") {
                                    Text(
                                        text = "DURATION VALUE ($durationUnit):",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val options = when (durationUnit) {
                                            "MINUTES" -> listOf(15 to "15m", 30 to "30m", 60 to "60m", 120 to "120m")
                                            "HOURS" -> listOf(1 to "1 hr", 3 to "3 hrs", 12 to "12 hrs", 24 to "24 hrs")
                                            else -> listOf(1 to "1 Day", 3 to "3 Days", 7 to "7 Days", 30 to "30 Days")
                                        }
                                        options.forEach { (value, label) ->
                                            val isSelected = durationValue == value
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) VipGold.copy(alpha = 0.2f) else DarkSurface)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSelected) VipGold else DarkSurfaceBorder,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { durationValue = value }
                                                    .padding(vertical = 7.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = if (isSelected) VipGold else TextSecondary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Device Limit Selector
                                Text(
                                    text = "DEVICE LIMIT (Max Allowed):",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        1 to "1 Device",
                                        2 to "2 Devices",
                                        3 to "3 Devices",
                                        5 to "5 Devices"
                                    ).forEach { (devCount, label) ->
                                        val isSelected = maxDeviceLimit == devCount
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else DarkSurface)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) NeonGreen else DarkSurfaceBorder,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { maxDeviceLimit = devCount }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (isSelected) NeonGreen else TextSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Tier Selector
                                Text(
                                    text = "VIP TIER:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("TRIAL", "VIP", "VVIP", "EXCLUSIVE").forEach { tier ->
                                        val isSelected = selectedTier == tier
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) MafiaRedPrimary.copy(alpha = 0.2f) else DarkSurface)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) MafiaRedPrimary else DarkSurfaceBorder,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedTier = tier }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = tier,
                                                color = if (isSelected) MafiaRedPrimary else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Custom Key Name or Batch Generator
                                Text(
                                    text = "CUSTOM KEY NAME (Optional):",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = customKeyName,
                                    onValueChange = { customKeyName = it },
                                    placeholder = { Text("e.g. MPX-SPECIAL-VIP-KEY", color = TextMuted, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedContainerColor = DarkSurface,
                                        unfocusedContainerColor = DarkSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quantity Selector (if not custom single key)
                                if (customKeyName.isBlank()) {
                                    Text(
                                        text = "BATCH QUANTITY:",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(1, 3, 5, 10).forEach { count ->
                                            val isSelected = generateCount == count
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) VipGold.copy(alpha = 0.2f) else DarkSurface)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSelected) VipGold else DarkSurfaceBorder,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { generateCount = count }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$count Key(s)",
                                                    color = if (isSelected) VipGold else TextSecondary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Generate Button
                                GlowButton(
                                    text = if (customKeyName.isNotBlank()) "CREATE CUSTOM KEY" else "GENERATE $generateCount $selectedTier KEY(S)",
                                    onClick = {
                                        if (customKeyName.isNotBlank()) {
                                            viewModel.createCustomNamedKey(
                                                keyName = customKeyName.trim(),
                                                durationValue = durationValue,
                                                durationUnit = durationUnit,
                                                tier = selectedTier,
                                                maxDevices = maxDeviceLimit
                                            ) { newKey ->
                                                recentlyGeneratedKeys = listOf(newKey)
                                                customKeyName = ""
                                                Toast.makeText(context, "Custom key created!", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            viewModel.generateNewKeys(
                                                count = generateCount,
                                                durationValue = durationValue,
                                                durationUnit = durationUnit,
                                                tier = selectedTier,
                                                customPrefix = customPrefix,
                                                maxDevices = maxDeviceLimit
                                            ) { newKeys ->
                                                recentlyGeneratedKeys = newKeys
                                                Toast.makeText(context, "Successfully generated ${newKeys.size} key(s)!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    isPrimaryRed = false,
                                    iconVector = Icons.Default.Key,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "generate_keys_submit_btn"
                                )
                            }
                        }

                        // Recently Generated Banner & Quick Copy
                        if (recentlyGeneratedKeys.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            NeonCard(
                                borderColor = NeonGreen.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "RECENTLY GENERATED (${recentlyGeneratedKeys.size})",
                                            color = NeonGreen,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Button(
                                            onClick = {
                                                val allText = recentlyGeneratedKeys.joinToString("\n") { it.key }
                                                clipboardManager.setText(AnnotatedString(allText))
                                                Toast.makeText(context, "Copied all keys!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = DarkBackground
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("COPY ALL", color = DarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    recentlyGeneratedKeys.forEach { k ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DarkSurfaceElevated)
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = k.key,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = CyberCyan,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clickable {
                                                        clipboardManager.setText(AnnotatedString(k.key))
                                                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // SERVER CONFIGURATION SECTION
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NeonCard(
                            borderColor = MafiaRedPrimary.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "SERVER STATUS & BROADCASTING",
                                    color = MafiaRedLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Server Online Switch
                                ModSwitchRow(
                                    title = if (serverStatus) "Server Status: ONLINE" else "Server Status: MAINTENANCE",
                                    subtitle = "Toggling OFF will block users from logging in with keys",
                                    checked = serverStatus,
                                    onCheckedChange = { serverStatus = it },
                                    badge = if (serverStatus) "ONLINE" else "OFFLINE",
                                    badgeColor = if (serverStatus) NeonGreen else MafiaRedPrimary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Announcement Message
                                Text(
                                    text = "SERVER ANNOUNCEMENT / NOTIFICATION:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = announcementText,
                                    onValueChange = { announcementText = it },
                                    modifier = Modifier.fillMaxWidth().testTag("announcement_field"),
                                    maxLines = 3,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedContainerColor = DarkSurface,
                                        unfocusedContainerColor = DarkSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Emergency Notice & Force Update Controls
                                Text(
                                    text = "MAINTENANCE & FORCE CONTROLS:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Force Update Switch & Message
                                ModSwitchRow(
                                    title = "Force App Update",
                                    subtitle = "Blocks users from dashboard until they update app",
                                    checked = forceUpdateEnabled,
                                    onCheckedChange = { forceUpdateEnabled = it },
                                    badge = if (forceUpdateEnabled) "ACTIVE" else "OFF",
                                    badgeColor = if (forceUpdateEnabled) WarningAmber else TextMuted
                                )
                                if (forceUpdateEnabled) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = forceUpdateMessage,
                                        onValueChange = { forceUpdateMessage = it },
                                        label = { Text("Update Prompt Message", fontSize = 11.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = WarningAmber,
                                            unfocusedBorderColor = DarkSurfaceBorder
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Block Notice Switch & Message
                                ModSwitchRow(
                                    title = "Emergency Block Notice",
                                    subtitle = "Displays full-screen blocker message to all users",
                                    checked = blockNoticeEnabled,
                                    onCheckedChange = { blockNoticeEnabled = it },
                                    badge = if (blockNoticeEnabled) "BLOCKING" else "OFF",
                                    badgeColor = if (blockNoticeEnabled) MafiaRedPrimary else TextMuted
                                )
                                if (blockNoticeEnabled) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = blockNoticeMessage,
                                        onValueChange = { blockNoticeMessage = it },
                                        label = { Text("Block Reason / Notice Message", fontSize = 11.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = MafiaRedPrimary,
                                            unfocusedBorderColor = DarkSurfaceBorder
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Social Links
                                Text(
                                    text = "SOCIAL MEDIA & COMMUNITY CHANNELS:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = telegramUrl,
                                    onValueChange = { telegramUrl = it },
                                    label = { Text("Telegram Channel Link", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = DarkSurfaceBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = whatsappUrl,
                                    onValueChange = { whatsappUrl = it },
                                    label = { Text("WhatsApp Group Link", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = DarkSurfaceBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = discordUrl,
                                    onValueChange = { discordUrl = it },
                                    label = { Text("Discord Community Link", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = DarkSurfaceBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = adminPassword,
                                    onValueChange = { adminPassword = it },
                                    label = { Text("Admin Panel Password", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = VipGold,
                                        unfocusedBorderColor = DarkSurfaceBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                GlowButton(
                                    text = "SAVE SERVER CONFIGURATION",
                                    onClick = {
                                        val currentConfig = adminConfig ?: AdminConfig()
                                        val updated = currentConfig.copy(
                                            serverStatus = serverStatus,
                                            announcement = announcementText.trim(),
                                            telegramUrl = telegramUrl.trim(),
                                            whatsappUrl = whatsappUrl.trim(),
                                            discordUrl = discordUrl.trim(),
                                            adminPassword = adminPassword.trim().ifEmpty { "admin123" },
                                            forceUpdateEnabled = forceUpdateEnabled,
                                            forceUpdateMessage = forceUpdateMessage.trim(),
                                            blockNoticeEnabled = blockNoticeEnabled,
                                            blockNoticeMessage = blockNoticeMessage.trim()
                                        )
                                        viewModel.updateAdminConfig(updated)
                                        Toast.makeText(context, "Server configuration updated successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    isPrimaryRed = true,
                                    iconVector = Icons.Default.Save,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "save_config_btn"
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
fun AdminStatCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun KeyItemCard(
    keyItem: LicenseKey,
    onCopy: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceElevated)
            .border(
                width = 1.dp,
                color = if (keyItem.isActive) DarkSurfaceBorder else MafiaRedPrimary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = keyItem.key,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            when (keyItem.tier) {
                                "EXCLUSIVE" -> VipGold.copy(alpha = 0.2f)
                                "VVIP" -> MafiaRedPrimary.copy(alpha = 0.2f)
                                else -> CyberCyan.copy(alpha = 0.2f)
                            },
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${keyItem.tier} • ${keyItem.durationLabel}",
                        color = when (keyItem.tier) {
                            "EXCLUSIVE" -> VipGold
                            "VVIP" -> MafiaRedPrimary
                            else -> CyberCyan
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val usedDeviceCount = if (keyItem.usedByDeviceId.isBlank()) 0 else keyItem.usedByDeviceId.split(",").filter { it.isNotBlank() }.size
                    Text(
                        text = if (keyItem.isUsed) "Status: USED ($usedDeviceCount/${keyItem.maxDevices} Devs)" else "Status: UNUSED (Max ${keyItem.maxDevices} Dev)",
                        color = if (keyItem.isUsed) WarningAmber else NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (keyItem.expiresAt > 0L) {
                        Text(
                            text = "Expires: ${dateFormat.format(Date(keyItem.expiresAt))}",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    } else {
                        Text(
                            text = "Expires: Never (Lifetime)",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Ban / Unban Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (keyItem.isActive) MafiaRedPrimary.copy(alpha = 0.15f) else NeonGreen.copy(alpha = 0.15f))
                            .border(0.5.dp, if (keyItem.isActive) MafiaRedPrimary else NeonGreen, RoundedCornerShape(6.dp))
                            .clickable(onClick = onToggleActive)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (keyItem.isActive) "BAN/REVOKE" else "UNBAN/ACTIVATE",
                            color = if (keyItem.isActive) MafiaRedLight else NeonGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Delete button
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
