package com.example.guitartunerpro.model

data class Instrument(
    val name: String,
    val category: String,
    val strings: List<InstrumentString>
)

data class InstrumentString(
    val note: String,
    val frequency: Float
)

object Instruments {
    val GUITAR_STANDARD = Instrument(
        "Guitar (Standard)",
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

    val GUITAR_DROP_D = Instrument(
        "Guitar (Drop D)",
        "Guitar",
        listOf(
            InstrumentString("D2", 73.42f),
            InstrumentString("A2", 110.00f),
            InstrumentString("D3", 146.83f),
            InstrumentString("G3", 196.00f),
            InstrumentString("B3", 246.94f),
            InstrumentString("E4", 329.63f)
        )
    )

    val GUITAR_OPEN_D5 = Instrument(
        "Guitar (Open D5)",
        "Guitar",
        listOf(
            InstrumentString("D2", 73.42f),
            InstrumentString("A2", 110.00f),
            InstrumentString("D3", 146.83f),
            InstrumentString("A3", 220.00f),
            InstrumentString("D4", 293.66f),
            InstrumentString("D4", 293.66f)
        )
    )

    val UKULELE = Instrument(
        "Ukulele (Standard)",
        "Ukulele",
        listOf(
            InstrumentString("G4", 392.00f),
            InstrumentString("C4", 261.63f),
            InstrumentString("E4", 329.63f),
            InstrumentString("A4", 440.00f)
        )
    )

    val BANJO = Instrument(
        "Banjo (Standard)",
        "Banjo",
        listOf(
            InstrumentString("G4", 392.00f),
            InstrumentString("D3", 146.83f),
            InstrumentString("G3", 196.00f),
            InstrumentString("B3", 246.94f),
            InstrumentString("D4", 293.66f)
        )
    )

    val BASS_GUITAR = Instrument(
        "Bass Guitar (Standard)",
        "Bass",
        listOf(
            InstrumentString("E1", 41.20f),
            InstrumentString("A1", 55.00f),
            InstrumentString("D2", 73.42f),
            InstrumentString("G2", 98.00f)
        )
    )

    val ALL = listOf(
        GUITAR_STANDARD,
        GUITAR_DROP_D,
        GUITAR_OPEN_D5,
        UKULELE,
        BANJO,
        BASS_GUITAR
    )
}
