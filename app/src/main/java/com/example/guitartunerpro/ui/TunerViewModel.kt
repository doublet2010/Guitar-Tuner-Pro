package com.example.guitartunerpro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartunerpro.core.PitchDetector
import com.example.guitartunerpro.model.Instrument
import com.example.guitartunerpro.model.Instruments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TunerViewModel : ViewModel() {
    private val pitchDetector = PitchDetector()
    
    private val _currentFrequency = MutableStateFlow(0f)
    val currentFrequency: StateFlow<Float> = _currentFrequency
    
    private val _selectedInstrument = MutableStateFlow(Instruments.GUITAR_STANDARD)
    val selectedInstrument: StateFlow<Instrument> = _selectedInstrument

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

    fun selectInstrument(instrument: Instrument) {
        _selectedInstrument.value = instrument
    }
}
