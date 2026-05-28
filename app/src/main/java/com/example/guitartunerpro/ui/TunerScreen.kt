package com.example.guitartunerpro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitartunerpro.model.Instrument
import com.example.guitartunerpro.model.InstrumentString
import com.example.guitartunerpro.model.Instruments
import com.example.guitartunerpro.model.PegSide
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreen(viewModel: TunerViewModel) {
    val frequency by viewModel.currentFrequency.collectAsState()
    val selectedInstrument by viewModel.selectedInstrument.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Select Instrument",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Divider()
                LazyColumn {
                    val grouped = Instruments.ALL.groupBy { it.category }
                    grouped.forEach { (category, instruments) ->
                        item {
                            Text(
                                text = category,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(instruments) { instrument ->
                            NavigationDrawerItem(
                                label = { Text(instrument.name) },
                                selected = instrument == selectedInstrument,
                                onClick = {
                                    viewModel.selectInstrument(instrument)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Guitar Tuner Pro") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                TunerContent(frequency, selectedInstrument)
            }
        }
    }
}

@Composable
fun TunerContent(frequency: Float, instrument: Instrument) {
    val closestString = if (frequency > 0) instrument.strings.minByOrNull { abs(it.frequency - frequency) } else null
    val diffCents = if (frequency > 0 && closestString != null) {
        1200 * log2(frequency / closestString.frequency).toFloat()
    } else {
        0f
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = instrument.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            InstrumentHeadstock(instrument, closestString, diffCents)
        }
        
        closestString?.let {
            Text(
                text = it.note,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold
            )
            TunerGauge(diffCents)
            Text(
                text = String.format(Locale.getDefault(), "%.1f Hz", frequency),
                fontSize = 18.sp
            )
        } ?: run {
            Text("Pluck a string", fontSize = 24.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun InstrumentHeadstock(instrument: Instrument, activeString: InstrumentString?, diffCents: Float) {
    Canvas(modifier = Modifier.size(200.dp, 300.dp)) {
        val width = size.width
        val height = size.height
        
        // Headstock body
        val bodyPath = Path().apply {
            moveTo(width * 0.3f, height * 0.1f)
            lineTo(width * 0.7f, height * 0.1f)
            lineTo(width * 0.8f, height * 0.9f)
            lineTo(width * 0.2f, height * 0.9f)
            close()
        }
        drawPath(path = bodyPath, color = Color(0xFF424242))
        
        // Group strings by side
        val leftStrings = instrument.strings.filter { it.pegSide == PegSide.LEFT }.sortedBy { it.pegIndex }
        val rightStrings = instrument.strings.filter { it.pegSide == PegSide.RIGHT }.sortedBy { it.pegIndex }
        
        fun drawPegs(strings: List<InstrumentString>, xPos: Float, side: PegSide) {
            val count = strings.size
            if (count == 0) return
            
            val startY = height * 0.2f
            val endY = height * 0.8f
            val stepY = if (count > 1) (endY - startY) / (count - 1) else 0f
            
            strings.forEachIndexed { index, string ->
                val py = startY + index * stepY
                val isActive = string == activeString
                val pegColor = if (isActive) {
                    if (abs(diffCents) < 5) Color.Green else Color.Red
                } else {
                    Color(0xFF757575)
                }
                
                // Draw tuning ear
                val earWidth = 20.dp.toPx()
                val earHeight = 10.dp.toPx()
                val earX = if (side == PegSide.LEFT) xPos - earWidth else xPos
                
                drawRoundRect(
                    color = pegColor,
                    topLeft = Offset(earX, py - earHeight / 2),
                    size = Size(earWidth, earHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                
                // Draw peg post
                drawCircle(
                    color = pegColor,
                    radius = 5.dp.toPx(),
                    center = Offset(xPos, py)
                )
            }
        }
        
        drawPegs(leftStrings, width * 0.35f, PegSide.LEFT)
        drawPegs(rightStrings, width * 0.65f, PegSide.RIGHT)
    }
}

@Composable
fun TunerGauge(diffCents: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 32.dp),
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
                end = Offset(size.width / 2, size.height - 30.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Draw needle
            val angle = (diffCents.coerceIn(-50f, 50f) / 50f) * (PI / 4).toFloat()
            val needleEndX = size.width / 2 + radius * sin(angle.toDouble()).toFloat()
            val needleEndY = size.height - radius * cos(angle.toDouble()).toFloat()
            
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
