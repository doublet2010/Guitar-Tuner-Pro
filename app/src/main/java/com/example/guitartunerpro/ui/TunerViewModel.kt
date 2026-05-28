package com.example.guitartunerpro.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartunerpro.core.PitchDetector
import com.example.guitartunerpro.model.Instrument
import com.example.guitartunerpro.model.InstrumentString
import com.example.guitartunerpro.model.Instruments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class Screen { TUNER, ABOUT, PRIVACY }

class TunerViewModel(application: Application) : AndroidViewModel(application) {
    private val pitchDetector = PitchDetector()
    private val prefs = application.getSharedPreferences("tuner_prefs", Context.MODE_PRIVATE)
    
    private val _currentFrequency = MutableStateFlow(0f)
    val currentFrequency: StateFlow<Float> = _currentFrequency
    
    private val _selectedInstrument = MutableStateFlow(loadSelectedInstrument())
    val selectedInstrument: StateFlow<Instrument> = _selectedInstrument

    private val _isAutoMode = MutableStateFlow(true)
    val isAutoMode: StateFlow<Boolean> = _isAutoMode

    private val _manualString = MutableStateFlow<InstrumentString?>(null)
    val manualString: StateFlow<InstrumentString?> = _manualString

    private val _currentScreen = MutableStateFlow(Screen.TUNER)
    val currentScreen: StateFlow<Screen> = _currentScreen

    // Subscription status (Stub for Google Play Billing)
    private val _isPro = MutableStateFlow(true) // Default true for testing
    val isPro: StateFlow<Boolean> = _isPro

    init {
        startTuning()
    }

    private fun startTuning() {
        viewModelScope.launch {
            pitchDetector.startDetection().collect { frequency ->
                _currentFrequency.value = frequency
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectInstrument(instrument: Instrument) {
        _selectedInstrument.value = instrument
        _manualString.value = null // Reset manual selection when changing instrument
        saveSelectedInstrument(instrument.name)
    }

    fun toggleAutoMode(enabled: Boolean) {
        _isAutoMode.value = enabled
        if (!enabled && _manualString.value == null) {
            _manualString.value = _selectedInstrument.value.strings.firstOrNull()
        }
    }

    fun selectManualString(string: InstrumentString) {
        _manualString.value = string
        _isAutoMode.value = false
    }

    private fun saveSelectedInstrument(name: String) {
        prefs.edit().putString("last_instrument", name).apply()
    }

    private fun loadSelectedInstrument(): Instrument {
        val name = prefs.getString("last_instrument", Instruments.GUITAR_STANDARD.name)
        return Instruments.ALL.find { it.name == name } ?: Instruments.GUITAR_STANDARD
    }
}
