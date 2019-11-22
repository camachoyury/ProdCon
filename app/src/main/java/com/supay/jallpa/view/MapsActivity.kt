package com.supay.jallpa.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.supay.jallpa.viewmodel.MapViewModel
import com.supay.jallpa.viewmodel.getViewModel
import com.google.android.gms.maps.model.Marker
import com.supay.core.Seller
import com.supay.jallpa.R
import com.google.android.gms.maps.model.LatLngBounds
import android.content.res.Resources
import com.google.android.gms.maps.*
import com.google.android.gms.maps.CameraUpdateFactory
import android.location.Criteria
import android.location.LocationManager
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    ActionBottomDialogFragment.ItemClickListener {

    private lateinit var mMap: GoogleMap

    val mapViewModel by lazy { getViewModel { MapViewModel() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapViewModel.updateUI()
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true;

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()

        val location = locationManager.getLastKnownLocation(
            locationManager
                .getBestProvider(criteria, false)!!
        )
        val latitude = location!!.latitude
        val longitude = location.longitude
        val myLocation = LatLng(latitude, longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10.0f))


        mapViewModel.producers.observe(this, Observer {
            showLocationsOnMap(it)
        })
        mMap.uiSettings.isZoomControlsEnabled = true
    }


    private fun showLocationsOnMap(items: List<Seller>) {

        var latitude: Float
        var longitude: Float
        var marker: Marker
        val builder = LatLngBounds.Builder()
        //Add markers for all locations
        for (producer in items) {

            latitude = producer.location.latitude.toFloat()
            longitude = producer.location.longitude.toFloat()
            var latlang = LatLng(latitude.toDouble(), longitude.toDouble())
            mMap.setOnMarkerClickListener(this);
            marker = mMap.addMarker(
                MarkerOptions()
                    .position(latlang)
                    .title(producer.product.toUpperCase())
                    .snippet(producer.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            marker.tag = producer
            builder.include(marker.position)
        }


        var bounds = builder.build()
        var cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(40));
        mMap.moveCamera(cameraUpdate);

    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
    fun showBottomSheet(seller: Seller) {

        val sellerInfoBottomSheet = ActionBottomDialogFragment.newInstance(seller)
        sellerInfoBottomSheet.show(
            supportFragmentManager,
            ActionBottomDialogFragment.TAG
        )
    }

    override fun onItemClick(item: String) {
//        tvSelectedItem.setText("Selected action item is $item")
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        var seller = marker?.tag as Seller
        showBottomSheet(seller)
        marker.showInfoWindow();
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    }
