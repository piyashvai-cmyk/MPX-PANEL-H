package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "license_keys")
data class LicenseKey(
    @PrimaryKey
    val key: String,
    val durationValue: Int = 1, // Number of units (e.g. 15, 1, 24, 7, 30, 9999)
    val durationUnit: String = "DAYS", // "MINUTES", "HOURS", "DAYS", "LIFETIME"
    val durationDays: Int = 1, // Kept for backwards compatibility
    val tier: String = "VIP", // "VIP", "VVIP", "EXCLUSIVE", "TRIAL"
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L, // 0 if unused
    val isUsed: Boolean = false,
    val maxDevices: Int = 1, // 1 device default, configurable
    val usedByDeviceId: String = "", // Comma-separated or single device ID
    val isActive: Boolean = true // Admin can ban/revoke
) {
    val isExpired: Boolean
        get() = isUsed && expiresAt > 0L && System.currentTimeMillis() > expiresAt

    val durationLabel: String
        get() = when (durationUnit.uppercase()) {
            "LIFETIME" -> "Lifetime"
            "MINUTES" -> "$durationValue Min(s)"
            "HOURS" -> "$durationValue Hour(s)"
            "DAYS" -> {
                if (durationValue >= 9999) "Lifetime"
                else if (durationValue >= 30) "${durationValue / 30} Month(s)"
                else "$durationValue Day(s)"
            }
            else -> {
                if (durationDays >= 9999) "Lifetime"
                else if (durationDays >= 30) "${durationDays / 30} Month(s)"
                else "$durationDays Day(s)"
            }
        }
}
