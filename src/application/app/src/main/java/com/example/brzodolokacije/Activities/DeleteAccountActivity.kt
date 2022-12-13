package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.ChangePasswordDto
import com.example.brzodolokacije.ModelsDto.CheckPasswordDto
import com.example.brzodolokacije.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var builder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val retrofit = Client(this).buildService(Api::class.java)

        val sessionManager = SessionManager(this)

        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))

        builder.setTitle("Accout Deletion")
            .setMessage(R.string.account_deletion)
            .setCancelable(true)
            .setPositiveButton("Confirm"){dialogInterface, it->
                var usernameSm = sessionManager.fetchUsername()
//                Log.d("username", usernameSm.toString())
                if (usernameSm != null) {
                    retrofit.deleteUser(usernameSm).enqueue(object: Callback<DefaultResponse>{
                        override fun onResponse(
                            call: Call<DefaultResponse>,
                            response: Response<DefaultResponse>
                        ) {
                            if(response.body()?.error.toString() == "false")
                            {
//                                val message = response.body()?.message.toString()
//                                Toast.makeText(this@DeleteAccountActivity, message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@DeleteAccountActivity, AccountDeletedActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
//                                Log.d("error", response.body()?.error.toString())
                            }
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                            Log.d("failed", "")
                            Toast.makeText(this@DeleteAccountActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }
            .setNegativeButton("Cancel"){dialogInterface, it->
                dialogInterface.cancel()
            }

        val backButtonDeleteAccount = findViewById<Button>(R.id.backButtonDeleteAccount)
        backButtonDeleteAccount.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val cancelAccountDeletionButton = findViewById<Button>(R.id.cancelAccountDeletionButton)
        cancelAccountDeletionButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val confirmAccountDeletionButton = findViewById<Button>(R.id.confirmAccountDeletionButton)
        confirmAccountDeletionButton.setOnClickListener{
            val userPasswordPolje = findViewById<EditText>(R.id.userPassword)
            val userPassword = userPasswordPolje.text.toString().trim()

            //password check
            if(userPassword.isEmpty()){
                userPasswordPolje.error = "You must enter your password"
                userPasswordPolje.requestFocus()
                return@setOnClickListener
            }

            val password = CheckPasswordDto(userPassword)

            retrofit.checkPassword(password).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
                        builder.show()
                    }
                    else if(response.body()?.error.toString() == "true")
                    {
                        val message = response.body()?.message.toString()
                        Toast.makeText(this@DeleteAccountActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                    Log.d("failed", "")
                    Toast.makeText(this@DeleteAccountActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}