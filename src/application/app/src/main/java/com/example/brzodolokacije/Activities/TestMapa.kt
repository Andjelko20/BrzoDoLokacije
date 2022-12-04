package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.brzodolokacije.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TestMapa : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap : GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_locations)
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.maps, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)

//        val longitude = intent.getStringExtra("longitude")?.toDouble()
//        val latitude = intent.getStringExtra("latitude")?.toDouble()
//        val lokacija = intent.getStringExtra("sb")
//        Log.d("long", longitude.toString() + "," + latitude.toString() + "," + lokacija)
//        if (longitude != null && latitude != null) {
//            val latLng = LatLng(latitude, longitude)
//            Log.d("latlng", latLng.toString())
//            mMap.addMarker(MarkerOptions().position(latLng).title(lokacija.toString()))
//        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        val longitude = intent.getStringExtra("longitude")?.toDouble()
        val latitude = intent.getStringExtra("latitude")?.toDouble()
        val lokacija = intent.getStringExtra("sb")
        Log.d("long", longitude.toString() + "," + latitude.toString() + "," + lokacija)
        if (longitude != null && latitude != null) {
            val latLng = LatLng(latitude, longitude)
            Log.d("latlng", latLng.toString())
            mMap.addMarker(MarkerOptions().position(latLng).title(lokacija.toString()))
        }
    }
}