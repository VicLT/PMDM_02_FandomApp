package edu.victorlamas.fandom.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Fandom(
    val id: Int,
    val name: String,
    val universe: String,
    val description: String,
    val image: String,
    val info: String,
    var fav: Boolean = false,
    var visible: Boolean = true
) : Parcelable