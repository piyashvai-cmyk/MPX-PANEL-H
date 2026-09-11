package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_config")
data class AdminConfig(
    @PrimaryKey
    val id: Int = 1,
    val serverStatus: Boolean = true, // Online / Offline
    val announcement: String = "🔥 MPX PANEL VIP ACTIVE! Bypass Anti-Ban 100% Online. Join Telegram for new updates and VIP keys!",
    val telegramUrl: String = "https://t.me/mpx_panel_official",
    val whatsappUrl: String = "https://whatsapp.com/channel/mpxpanel",
    val discordUrl: String = "https://discord.gg/mpxpanel",
    val youtubeUrl: String = "https://youtube.com/@mpxpanel",
    val adminPassword: String = "admin123",
    val appVersion: String = "v5.2 VIP (Stable)",
    // Force Update & Notice Blocking Modal
    val forceUpdateEnabled: Boolean = false,
    val forceUpdateMessage: String = "A mandatory security update is available. You must update to continue using MPX PANEL.",
    val forceUpdateDownloadUrl: String = "https://t.me/mpx_panel_official",
    val blockNoticeEnabled: Boolean = false,
    val blockNoticeTitle: String = "IMPORTANT NOTICE",
    val blockNoticeMessage: String = "Server undergoing security synchronization. Please stay tuned on Telegram.",
    // Feature master switches controlled by Admin:
    val aimbotAllowed: Boolean = true,
    val espAllowed: Boolean = true,
    val antiBanAllowed: Boolean = true,
    val speedAllowed: Boolean = true,
    val guestResetAllowed: Boolean = true
)
