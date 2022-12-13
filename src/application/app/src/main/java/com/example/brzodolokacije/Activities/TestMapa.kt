package com.example.brzodolokacije.Activities


import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors


class TestMapa : AppCompatActivity() , OnMapReadyCallback , GoogleMap.OnMarkerClickListener{
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


    }
    private fun getMarkerBitmapFromView(@DrawableRes resId: Int): Bitmap? {
        val customMarkerView: View = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(com.example.brzodolokacije.R.layout.view_custom_marker, null)
        val markerImageView: ImageView = customMarkerView.findViewById(R.id.profile_image) as ImageView
        markerImageView.setImageResource(resId)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.getMeasuredWidth(),
            customMarkerView.getMeasuredHeight()
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable: Drawable = customMarkerView.getBackground()
        if (drawable != null) drawable.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
//        Log.d("Log" ,"onMapReady() called with");
        MapsInitializer.initialize(this);
        addCustomMarker();

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
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker!!)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addCustomMarker() {
//        Log.d("TAG", "addCustomMarker()")
        if (mMap == null) {
            return
        }
        val sessionManager= this?.let { SessionManager(it) }
        val usernameSm = sessionManager?.fetchUsername()

        val retrofit = Client(this).buildService(Api::class.java)
        if (usernameSm != null){
            retrofit.getUserPosts(usernameSm).enqueue(object: Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if (response.body()?.error.toString() == "false") {
                        val json = response.body()?.message.toString()
//                        Log.d("json", json)
                        val typeToken = object : TypeToken<List<Int>>() {}.type
                        val idList = Gson().fromJson<List<Int>>(json, typeToken)

                        val ids = mutableListOf<String>()
                        for (id in idList) {
                            ids.add(Constants.BASE_URL + "Post/postPhoto/" + id.toString())
//                            Log.d("id",id.toString())
                        }

                        for (id in ids) {
//                            Log.d("idl",id)
                        }
                        val mDummyLatLng = LatLng(44.014772, 20.914728)
                        loadImage(mDummyLatLng,ids.get(0))

                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                    Log.d("failure", "")
                }
            })
        }
        // adding a marker on map with image from  drawable

        // adding a marker on map with image from  drawable

    }

    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                this,
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }
}