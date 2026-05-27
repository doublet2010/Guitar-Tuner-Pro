package com.example.guitartunerpro.core

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.abs

class PitchDetector {
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ) * 2

    fun startDetection(): Flow<Float> = flow {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val buffer = ShortArray(bufferSize)
        audioRecord.startRecording()

        try {
            while (true) {
                val read = audioRecord.read(buffer, 0, bufferSize)
                if (read > 0) {
                    val pitch = detectPitch(buffer, read)
                    if (pitch > 0) {
                        emit(pitch)
                    }
                }
                delay(100)
            }
        } finally {
            audioRecord.stop()
            audioRecord.release()
        }
    }.flowOn(Dispatchers.IO)

    private fun detectPitch(buffer: ShortArray, size: Int): Float {
        // Very basic zero-crossing or autocorrelation implementation
        // For a real app, YIN or MPM would be better.
        // This is a placeholder for a simple autocorrelation.
        
        var maxCorr = -1f
        var bestPeriod = -1
        
        val maxPeriod = sampleRate / 50 // Min 50Hz
        val minPeriod = sampleRate / 1000 // Max 1000Hz
        
        for (period in minPeriod..maxPeriod) {
            var corr = 0f
            for (i in 0 until size - period) {
                corr += (buffer[i].toFloat() * buffer[i + period].toFloat())
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestPeriod = period
            }
        }
        
        return if (bestPeriod > 0) sampleRate.toFloat() / bestPeriod else -1f
    }
}
