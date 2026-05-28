package com.example.guitartunerpro.model

data class Instrument(
    val name: String,
    val category: String,
    val strings: List<InstrumentString>
)

data class InstrumentString(
    val note: String,
    val frequency: Float,
    val pegSide: PegSide,
    val pegIndex: Int // 0-based index from top to bottom on its side
)

enum class PegSide { LEFT, RIGHT }

object Instruments {
    val GUITAR_STANDARD = Instrument(
        "Guitar (Standard)", "Guitar",
        listOf(
            InstrumentString("E2", 82.41f, PegSide.LEFT, 2),
            InstrumentString("A2", 110.00f, PegSide.LEFT, 1),
            InstrumentString("D3", 146.83f, PegSide.LEFT, 0),
            InstrumentString("G3", 196.00f, PegSide.RIGHT, 0),
            InstrumentString("B3", 246.94f, PegSide.RIGHT, 1),
            InstrumentString("E4", 329.63f, PegSide.RIGHT, 2)
        )
    )

    val GUITAR_DROP_D = Instrument(
        "Guitar (Drop D)", "Guitar",
        listOf(
            InstrumentString("D2", 73.42f, PegSide.LEFT, 2),
            InstrumentString("A2", 110.00f, PegSide.LEFT, 1),
            InstrumentString("D3", 146.83f, PegSide.LEFT, 0),
            InstrumentString("G3", 196.00f, PegSide.RIGHT, 0),
            InstrumentString("B3", 246.94f, PegSide.RIGHT, 1),
            InstrumentString("E4", 329.63f, PegSide.RIGHT, 2)
        )
    )

    val GUITAR_OPEN_D5 = Instrument(
        "Guitar (Open D5)", "Guitar",
        listOf(
            InstrumentString("D2", 73.42f, PegSide.LEFT, 2),
            InstrumentString("A2", 110.00f, PegSide.LEFT, 1),
            InstrumentString("D3", 146.83f, PegSide.LEFT, 0),
            InstrumentString("A3", 220.00f, PegSide.RIGHT, 0),
            InstrumentString("D4", 293.66f, PegSide.RIGHT, 1),
            InstrumentString("D4", 293.66f, PegSide.RIGHT, 2)
        )
    )

    val GUITAR_12_STRING = Instrument(
        "Guitar (12 String)", "Guitar",
        listOf(
            InstrumentString("E2", 82.41f, PegSide.LEFT, 5),
            InstrumentString("E3", 164.81f, PegSide.LEFT, 4),
            InstrumentString("A2", 110.00f, PegSide.LEFT, 3),
            InstrumentString("A3", 220.00f, PegSide.LEFT, 2),
            InstrumentString("D3", 146.83f, PegSide.LEFT, 1),
            InstrumentString("D4", 293.66f, PegSide.LEFT, 0),
            InstrumentString("G3", 196.00f, PegSide.RIGHT, 0),
            InstrumentString("G4", 392.00f, PegSide.RIGHT, 1),
            InstrumentString("B3", 246.94f, PegSide.RIGHT, 2),
            InstrumentString("B3", 246.94f, PegSide.RIGHT, 3),
            InstrumentString("E4", 329.63f, PegSide.RIGHT, 4),
            InstrumentString("E4", 329.63f, PegSide.RIGHT, 5)
        )
    )

    val UKULELE = Instrument(
        "Ukulele", "Ukulele",
        listOf(
            InstrumentString("G4", 392.00f, PegSide.LEFT, 1),
            InstrumentString("C4", 261.63f, PegSide.LEFT, 0),
            InstrumentString("E4", 329.63f, PegSide.RIGHT, 0),
            InstrumentString("A4", 440.00f, PegSide.RIGHT, 1)
        )
    )

    val BANJO = Instrument(
        "Banjo", "Banjo",
        listOf(
            InstrumentString("G4", 392.00f, PegSide.LEFT, 2),
            InstrumentString("D3", 146.83f, PegSide.LEFT, 1),
            InstrumentString("G3", 196.00f, PegSide.LEFT, 0),
            InstrumentString("B3", 246.94f, PegSide.RIGHT, 0),
            InstrumentString("D4", 293.66f, PegSide.RIGHT, 1)
        )
    )

    val BASS_GUITAR = Instrument(
        "Bass Guitar", "Bass",
        listOf(
            InstrumentString("E1", 41.20f, PegSide.LEFT, 1),
            InstrumentString("A1", 55.00f, PegSide.LEFT, 0),
            InstrumentString("D2", 73.42f, PegSide.RIGHT, 0),
            InstrumentString("G2", 98.00f, PegSide.RIGHT, 1)
        )
    )

    val ALL = listOf(
        GUITAR_STANDARD,
        GUITAR_DROP_D,
        GUITAR_OPEN_D5,
        GUITAR_12_STRING,
        UKULELE,
        BANJO,
        BASS_GUITAR
    )
}
