package com.sample.trackmylocation.view

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sample.trackmylocation.R
import com.sample.trackmylocation.presenter.HomeActivityPresenter
import com.sample.trackmylocation.utils.log
import com.sample.trackmylocation.utils.toast
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeActivityPresenter.View {

    private lateinit var presenter: HomeActivityPresenter

    companion object {
        val PERMISSIONS_REQUEST_ACCESS_LOCATION_FOREGROUND = 1
        val PERMISSIONS_REQUEST_ACCESS_LOCATION_BACKGROUND = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomeActivityPresenter(this)

        initClickListener()

    }


    private fun initClickListener() {
        btn_start.setOnClickListener {
            log("Start Journey")
            validatePermission()
        }
    }

    private fun goToMapsActivity() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            log("Location Disabled")
            toast("Turn ON Location")
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            log("Location Enabled")
            startActivity(Intent(this@HomeActivity, MapsActivity::class.java))
        }
    }


    private fun validatePermission() {
        val hasPermissionForeground = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasPermissionBackground = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

        if(hasPermissionForeground && hasPermissionBackground){
            goToMapsActivity()
        } else if(!hasPermissionForeground) {
            ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_LOCATION_FOREGROUND)
        } else if(!hasPermissionBackground) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showDialog()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), PERMISSIONS_REQUEST_ACCESS_LOCATION_BACKGROUND)
            }
        }
    }

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(R.string.background_location_permission_rationale)
            .setCancelable(false)
            .setPositiveButton("Proceed") { dialog, id ->
                toast("Allow all the time")

                ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), PERMISSIONS_REQUEST_ACCESS_LOCATION_BACKGROUND)

                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Permission Needed")
        alert.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_LOCATION_FOREGROUND,
            PERMISSIONS_REQUEST_ACCESS_LOCATION_BACKGROUND -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //cross check all the permission
                    validatePermission()
                }
            }
        }
    }

    override fun updatedJourneyList(list: String?) {
    }

    override fun showProgressBar() {
    }

    override fun hideProgressBar() {
    }
}