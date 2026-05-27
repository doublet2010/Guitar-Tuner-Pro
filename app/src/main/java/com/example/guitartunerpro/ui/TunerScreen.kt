package com.example.guitartunerpro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitartunerpro.model.InstrumentString
import kotlin.math.abs
import kotlin.math.log2

@Composable
fun TunerScreen(viewModel: TunerViewModel) {
    val frequency by viewModel.currentFrequency.collectAsState()
    val instrument by viewModel.selectedInstrument.collectAsState()

    val closestString = instrument.strings.minByOrNull { abs(it.frequency - frequency) }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = instrument.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        closestString?.let {
            Text(
                text = it.note,
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            val diff = if (frequency > 0) {
                // Calculate cents difference
                1200 * log2(frequency / it.frequency)
            } else {
                0f
            }
            
            TunerGauge(diff)
            
            Text(
                text = String.format("%.1f Hz", frequency),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun TunerGauge(diffCents: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height)
            val radius = size.height * 0.8f
            
            // Draw scale
            drawLine(
                color = Color.Gray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx()
            )
            
            // Draw center mark
            drawLine(
                color = Color.Green,
                start = Offset(size.width / 2, size.height),
                end = Offset(size.width / 2, size.height - 40.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Draw needle
            val angle = (diffCents.coerceIn(-50f, 50f) / 50f) * (Math.PI / 4).toFloat()
            val needleEndX = size.width / 2 + radius * Math.sin(angle.toDouble()).toFloat()
            val needleEndY = size.height - radius * Math.cos(angle.toDouble()).toFloat()
            
            drawLine(
                color = if (abs(diffCents) < 5) Color.Green else Color.Red,
                start = center,
                end = Offset(needleEndX, needleEndY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
