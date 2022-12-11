package com.example.brzodolokacije.Fragments2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.ShowPostActivity
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.PaginationResponse
import com.example.brzodolokacije.ModelsDto.PinDto
import com.example.brzodolokacije.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileVisitLocationsFragment : Fragment(),OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var user : String

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
        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_locations, container, false)
        user = arguments?.getString("username").toString()
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationsFragment.
         */
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationsFragment.
         */

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        if (user != "null"){
            retrofit.getLocationsByUser(user).enqueue(object: Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
                        val listOfPins: String = response.body()?.message.toString()
                        val typeToken = object : TypeToken<List<PinDto>>() {}.type
                        val pins = Gson().fromJson<List<PinDto>>(listOfPins, typeToken)

//                        Toast.makeText(requireActivity(),pins.toString(),Toast.LENGTH_SHORT).show()
                        var i = 0
                        while(i < pins!!.size) {
                            val latLng = LatLng(pins[i].latitude.toDouble(), pins[i].longitude.toDouble())
//                            mMap.addMarker(MarkerOptions().position(latLng).title(pins[i].id.toString()))
                            loadImage(latLng, Constants.BASE_URL + "Post/postPhoto/" + pins[i].id.toString(),pins[i].id.toString())
                            i++
                        }
                    }
                    else
                    {
                        Toast.makeText(requireActivity(),"Unable to load user's locations",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(),"Error. Something went wrong",Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun loadImage(longlat : LatLng, path : String,id : String)
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
                    mMap.setOnMarkerClickListener {
                        val intent = Intent(requireActivity(), ShowPostActivity::class.java)
                        intent.putExtra("showPost", id);
                        startActivity(intent)
                        true
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}