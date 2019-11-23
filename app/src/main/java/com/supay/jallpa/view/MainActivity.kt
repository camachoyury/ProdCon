package com.supay.jallpa.view

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.os.Build

import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import java.util.ArrayList

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.collect.MapMaker
import com.google.gson.Gson

import com.supay.core.Seller
import com.supay.jallpa.LocationTrack
import com.supay.jallpa.R
import com.supay.jallpa.ValidatePhoneActivity
import com.supay.jallpa.viewmodel.TrackViewModel
import com.supay.jallpa.viewmodel.getViewModel

import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    override fun onMarkerDragEnd(p0: Marker?) {
        mMap.clear()
        mMap.animateCamera(CameraUpdateFactory.newLatLng(p0?.getPosition()));

    }

    override fun onMarkerDragStart(p0: Marker?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMarkerDrag(p0: Marker?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mMap = googleMap
        }
        mMap.isMyLocationEnabled = true;
        mMap.setOnMarkerDragListener(this)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()

        val location = locationManager.getLastKnownLocation(
            locationManager
                .getBestProvider(criteria, false)!!
        )
        val latitude = location!!.latitude
        val longitude = location.longitude
        val myLocation = LatLng(latitude, longitude)
        updateMarker(myLocation)
    }

    private fun updateMarker(myLocation: LatLng) {
        mMap.clear()
        marker = mMap.addMarker(
            MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).draggable(
                    true
                )
        )


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0f))
    }


    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    internal lateinit var locationTrack: LocationTrack
    var mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    val viewModel by lazy {
        getViewModel { TrackViewModel() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissions.add(ACCESS_FINE_LOCATION)
        permissions.add(ACCESS_COARSE_LOCATION)
        permissions.add(CALL_PHONE)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        permissionsToRequest = findUnAskedPermissions(permissions)
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0)
                requestPermissions(permissionsToRequest!!.toTypedArray(),
                    ALL_PERMISSIONS_RESULT
                )
        }


        val btn = findViewById<FloatingActionButton>(R.id.btn)


        btn.setOnClickListener {
            locationTrack = LocationTrack(this@MainActivity)



            if (locationTrack.canGetLocation()) {


                val longitude = locationTrack.getLongitude()
                val latitude = locationTrack.getLatitude()
                updateLocation(longitude,latitude)



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


    fun updateLocation( longitude:Double, latitude: Double){

        updateMarker(LatLng(latitude,longitude))
        val sharedPref = getSharedPreferences(
            ValidatePhoneActivity.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE
        )

        val gson = Gson()
        val json = sharedPref.getString("USER", "")
        val obj = gson.fromJson<Seller>(json, Seller::class.java)

        if (obj != null){
            try {
                mFirebaseDatabaseReference.child("productores").child(obj.id).child("location").child("longitude")
                    .setValue(longitude)
                mFirebaseDatabaseReference.child("productores").child(obj.id).child("location").child("latitude")
                    .setValue(latitude)
            } catch (e: Exception) {
                e.printStackTrace()
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
