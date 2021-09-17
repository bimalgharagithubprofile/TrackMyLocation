package com.sample.trackmylocation.presenter

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sample.trackmylocation.database.AppDatabase
import com.sample.trackmylocation.database.entities.EntityJourneyDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.*

class HomeActivityPresenter(private var view: View) {

    suspend fun getJourneyList(context: Context) : LiveData<List<EntityJourneyDetails>> {
        return withContext(Dispatchers.IO) {
            val db: AppDatabase = AppDatabase.invoke(context)

            db.getJourneyDetailsDao().getAll()
        }
    }

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
    }
}