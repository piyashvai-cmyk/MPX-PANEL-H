package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.AdminConfig
import com.example.data.entity.LicenseKey
import com.example.data.repository.KeyValidationResult
import com.example.data.repository.PanelRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameProfile(
    val id: String,
    val name: String,
    val packageName: String,
    val arch: String,
    val isInstalled: Boolean = true,
    val antiBanStatus: String = "100% SAFE"
)

data class ModFeaturesState(
    // Aimbot
    val aimbotEnabled: Boolean = true,
    val autoHeadshot: Boolean = true,
    val aimLock: Boolean = true,
    val aimScope: Boolean = false,
    val aimFov: Float = 120f,
    val aimSmooth: Float = 5f,

    // Visuals / ESP
    val espLine: Boolean = true,
    val espBox: Boolean = true,
    val espSkeleton: Boolean = false,
    val espDistance: Boolean = true,
    val espHealth: Boolean = true,
    val espName: Boolean = true,
    val espGrenadeAlert: Boolean = true,
    val espColor: String = "CYAN", // "CYAN", "RED", "GREEN", "YELLOW"

    // Game Boost & Security
    val antiBanProtection: Boolean = true,
    val antiBlacklist: Boolean = true,
    val ultra120Fps: Boolean = true,
    val pingStabilizer: Boolean = true,
    val noRecoil: Boolean = false,
    val speedRun2x: Boolean = false,
    val magicBullet: Boolean = false,

    // Overlay View State
    val isOverlayActive: Boolean = false,
    val isMenuExpanded: Boolean = false,
    val overlayOpacity: Float = 0.95f,
    val currentTab: Int = 0 // 0: Aim, 1: ESP, 2: Boost, 3: Settings
)

data class SystemStats(
    val ramUsedPercent: Int = 38,
    val ramUsedGb: Float = 3.1f,
    val ramTotalGb: Float = 8.0f,
    val pingMs: Int = 24,
    val fps: Int = 120,
    val storageFreeGb: Float = 48.5f,
    val batteryPercent: Int = 82
)

sealed class AppScreen {
    object Login : AppScreen()
    object Dashboard : AppScreen()
    object AdminPanel : AppScreen()
}

class PanelViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PanelRepository

    val allKeys: StateFlow<List<LicenseKey>>
    val adminConfig: StateFlow<AdminConfig?>

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Login)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentSessionKey = MutableStateFlow<LicenseKey?>(null)
    val currentSessionKey: StateFlow<LicenseKey?> = _currentSessionKey.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

    private val _modFeatures = MutableStateFlow(ModFeaturesState())
    val modFeatures: StateFlow<ModFeaturesState> = _modFeatures.asStateFlow()

    private val _systemStats = MutableStateFlow(SystemStats())
    val systemStats: StateFlow<SystemStats> = _systemStats.asStateFlow()

    val gamesList = listOf(
        GameProfile("ff_max", "Free Fire MAX", "com.dts.freefiremax", "64-bit"),
        GameProfile("ff_normal", "Free Fire", "com.dts.freefireth", "32-bit"),
        GameProfile("pubg_mobile", "PUBG Mobile / BGMI", "com.tencent.ig", "64-bit"),
        GameProfile("8ball_pool", "8 Ball Pool VIP", "com.miniclip.eightballpool", "32-bit")
    )

    private val _selectedGame = MutableStateFlow(gamesList[0])
    val selectedGame: StateFlow<GameProfile> = _selectedGame.asStateFlow()

    val deviceId: String

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PanelRepository(db.licenseKeyDao(), db.adminConfigDao())

        allKeys = repository.allKeys.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        adminConfig = repository.adminConfig.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        val androidId = Settings.Secure.getString(
            application.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "UNKNOWN_DEV_${Build.MODEL.take(6)}"
        deviceId = androidId.uppercase()

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            startSystemStatsMonitoring()
        }
    }

    private fun startSystemStatsMonitoring() {
        viewModelScope.launch {
            while (isActive) {
                delay(3000)
                val jitterPing = Random.nextInt(18, 38)
                val jitterRam = Random.nextInt(32, 42)
                _systemStats.update {
                    it.copy(
                        pingMs = jitterPing,
                        ramUsedPercent = jitterRam,
                        ramUsedGb = (jitterRam * 8.0f / 100f)
                    )
                }
            }
        }
    }

    fun loginWithKey(key: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            _loginError.value = null
            delay(600) // Brief futuristic validation pulse
            when (val result = repository.validateAndActivateKey(key, deviceId)) {
                is KeyValidationResult.Success -> {
                    _currentSessionKey.value = result.key
                    _currentScreen.value = AppScreen.Dashboard
                    _isLoggingIn.value = false
                    onComplete(true)
                }
                is KeyValidationResult.Error -> {
                    _loginError.value = result.message
                    _isLoggingIn.value = false
                    onComplete(false)
                }
            }
        }
    }

    fun quickFillTrialKey() {
        val firstActiveKey = allKeys.value.firstOrNull { !it.isUsed && it.isActive }
        val trialKey = firstActiveKey?.key ?: "MAFIA-VIP-FREE-2026"
        loginWithKey(trialKey)
    }

    fun logout() {
        _currentSessionKey.value = null
        _modFeatures.update { it.copy(isOverlayActive = false, isMenuExpanded = false) }
        _currentScreen.value = AppScreen.Login
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectGame(game: GameProfile) {
        _selectedGame.value = game
    }

    fun toggleInjector() {
        _modFeatures.update {
            val nextState = !it.isOverlayActive
            it.copy(
                isOverlayActive = nextState,
                isMenuExpanded = if (nextState) true else it.isMenuExpanded
            )
        }
    }

    fun setOverlayActive(active: Boolean) {
        _modFeatures.update { it.copy(isOverlayActive = active) }
    }

    fun toggleFloatingMenuExpanded() {
        _modFeatures.update { it.copy(isMenuExpanded = !it.isMenuExpanded) }
    }

    fun setFloatingMenuExpanded(expanded: Boolean) {
        _modFeatures.update { it.copy(isMenuExpanded = expanded) }
    }

    fun setFloatingTab(tabIndex: Int) {
        _modFeatures.update { it.copy(currentTab = tabIndex) }
    }

    fun updateModFeature(transform: (ModFeaturesState) -> ModFeaturesState) {
        _modFeatures.update(transform)
    }

    // Admin Operations
    fun generateNewKeys(
        count: Int,
        durationValue: Int,
        durationUnit: String,
        tier: String,
        customPrefix: String = "MPX",
        maxDevices: Int = 1,
        onGenerated: (List<LicenseKey>) -> Unit
    ) {
        viewModelScope.launch {
            val newKeys = repository.generateKeys(
                count = count,
                durationValue = durationValue,
                durationUnit = durationUnit,
                tier = tier,
                customPrefix = customPrefix,
                maxDevices = maxDevices
            )
            onGenerated(newKeys)
        }
    }

    fun createCustomNamedKey(
        keyName: String,
        durationValue: Int,
        durationUnit: String,
        tier: String,
        maxDevices: Int = 1,
        onCreated: (LicenseKey) -> Unit
    ) {
        viewModelScope.launch {
            val newKey = repository.createCustomKey(
                customKeyName = keyName,
                durationValue = durationValue,
                durationUnit = durationUnit,
                tier = tier,
                maxDevices = maxDevices
            )
            onCreated(newKey)
        }
    }

    fun toggleKeyStatus(key: LicenseKey) {
        viewModelScope.launch {
            repository.toggleKeyActive(key)
        }
    }

    fun deleteKey(key: String) {
        viewModelScope.launch {
            repository.deleteKey(key)
        }
    }

    fun updateAdminConfig(config: AdminConfig) {
        viewModelScope.launch {
            repository.updateAdminConfig(config)
        }
    }
}
