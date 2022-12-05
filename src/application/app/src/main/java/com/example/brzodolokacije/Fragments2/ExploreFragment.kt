package com.example.brzodolokacije.Fragments2

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.PinDto
import com.example.brzodolokacije.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors


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

        val languageToLoad = "US"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity?.getBaseContext()?.getResources()?.updateConfiguration(
            config,
            activity?.getBaseContext()?.getResources()?.getDisplayMetrics()
        )
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
                    mMap.clear()
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
                        val retrofit = Client(requireActivity()).buildService(Api::class.java)
                        retrofit.onMapLocation(sb.toString()).enqueue(object: Callback<DefaultResponse>{
                            override fun onResponse(
                                call: Call<DefaultResponse>,
                                response: Response<DefaultResponse>
                            ) {
                                if(response.body()?.error.toString() == "false")
                                {
                                    val listOfPins: String = response.body()?.message.toString()
                                    val typeToken = object : TypeToken<List<PinDto>>() {}.type
                                    val pins = Gson().fromJson<List<PinDto>>(listOfPins, typeToken)

//                                    Toast.makeText(requireActivity(),pins.toString(),Toast.LENGTH_SHORT).show()
                                    var i = 0
                                    while(i < pins!!.size) {
                                        val latLng = LatLng(pins[i].latitude.toDouble(), pins[i].longitude.toDouble())

                                        loadImage(latLng, Constants.BASE_URL + "Post/postPhoto/" + pins[i].id.toString())
                                        i++
                                    }

                                }
                            }

                            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                        })

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
//                mMap.addMarker(MarkerOptions().position(currentLatLong).title(sb.toString()))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,12f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)

        markerOptions.title("")

        mMap.addMarker(markerOptions)
    }
    private fun loadImage(longlat : LatLng, path : String)
    {
        //image.layoutParams.height=Constants.screenHeight
        val executor = Executors.newSingleThreadExecutor()

        val handler = android.os.Handler(Looper.getMainLooper())

        var i: Bitmap? = null
        executor.execute {

            // Image URL
            val imageURL = path
            try {
                val `in` = java.net.URL(imageURL).openStream()
                i = BitmapFactory.decodeStream(`in`)
                handler.post {
                    val smallMarker = Bitmap.createScaledBitmap(i!!, 150, 150, false)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(longlat)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker!!))
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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