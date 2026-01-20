package com.mikey.atlasoffline

object RankArt {

    fun imageForRankTitle(title: String): Int? = when (title.uppercase()) {
        "RECRUIT" -> R.drawable.icarus
        "HOPLITE" -> R.drawable.perseus
        "ENOMOTARCH" -> R.drawable.theseus
        "LOCHAGOS" -> R.drawable.achilles
        "PENTEKONTER" -> R.drawable.odysseus
        "TAXIARCH" -> R.drawable.jason
        "STRATEGOS" -> R.drawable.leonidas
        "POLEMARCH" -> R.drawable.ares
        "SPARTAN" -> R.drawable.heracles
        "HERO" -> R.drawable.bellerophon
        "DEMIGOD" -> R.drawable.achillesdemigod
        "TITAN" -> R.drawable.cronus
        "OLYMPIAN" -> R.drawable.zeus
        else -> null
    }
}
