package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.brzodolokacije.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*

class ActivityMaps : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap : GoogleMap
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.maps, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
        searchMap.visibility = View.INVISIBLE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)

            return
        }
        var currentlocation = fusedLocationClient.lastLocation.addOnSuccessListener {location ->
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude,location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,12f))
            }
        }

        mMap.setOnMapClickListener ( object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                val location = LatLng(latlng.latitude,latlng.longitude)
                try {
                    var grad = getAdressName(location.latitude,location.longitude)
                    var drzava = getCountryName(location.latitude,location.longitude)
                    var sb = StringBuilder()
                    sb.append(grad).append(", ").append(drzava)
//                    Log.d("Lokacija",sb.toString())
                    val builder = AlertDialog.Builder(this@ActivityMaps)
                    val bit = intent.getStringExtra("bit")
//                    Log.d("bit",bit.toString())
                    builder.setTitle("Confirm location")
//                    builder.setMessage("Test")
                    builder.setPositiveButton("Yes"){dialogInterface, which ->
                        Intent(this@ActivityMaps,ActivityAddPost::class.java).also{
                            it.putExtra("sb",sb.toString())
                            it.putExtra("bitslike",bit)
                            it.putExtra("latitude",location.latitude)
                            it.putExtra("longitude",location.longitude)
                            startActivity(it)
                        }
                    }
                    builder.setNegativeButton("Cancel",null)

                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setOnShowListener(DialogInterface.OnShowListener() {
                        alertDialog.window?.setBackgroundDrawableResource(R.color.light_blue)
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.light_text))
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.light_text))

                    })
                    alertDialog.setCancelable(false)
                    alertDialog.show()
//                    Intent(this@ActivityMaps,ActivityAddPost::class.java).also{
//                        it.putExtra("sb",sb.toString())
//                        startActivity(it)
//                    }
                    Toast.makeText(this@ActivityMaps,sb.toString(),Toast.LENGTH_SHORT).show()
                    mMap.addMarker(MarkerOptions().position(location))
                }catch (e : Exception){

                }

            }

        })
    }

    private fun getAdressName(lat: Double, lon: Double): String {
        var adressName = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        val adress = geocoder.getFromLocation(lat,lon,1)
        adressName = adress[0].locality
        return adressName
    }
    private fun getCountryName(lat: Double, lon: Double): String {
        var adressName = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        val adress = geocoder.getFromLocation(lat,lon,1)
        adressName = adress[0].countryName
        return adressName
    }


}