package com.example.brzodolokacije.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.R
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_editprofile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityEditProfile : AppCompatActivity() {

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

                        val avatarEncoded = userProfileInfo.profilePicture;

                        val imageBytes = Base64.decode(avatarEncoded, Base64.DEFAULT)
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        pfp.setImageBitmap(decodedImage)
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