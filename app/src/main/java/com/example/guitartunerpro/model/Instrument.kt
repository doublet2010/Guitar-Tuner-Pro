package com.example.guitartunerpro.model

data class Instrument(
    val name: String,
    val strings: List<InstrumentString>
)

data class InstrumentString(
    val note: String,
    val frequency: Float
)

object Instruments {
    val GUITAR = Instrument(
        "Guitar",
        listOf(
            InstrumentString("E2", 82.41f),
            InstrumentString("A2", 110.00f),
            InstrumentString("D3", 146.83f),
            InstrumentString("G3", 196.00f),
            InstrumentString("B3", 246.94f),
            InstrumentString("E4", 329.63f)
        )
    )

    // To add more instruments, simply define them here:
    // val UKULELE = Instrument("Ukulele", listOf(...))
    
    val ALL = listOf(GUITAR)
}
