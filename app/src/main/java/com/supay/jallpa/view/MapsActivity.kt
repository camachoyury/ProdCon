package com.supay.jallpa.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.supay.jallpa.R
import com.supay.jallpa.viewmodel.MapViewModel
import com.supay.jallpa.viewmodel.getViewModel
import com.google.android.gms.maps.model.Marker
import com.supay.core.Seller
import com.google.android.gms.maps.model.LatLngBounds

import android.content.res.Resources
import com.google.android.gms.maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,ActionBottomDialogFragment.ItemClickListener  {

    private lateinit var mMap: GoogleMap

    val  mapViewModel by lazy { getViewModel { MapViewModel() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapViewModel.updateUI()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mapViewModel.producers.observe(this, Observer {
            showLocationsOnMap(it)
        })
    }

    private fun showLocationsOnMap( items: List<Seller>) {

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
                    .title(producer.name)

            )
            marker.tag = producer
            builder.include(marker.position)
        }


        var bounds = builder.build()
        var cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(40));
        mMap.moveCamera(cameraUpdate);

    }

    private fun  dpToPx(dp: Int): Int {
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

}
