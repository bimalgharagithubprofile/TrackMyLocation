package com.sample.trackmylocation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult
import com.sample.trackmylocation.model.LocationEvent
import com.sample.trackmylocation.utils.log
import org.greenrobot.eventbus.EventBus


class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        log("onReceive() context:$context, intent:$intent")

        if (intent.action == ACTION_PROCESS_UPDATES) {
            LocationResult.extractResult(intent).let { locationResult ->
                locationResult?.let {
                    val location = locationResult.locations[0]
                    log("new Location: ${location.latitude}, ${location.longitude} <> ${location.time}")
                    EventBus.getDefault().post(LocationEvent(location))
                }
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "com.sample.trackmylocation.action." +
                    "PROCESS_UPDATES"
    }
}
