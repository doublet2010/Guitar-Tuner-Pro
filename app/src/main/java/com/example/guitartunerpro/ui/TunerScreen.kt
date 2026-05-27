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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitartunerpro.model.Instrument
import com.example.guitartunerpro.model.Instruments
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
                1200 * log2(frequency / it.frequency).toFloat()
            } else {
                0f
            }
            
            TunerGauge(diff)
            
            Text(
                text = String.format(Locale.getDefault(), "%.1f Hz", frequency),
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
