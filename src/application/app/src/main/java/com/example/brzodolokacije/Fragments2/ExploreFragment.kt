package com.example.brzodolokacije.Fragments2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExploreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExploreFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mMap : GoogleMap
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = SupportMapFragment.newInstance()
        getParentFragmentManager()
            .beginTransaction()
            .add(R.id.maps, mapFragment)
            .commit()


        searchMap.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                val location: String = searchMap.getQuery().toString().trim()
                var addressList: List<Address>? = null
                Log.d("Lokacija",location)
                if (location != null || location == "") {
                    val geocoder = Geocoder(activity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                        val address: Address = addressList!![0]
                        val latLng = LatLng(address.getLatitude(), address.getLongitude())
                        var grad = "";
                        try {
                            grad = getAdressName(address.getLatitude(),address.getLongitude())
                        }
                        catch (e : Exception){
                            e.printStackTrace()
                        }

                        Log.d("grad",grad)
                        var drzava = getCountryName(address.getLatitude(),address.getLongitude())
                        var sb = StringBuilder()
                        if(grad == "")
                            sb.append(drzava)
                        else
                            sb.append(grad).append(", ").append(drzava)
//                        mMap.addMarker(MarkerOptions().position(latLng).title(sb.toString()))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(activity,"Location misspelled",Toast.LENGTH_SHORT).show()
                       // Log.d("Adress", e.printStackTrace().toString())
                    }
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_maps, container, false)
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1;
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExploreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setupMap()
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)

            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude,location.longitude)
                var grad = getAdressName(currentLatLong.latitude,currentLatLong.longitude)
                var drzava = getCountryName(currentLatLong.latitude,currentLatLong.longitude)
                var sb = StringBuilder()
                sb.append(grad).append(", ").append(drzava)
                mMap.addMarker(MarkerOptions().position(currentLatLong).title(sb.toString()))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,12f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)

        markerOptions.title("")

        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    private fun getAdressName(lat: Double, lon: Double): String {
        var adressName = ""
        val geocoder = Geocoder(this.requireActivity(), Locale.getDefault())
        val adress = geocoder.getFromLocation(lat,lon,1)
        adressName = adress[0].locality
        return adressName
    }
    private fun getCountryName(lat: Double, lon: Double): String {
        var adressName = ""
        val geocoder = Geocoder(this.requireActivity(), Locale.getDefault())
        val adress = geocoder.getFromLocation(lat,lon,1)
        adressName = adress[0].countryName
        return adressName
    }
}