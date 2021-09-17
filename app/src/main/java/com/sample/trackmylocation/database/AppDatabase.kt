package com.sample.trackmylocation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sample.trackmylocation.database.dao.DaoJourneyDetails
import com.sample.trackmylocation.database.entities.EntityJourneyDetails

@Database(
    entities = [EntityJourneyDetails::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getJourneyDetailsDao(): DaoJourneyDetails

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "MyDatabase.db"
            ).fallbackToDestructiveMigration().build()
    }
}