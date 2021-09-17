package com.sample.trackmylocation.presenter

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.sample.trackmylocation.database.AppDatabase
import com.sample.trackmylocation.database.entities.EntityJourneyDetails
import com.sample.trackmylocation.model.LastLocation
import com.sample.trackmylocation.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class HomeActivityPresenter(private var view: View) {

    val journeyList = MutableLiveData<List<EntityJourneyDetails>>()


    suspend fun getJourneyList(context: Context) {
        val db: AppDatabase = AppDatabase.invoke(context)

        val rs = db.getJourneyDetailsDao().getAll()

        //journeyList.postValue(rs)

    }

    interface View {
        fun updatedJourneyList(list: String?)

        fun showProgressBar()
        fun hideProgressBar()
    }
}