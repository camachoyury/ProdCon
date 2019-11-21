package com.supay.jallpa.view

import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build

import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import java.util.ArrayList

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.supay.core.Location
import com.supay.jallpa.LocationTrack
import com.supay.jallpa.R
import com.supay.jallpa.viewmodel.TrackViewModel
import com.supay.jallpa.viewmodel.getViewModel

class MainActivity : AppCompatActivity() {


    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    internal lateinit var locationTrack: LocationTrack

    val viewModel by lazy {
        getViewModel { TrackViewModel(Injector.trackerRepository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissions.add(ACCESS_FINE_LOCATION)
        permissions.add(ACCESS_COARSE_LOCATION)






//        showFirstTodo()
        permissionsToRequest = findUnAskedPermissions(permissions)
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0)
                requestPermissions(permissionsToRequest!!.toTypedArray(),
                    ALL_PERMISSIONS_RESULT
                )
        }


        val btn = findViewById<Button>(R.id.btn)


        btn.setOnClickListener {
            locationTrack = LocationTrack(this@MainActivity)



            if (locationTrack.canGetLocation()) {


                val longitude = locationTrack.getLongitude()
                val latitude = locationTrack.getLatitude()
                viewModel.setCurrentLocation(Location(longitude, latitude))

                Toast.makeText(
                    getApplicationContext(),
                    "Longitude:" + java.lang.Double.toString(longitude) + "\nLatitude:" + java.lang.Double.toString(
                        latitude
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                locationTrack.showSettingsAlert()
            }
        }

    }


    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(permission) === PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            permissionsRejected.toTypedArray(),
                                            ALL_PERMISSIONS_RESULT
                                        )
                                    }
                                })
                            return
                        }
                    }

                }
            }
        }

    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    protected override fun onDestroy() {
        super.onDestroy()
        if(locationTrack != null){
            locationTrack.stopListener()
        }

    }

    companion object {

        private val ALL_PERMISSIONS_RESULT = 101
    }


}
