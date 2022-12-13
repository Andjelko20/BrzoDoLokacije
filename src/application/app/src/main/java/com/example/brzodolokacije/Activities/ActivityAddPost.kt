package com.example.brzodolokacije.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.NewPostDto
import com.example.brzodolokacije.Posts.SelectedPhoto
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.activity_addpost.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ActivityAddPost : AppCompatActivity() {

    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null
    var file: File? = null
    var fileName: String = "slika.jpeg"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)
        val retrofit = Client(this).buildService(Api::class.java)

        var nesto = intent.getStringExtra("sb").toString()
//        Log.d("nesto",nesto)
        if(nesto != "null") {
            editLocationSection.setText(nesto)
        }
        else{
            editLocationSection.setText("")
        }
        val longitude = intent.getStringExtra("longitude")
        val latitude = intent.getStringExtra("latitude")
        if(longitude != "null" && latitude != "null") {
//            Log.d("stampaj1", longitude.toString())
//            Log.d("stampaj2", latitude.toString())
        }

        if(SelectedPhoto.returnSavedBitmap() != null)
        {
            pickedBitMap = SelectedPhoto.returnSavedBitmap()
            SelectedPhoto.saveBitmap(null)
            previewPic.setImageBitmap(pickedBitMap)
            file = bitmapToFile(pickedBitMap!!, "slika.jpeg")
        }

        editLocationSection.setOnClickListener{
            val intent = Intent(this@ActivityAddPost, ActivityMaps::class.java)
            val banana = pickedBitMap?.let { encodeImage(it) }
//            intent.putExtra("bit",banana)
            if(pickedBitMap != null) SelectedPhoto.saveBitmap(pickedBitMap)
            startActivity(intent)
        }
//        val test = intent.getStringExtra("bitslike");
//        if(test.toString() != "null")
//        {
//            val imageBytes =Base64.decode(test,0);
//            val image=BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size);
//            pickedBitMap = image
//            previewPic.setImageBitmap(pickedBitMap)
//            file = bitmapToFile(pickedBitMap!!, "slika.jpeg")
//        }

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("backPressed", "returnToProfile");
            startActivity(intent)
        }

            btnAddPost.setOnClickListener {
                if(file != null) {
                    val picture = MultipartBody.Part.createFormData(
                        "picture",
                        file!!.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
//                    Log.d("File",picture.toString())
//                var location = editLocationSection.text.toString().trim()


                var caption = editCaptionSection.text.toString().trim()
                var location = intent.getStringExtra("sb").toString()
                    var newPost : NewPostDto? = null;
                    if(nesto == "null"){
                        editLocationSection.error = "Please enter your current location"
                        editLocationSection.requestFocus()
                        return@setOnClickListener
                    }
                    else
                    {
                        newPost = NewPostDto(location, caption,latitude.toString(),longitude.toString())

                retrofit.addNewPost(newPost).enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
//                        Log.d("uploadPosta",response.body()?.message.toString())
                        Toast.makeText(
                            this@ActivityAddPost,
                            "Uploading... Please wait for our Artificial Intelligence services to finish checking your image",
                            Toast.LENGTH_LONG
                        ).show()
                        retrofit.uploadPostPhoto(picture, response.body()?.message.toString())
                            .enqueue(object : Callback<DefaultResponse> {
                                override fun onResponse(
                                    call: Call<DefaultResponse>,
                                    response: Response<DefaultResponse>
                                ) {
//                                    Log.v("Dobio response","yes")
                                    if(response.body()?.error.toString()=="false") {
                                        Toast.makeText(
                                            this@ActivityAddPost,
                                            "Post uploaded",
                                            Toast.LENGTH_SHORT
                                        ).show()
//                                    Log.d("uploadSlike",response.body()?.message.toString())
                                        val fdelete =
                                            File(getCacheDir().toString() + File.separator + fileName)
                                        if (fdelete.exists()) {
                                            if (fdelete.delete()) {
//                                                System.out.println("file deleted")
                                            } else {
//                                                System.out.println("file not deleted")
                                            }
                                        }
                                        val intent = Intent(
                                            this@ActivityAddPost,
                                            MainActivity::class.java
                                        )
                                        intent.putExtra("postAdded", "refresh again");
                                        startActivity(intent)
                                    }
                                    else if(response.body()?.error.toString() == "true")
                                    {
//                                        val message = response.body()?.message.toString()
//                                        Log.v("Usao u failure",message)
                                        Toast.makeText(this@ActivityAddPost,"An error occurred",Toast.LENGTH_LONG).show()
                                    }
                                    else
                                    {
                                        Toast.makeText(this@ActivityAddPost,"An error occurred",Toast.LENGTH_LONG).show()
                                    }
                                }
                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                    Toast.makeText(this@ActivityAddPost, "An error occurred", Toast.LENGTH_SHORT).show()
//                                    Log.d("Greska",t.message.toString())
                                }

                            })
//                        val intent = Intent(this@ActivityAddPost, MainActivity::class.java)
//                        intent.putExtra("postAdded", "refresh again");
//                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(this@ActivityAddPost, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })}
            }
        }
    }
    fun pickPhotoFromGallery(view: View){
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
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                    previewPic.setImageBitmap(pickedBitMap)
                    file = bitmapToFile(pickedBitMap!!, fileName)
                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedPhoto)
                    previewPic.setImageBitmap(pickedBitMap)
                    file = bitmapToFile(pickedBitMap!!, fileName)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            val cacheDir = getCacheDir()
            file = File(cacheDir.toString() + File.separator + fileNameToSave)
            file.createNewFile()

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos)
            val bitmapdata = bos.toByteArray()

            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file
        }
    }
    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("backPressed", "returnToProfile");
        startActivity(intent)
    }
}