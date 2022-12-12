package com.example.brzodolokacije.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.Models.Validation
import com.example.brzodolokacije.ModelsDto.EditProfileDto
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ActivityEditProfile : AppCompatActivity() {

    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null
    var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val buttonBack = findViewById<Button>(R.id.backButton)
        buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        saveChangesProfile.setOnClickListener{
            val editName = findViewById<EditText>(R.id.editNameProfile)
            val editUsername = findViewById<EditText>(R.id.editUsernameProfile)
            val editDescription = findViewById<EditText>(R.id.editDescriptionProfile)
            val validation = Validation()

            val name = editName.text.toString().trim()
            val username = editUsername.text.toString().trim()
            val description = editDescription.text.toString().trim()

            //username je obavezan, opis i name nisu
            if(username.isEmpty()){
                editUsername.error = "Username required"
                editUsername.requestFocus()
                return@setOnClickListener
            }

            if(!validation.checkUsername(username)){
                editUsername.error = "Username must contain at least 6 characters (lowercase letters, numbers and _ only)"
                editUsername.requestFocus()
                return@setOnClickListener
            }

            val newData = EditProfileDto(name, username, description)
            val sessionManager = SessionManager(this)
            val retrofit = Client(this).buildService(Api::class.java)

            if(file != null)
            {
                val picture = MultipartBody.Part.createFormData(
                    "picture",
                    file!!.name,
                    RequestBody.create(MediaType.parse("image/*"), file)
                )
                retrofit.uploadNewAvatar(picture).enqueue(object : Callback<DefaultResponse>{
                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
                        if(response.body()?.error.toString() == "false")
                        {
//                            Log.d("uspesno", "")
                            sendData(newData, sessionManager, retrofit)
                        }
                        else if(response.body()?.error.toString() == "true")
                        {
                            Log.d("error true", response.body()?.message.toString())
                        }
                        else
                        {
                            Log.d("error - " + response.body()?.error.toString(), " message " + response.body()?.message.toString())
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Log.d("failed slika", t.message.toString())
                    }
                })
            }
            else
            {
                sendData(newData, sessionManager, retrofit)
            }

        }

        fillData()
    }

    private fun sendData(newData: EditProfileDto, sessionManager: SessionManager, retrofit: Api)
    {
        retrofit.editUserInfo(newData).enqueue(object : Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    val token = response.body()?.message.toString()
                    sessionManager.deleteAuthToken()
                    sessionManager.saveAuthToken(token)

                    retrofit.authentication().enqueue(object: Callback<DefaultResponse>
                    {
                        override fun onResponse(
                            call: Call<DefaultResponse>,
                            response: Response<DefaultResponse>
                        ) {
                            if(response.body()?.error.toString() == "false") {
                                var usernameRes = response.body()?.message.toString()
                                sessionManager.deleteUsername()
                                sessionManager.saveUsername(usernameRes)
                            }
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                            Toast.makeText(this@ActivityEditProfile, t.toString(), Toast.LENGTH_SHORT).show()
                        }
                    })

                    Toast.makeText(this@ActivityEditProfile, "Data successfully updated", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ActivityEditProfile, MainActivity::class.java)
                    intent.putExtra("backToProfile", "returnToProfile");
                    startActivity(intent)
                    finish()
                }
                else if(response.body()?.error.toString() == "true" && response.body()?.message.toString() != "Error")
                {
                    editUsername.error = "Username already in use"
                    editUsername.requestFocus()
                }
                else
                {
                    Toast.makeText(this@ActivityEditProfile, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.d("edit profile failed", "")
            }

        })
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

                    file = bitmapToFile(pickedBitMap!!, "slika.jpeg")
                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedPhoto)
                    editProfilePicture.setImageBitmap(pickedBitMap)

                    file = bitmapToFile(pickedBitMap!!, "slika.jpeg")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        //create a file to write bitmap data
        var file: File? = null
        return try {
            val cacheDir = getCacheDir()
            file = File(cacheDir.toString() + File.separator + fileNameToSave)
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // null
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

                        val name = findViewById<TextView>(R.id.editNameProfile)
                        val username = findViewById<TextView>(R.id.editUsernameProfile)
                        val description = findViewById<TextView>(R.id.editDescriptionProfile)
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