package com.example.brzodolokacije.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_editprofile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityEditProfile : AppCompatActivity() {

    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val buttonBack = findViewById<Button>(R.id.backButton)
        buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backPressed", "returnToProfile");
            startActivity(intent)
        }

        fillData()
    }

    fun pickPhoto(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else {
            val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntext,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        if (requestCode == 1) {
            if (grantResults.size > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            pickedPhoto = data.data
            if (pickedPhoto != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver,pickedPhoto!!)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                    editProfilePicture.setImageBitmap(pickedBitMap)
                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedPhoto)
                    editProfilePicture.setImageBitmap(pickedBitMap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fillData()
    {
        val sessionManager = SessionManager(this)
        val usernameSm = sessionManager.fetchUsername()

        val retrofit = Client(this).buildService(Api::class.java)
        if (usernameSm != null) {
            retrofit.fetchUserProfileInfo(usernameSm).enqueue(object: Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
                        val userProfileInfoStr: String = response.body()?.message.toString();
                        val gson = Gson()
                        val userProfileInfo: UserProfile = gson.fromJson(userProfileInfoStr, UserProfile::class.java)

                        val name = findViewById<TextView>(R.id.editName)
                        val username = findViewById<TextView>(R.id.editUsername)
                        val description = findViewById<TextView>(R.id.editDescription)
                        val pfp = findViewById<CircleImageView>(R.id.editProfilePicture)

                        name.text = userProfileInfo.name;
                        username.text = userProfileInfo.username
                        description.text = userProfileInfo.description;

//                        val avatarEncoded = userProfileInfo.profilePicture;
//
//                        val imageBytes = Base64.decode(avatarEncoded, Base64.DEFAULT)
//                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                        pfp.setImageBitmap(decodedImage)

                        Picasso.get().load(Constants.BASE_URL + "User/avatar/" + usernameSm).into(pfp)
                    }
                    else
                    {
                        Log.d("error not false", "");
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failed","");
                }

            })
        }
    }
}