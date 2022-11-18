package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.R
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityEditProfile : AppCompatActivity() {

    val REQUEST_CODE = 200
    var PERMISSION_ALL = 1

    var PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    var selectedImageUri: Uri? = null
    var cameraUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val buttonBack = findViewById<Button>(R.id.backButton)
        buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backPressed", "returnToProfile");
            startActivity(intent)
        }

        val changePhoto = findViewById<TextView>(R.id.changePhotoLink)
        changePhoto.setOnClickListener{
            val pickImageIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            pickImageIntent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            pickImageIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(IMAGE_PICK_CODE, result)
            }.launch(pickImageIntent)
        }

        fillData()

        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                Constants.IMAGE_PICK_CODE -> {}
            }
        }
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

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}