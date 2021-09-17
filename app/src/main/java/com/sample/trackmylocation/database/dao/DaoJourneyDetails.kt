package com.sample.trackmylocation.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.trackmylocation.database.entities.EntityJourneyDetails

@Dao
interface DaoJourneyDetails {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveThis(journey : EntityJourneyDetails) : Long

    @Query("SELECT * FROM EntityJourneyDetails")
    fun getAll() : LiveData<List<EntityJourneyDetails>>

}