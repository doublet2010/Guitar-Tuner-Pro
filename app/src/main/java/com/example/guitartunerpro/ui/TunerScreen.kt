package com.example.guitartunerpro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
    val isAutoMode by viewModel.isAutoMode.collectAsState()
    val manualString by viewModel.manualString.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Guitar Tuner Pro",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Divider()
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PlayArrow, null) },
                    label = { Text("Tuner") },
                    selected = currentScreen == Screen.TUNER,
                    onClick = {
                        viewModel.navigateTo(Screen.TUNER)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (currentScreen == Screen.TUNER) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Instruments",
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                    val grouped = Instruments.ALL.groupBy { it.category }
                    grouped.forEach { (category, instruments) ->
                        instruments.forEach { instrument ->
                            NavigationDrawerItem(
                                label = { Text(instrument.name) },
                                selected = instrument == selectedInstrument,
                                onClick = {
                                    viewModel.selectInstrument(instrument)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(horizontal = 28.dp)
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text("About") },
                    selected = currentScreen == Screen.ABOUT,
                    onClick = {
                        viewModel.navigateTo(Screen.ABOUT)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Lock, null) },
                    label = { Text("Privacy Policy") },
                    selected = currentScreen == Screen.PRIVACY,
                    onClick = {
                        viewModel.navigateTo(Screen.PRIVACY)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(when(currentScreen) {
                            Screen.TUNER -> "Guitar Tuner Pro"
                            Screen.ABOUT -> "About"
                            Screen.PRIVACY -> "Privacy Policy"
                        })
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (currentScreen == Screen.TUNER) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Auto", style = MaterialTheme.typography.labelMedium)
                                Switch(
                                    checked = isAutoMode,
                                    onCheckedChange = { viewModel.toggleAutoMode(it) },
                                    modifier = Modifier.scale(0.8f)
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentScreen) {
                    Screen.TUNER -> TunerContent(
                        frequency = frequency,
                        instrument = selectedInstrument,
                        isAutoMode = isAutoMode,
                        manualString = manualString,
                        onStringClick = { viewModel.selectManualString(it) }
                    )
                    Screen.ABOUT -> AboutScreen()
                    Screen.PRIVACY -> PrivacyPolicyScreen()
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Guitar Tuner Pro", style = MaterialTheme.typography.headlineMedium)
        Text("Version 1.0.0", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Text(
            "Developer: Tom Travers",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "TTPC Systems",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "tomtravers@ttpc-systems.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "© 2026 TTPC Systems. All rights reserved.",
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "A professional tuning solution for musicians, providing high-precision real-time frequency analysis. Designed to be ad-free and subscription-based to support ongoing development and high-quality maintenance.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Privacy Policy", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            "Last updated: January 2026",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Guitar Tuner Pro is built as a privacy-first application. We believe your data is yours alone.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))
        Text("1. Data Collection", style = MaterialTheme.typography.titleMedium)
        Text("We do not collect, store, or transmit any personal information, location data, or usage statistics. The microphone permission is used strictly for real-time frequency analysis and is processed locally on your device. Audio data is never recorded or saved.")
        Spacer(Modifier.height(12.dp))
        Text("2. Data Sharing", style = MaterialTheme.typography.titleMedium)
        Text("Since we do not collect any data, we have nothing to share with third parties, advertisers, or government agencies.")
        Spacer(Modifier.height(12.dp))
        Text("3. Permissions", style = MaterialTheme.typography.titleMedium)
        Text("RECORD_AUDIO: Required to analyze the pitch of your instrument. Audio is processed in volatile memory and discarded immediately.")
        Spacer(Modifier.height(12.dp))
        Text("4. Payments", style = MaterialTheme.typography.titleMedium)
        Text("All subscription and license payments are handled securely through the Google Play Store. We do not have access to your credit card information or billing details.")
    }
}

@Composable
fun TunerContent(
    frequency: Float,
    instrument: Instrument,
    isAutoMode: Boolean,
    manualString: InstrumentString?,
    onStringClick: (InstrumentString) -> Unit
) {
    val activeString = if (isAutoMode) {
        if (frequency > 0) instrument.strings.minByOrNull { abs(it.frequency - frequency) } else null
    } else {
        manualString
    }
    
    val diffCents = if (frequency > 0 && activeString != null) {
        1200 * log2(frequency / activeString.frequency).toFloat()
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
        
        Box(
            modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            InstrumentHeadstock(
                instrument = instrument,
                activeString = activeString,
                diffCents = diffCents,
                onStringClick = onStringClick
            )
        }
        
        activeString?.let {
            Text(
                text = it.note,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold
            )
            TunerGauge(diffCents, modifier = Modifier.height(160.dp))
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
fun InstrumentHeadstock(
    instrument: Instrument,
    activeString: InstrumentString?,
    diffCents: Float,
    onStringClick: (InstrumentString) -> Unit
) {
    val leftStrings = instrument.strings.filter { it.pegSide == PegSide.LEFT }
    val rightStrings = instrument.strings.filter { it.pegSide == PegSide.RIGHT }
    val maxPegsPerSide = max(leftStrings.size, rightStrings.size)
    val headstockHeight = (100 + (maxPegsPerSide * 25)).dp

    val pegBounds = remember { mutableMapOf<InstrumentString, RectData>() }

    Canvas(
        modifier = Modifier
            .size(160.dp, headstockHeight)
            .pointerInput(instrument) {
                detectTapGestures { offset ->
                    pegBounds.forEach { (string, rect) ->
                        if (offset.x >= rect.left && offset.x <= rect.right &&
                            offset.y >= rect.top && offset.y <= rect.bottom
                        ) {
                            onStringClick(string)
                        }
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height
        
        // Headstock body
        val bodyPath = Path().apply {
            moveTo(width * 0.35f, height * 0.05f)
            lineTo(width * 0.65f, height * 0.05f)
            lineTo(width * 0.75f, height * 0.85f)
            lineTo(width * 0.25f, height * 0.85f)
            close()
        }
        drawPath(path = bodyPath, color = Color(0xFF424242))
        
        // Draw the Nut
        val nutY = height * 0.92f
        drawRect(
            color = Color(0xFFBDBDBD),
            topLeft = Offset(width * 0.22f, nutY),
            size = Size(width * 0.56f, 6.dp.toPx())
        )
        
        fun drawPegsAndStrings(strings: List<InstrumentString>, xPos: Float, side: PegSide) {
            val count = strings.size
            if (count == 0) return
            
            val startY = height * 0.15f
            val endY = height * 0.75f
            val stepY = if (count > 1) (endY - startY) / (count - 1) else 0f
            
            strings.sortedBy { it.pegIndex }.forEachIndexed { index, string ->
                val py = if (count > 1) startY + index * stepY else (startY + endY) / 2
                val isActive = string == activeString
                val pegColor = if (isActive) {
                    if (abs(diffCents) < 5) Color.Green else Color.Red
                } else {
                    Color(0xFF757575)
                }
                
                // Bigger Tuning Ears (Ears are 28x14 now)
                val earWidth = 28.dp.toPx()
                val earHeight = 14.dp.toPx()
                val earX = if (side == PegSide.LEFT) xPos - earWidth - 4.dp.toPx() else xPos + 4.dp.toPx()
                
                pegBounds[string] = RectData(
                    left = earX - 10f,
                    top = py - earHeight / 2 - 10f,
                    right = earX + earWidth + 10f,
                    bottom = py + earHeight / 2 + 10f
                )

                drawRoundRect(
                    color = pegColor,
                    topLeft = Offset(earX, py - earHeight / 2),
                    size = Size(earWidth, earHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                
                drawCircle(
                    color = pegColor,
                    radius = 5.dp.toPx(),
                    center = Offset(xPos, py)
                )

                // Draw String from peg to nut
                val nutWidth = width * 0.56f
                val nutStartX = width * 0.22f
                
                // Distribute strings evenly across the nut based on total string count
                val totalStrings = instrument.strings.size
                val stringSpacing = nutWidth / (totalStrings + 1)
                
                // Logic to avoid crossing: 
                // Left side: top peg goes to inner nut slot, bottom peg goes to outer nut slot.
                // Right side: top peg goes to inner nut slot, bottom peg goes to outer nut slot.
                val totalLeft = leftStrings.size
                val globalNutIndex = if (side == PegSide.LEFT) {
                    (totalLeft - 1) - index
                } else {
                    totalLeft + index
                }
                
                val nutStringX = nutStartX + (globalNutIndex + 1) * stringSpacing
                
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(xPos, py),
                    end = Offset(nutStringX, nutY),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Draw string extending past nut
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(nutStringX, nutY),
                    end = Offset(nutStringX, height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        
        drawPegsAndStrings(leftStrings, width * 0.38f, PegSide.LEFT)
        drawPegsAndStrings(rightStrings, width * 0.62f, PegSide.RIGHT)
    }
}

data class RectData(val left: Float, val top: Float, val right: Float, val bottom: Float)

@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

@Composable
fun TunerGauge(diffCents: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height)
            val radius = size.height * 0.9f

            // Draw scale background arc
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 225f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 2.dp.toPx())
            )

            // Tick marks every 10 cents for better resolution
            for (i in -5..5) {
                val tickCents = i * 10f
                val tickAngleRad = (tickCents / 50f) * (PI / 4).toFloat()
                val start = Offset(
                    center.x + radius * sin(tickAngleRad.toDouble()).toFloat(),
                    center.y - radius * cos(tickAngleRad.toDouble()).toFloat()
                )
                val tickLen = if (i == 0) 20.dp.toPx() else if (i % 5 == 0) 15.dp.toPx() else 8.dp.toPx()
                val end = Offset(
                    center.x + (radius - tickLen) * sin(tickAngleRad.toDouble()).toFloat(),
                    center.y - (radius - tickLen) * cos(tickAngleRad.toDouble()).toFloat()
                )
                drawLine(
                    color = Color.Gray,
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Green cone for acceptable tuning range (+/- 5 cents)
            val acceptableRange = 5f
            val sweepAngle = (acceptableRange / 50f) * 90f
            drawArc(
                color = Color.Green.copy(alpha = 0.2f),
                startAngle = 270f - sweepAngle / 2,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // Center line for perfect tune
            drawLine(
                color = Color.Green,
                start = center,
                end = Offset(center.x, center.y - radius),
                strokeWidth = 3.dp.toPx()
            )

            val angle = (diffCents.coerceIn(-50f, 50f) / 50f) * (PI / 4).toFloat()
            val needleEndX = center.x + radius * sin(angle.toDouble()).toFloat()
            val needleEndY = center.y - radius * cos(angle.toDouble()).toFloat()

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
