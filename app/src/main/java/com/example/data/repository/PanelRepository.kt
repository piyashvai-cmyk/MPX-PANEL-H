package com.example.data.repository

import com.example.data.dao.AdminConfigDao
import com.example.data.dao.LicenseKeyDao
import com.example.data.entity.AdminConfig
import com.example.data.entity.LicenseKey
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PanelRepository(
    private val licenseKeyDao: LicenseKeyDao,
    private val adminConfigDao: AdminConfigDao
) {
    val allKeys: Flow<List<LicenseKey>> = licenseKeyDao.getAllKeys()
    val adminConfig: Flow<AdminConfig?> = adminConfigDao.getConfig()

    suspend fun initializeDefaultsIfNeeded() {
        if (licenseKeyDao.getKeyCount() == 0) {
            val now = System.currentTimeMillis()
            val defaultKeys = listOf(
                LicenseKey(
                    key = "MPX-VIP-FREE-2026",
                    durationValue = 30,
                    durationUnit = "DAYS",
                    durationDays = 30,
                    tier = "VIP",
                    createdAt = now,
                    expiresAt = 0L,
                    isUsed = false,
                    maxDevices = 1
                ),
                LicenseKey(
                    key = "MPX-VIP-1HOUR-DEMO",
                    durationValue = 1,
                    durationUnit = "HOURS",
                    durationDays = 1,
                    tier = "TRIAL",
                    createdAt = now,
                    expiresAt = 0L,
                    isUsed = false,
                    maxDevices = 1
                ),
                LicenseKey(
                    key = "MPX-VIP-30MIN-TEST",
                    durationValue = 30,
                    durationUnit = "MINUTES",
                    durationDays = 1,
                    tier = "TRIAL",
                    createdAt = now,
                    expiresAt = 0L,
                    isUsed = false,
                    maxDevices = 1
                ),
                LicenseKey(
                    key = "MPX-VIP-LIFETIME-999",
                    durationValue = 9999,
                    durationUnit = "LIFETIME",
                    durationDays = 9999,
                    tier = "EXCLUSIVE",
                    createdAt = now,
                    expiresAt = 0L,
                    isUsed = false,
                    maxDevices = 3
                )
            )
            licenseKeyDao.insertKeys(defaultKeys)
        }

        if (adminConfigDao.getConfigDirect() == null) {
            adminConfigDao.saveConfig(AdminConfig())
        }
    }

    suspend fun validateAndActivateKey(rawKey: String, deviceId: String): KeyValidationResult {
        val trimmed = rawKey.trim()
        if (trimmed.isEmpty()) {
            return KeyValidationResult.Error("Please enter a valid License Key")
        }

        val config = adminConfigDao.getConfigDirect() ?: AdminConfig()
        if (!config.serverStatus) {
            return KeyValidationResult.Error("Server is currently in MAINTENANCE mode. Please check back soon.")
        }

        val keyEntity = licenseKeyDao.getKey(trimmed)
            ?: return KeyValidationResult.Error("Invalid License Key. Contact Admin on Telegram to get a valid key.")

        if (!keyEntity.isActive) {
            return KeyValidationResult.Error("This License Key has been BANNED or REVOKED by Admin.")
        }

        val now = System.currentTimeMillis()

        // If key is already used
        if (keyEntity.isUsed) {
            if (keyEntity.expiresAt > 0 && now > keyEntity.expiresAt) {
                return KeyValidationResult.Error("This License Key has EXPIRED. Please renew your VIP key.")
            }
            // Device limit checking (comma separated device IDs)
            val registeredDevices = keyEntity.usedByDeviceId.split(",").filter { it.isNotBlank() }.toMutableList()
            if (!registeredDevices.contains(deviceId)) {
                if (registeredDevices.size >= keyEntity.maxDevices) {
                    return KeyValidationResult.Error("Device limit reached (${keyEntity.maxDevices} max allowed). Bound to: ${registeredDevices.joinToString(", ") { it.take(6) }}")
                }
                // Allow additional device within limit
                registeredDevices.add(deviceId)
                val updatedKey = keyEntity.copy(usedByDeviceId = registeredDevices.joinToString(","))
                licenseKeyDao.updateKey(updatedKey)
                return KeyValidationResult.Success(updatedKey)
            }
            return KeyValidationResult.Success(keyEntity)
        }

        // First-time activation duration calculation
        val expiry = when (keyEntity.durationUnit.uppercase()) {
            "MINUTES" -> now + (keyEntity.durationValue.toLong() * 60 * 1000)
            "HOURS" -> now + (keyEntity.durationValue.toLong() * 60 * 60 * 1000)
            "LIFETIME" -> now + (3650L * 24 * 60 * 60 * 1000) // 10 years
            else -> { // DAYS
                if (keyEntity.durationValue >= 9999 || keyEntity.durationDays >= 9999) {
                    now + (3650L * 24 * 60 * 60 * 1000)
                } else {
                    val days = if (keyEntity.durationValue > 0) keyEntity.durationValue else keyEntity.durationDays
                    now + (days.toLong() * 24 * 60 * 60 * 1000)
                }
            }
        }

        val activatedKey = keyEntity.copy(
            isUsed = true,
            usedByDeviceId = deviceId,
            expiresAt = expiry
        )
        licenseKeyDao.updateKey(activatedKey)
        return KeyValidationResult.Success(activatedKey)
    }

    suspend fun generateKeys(
        count: Int,
        durationValue: Int,
        durationUnit: String,
        tier: String,
        customPrefix: String = "",
        maxDevices: Int = 1
    ): List<LicenseKey> {
        val generatedList = mutableListOf<LicenseKey>()
        val now = System.currentTimeMillis()
        for (i in 1..count) {
            val randomSegment = UUID.randomUUID().toString().take(4).uppercase()
            val randomSegment2 = UUID.randomUUID().toString().take(4).uppercase()
            val prefix = customPrefix.trim().ifEmpty { "MPX" }
            
            val unitStr = when (durationUnit.uppercase()) {
                "MINUTES" -> "${durationValue}M"
                "HOURS" -> "${durationValue}H"
                "LIFETIME" -> "LIFE"
                else -> "${durationValue}D"
            }

            val keyString = "$prefix-$unitStr-$randomSegment-$randomSegment2"

            val key = LicenseKey(
                key = keyString,
                durationValue = durationValue,
                durationUnit = durationUnit.uppercase(),
                durationDays = if (durationUnit.uppercase() == "DAYS") durationValue else 1,
                tier = tier,
                createdAt = now + i,
                expiresAt = 0L,
                isUsed = false,
                maxDevices = maxDevices
            )
            generatedList.add(key)
        }
        licenseKeyDao.insertKeys(generatedList)
        return generatedList
    }

    suspend fun createCustomKey(
        customKeyName: String,
        durationValue: Int,
        durationUnit: String,
        tier: String,
        maxDevices: Int = 1
    ): LicenseKey {
        val key = LicenseKey(
            key = customKeyName.trim().uppercase(),
            durationValue = durationValue,
            durationUnit = durationUnit.uppercase(),
            durationDays = if (durationUnit.uppercase() == "DAYS") durationValue else 1,
            tier = tier,
            createdAt = System.currentTimeMillis(),
            expiresAt = 0L,
            isUsed = false,
            maxDevices = maxDevices
        )
        licenseKeyDao.insertKey(key)
        return key
    }

    suspend fun toggleKeyActive(key: LicenseKey) {
        licenseKeyDao.updateKey(key.copy(isActive = !key.isActive))
    }

    suspend fun deleteKey(keyString: String) {
        licenseKeyDao.deleteKey(keyString)
    }

    suspend fun updateAdminConfig(config: AdminConfig) {
        adminConfigDao.saveConfig(config)
    }
}

sealed class KeyValidationResult {
    data class Success(val key: LicenseKey) : KeyValidationResult()
    data class Error(val message: String) : KeyValidationResult()
}
