package com.sample.trackmylocation.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/*@Parcelize id from gradle -> androidExtensions -> experimental*/
@Parcelize
@Entity
data class EntityJourneyDetails(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    val startedAt: String,
    val duration: String,
    val distance: String,
    val speed: String
): Parcelable