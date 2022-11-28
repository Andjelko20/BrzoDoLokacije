package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.Validation
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.activity_register.*

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val validation = Validation()

        val cancelPasswordButton = findViewById<Button>(R.id.cancelPasswordButton)
        cancelPasswordButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val buttonBackResetPassword = findViewById<Button>(R.id.backButtonResetPassword)
        buttonBackResetPassword.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val saveChangesPasswordButton = findViewById<Button>(R.id.saveChangesPasswordButton)
        saveChangesPasswordButton.setOnClickListener{

            val retrofit = Client(this).buildService(Api::class.java)

            val currentPasswordPolje = findViewById<EditText>(R.id.currentPassword)
            val newPasswordPolje = findViewById<EditText>(R.id.newPassword)
            val confirmNewPasswordPolje = findViewById<EditText>(R.id.confirmNewPassword)

            val currentPassword = currentPasswordPolje.text.toString().trim()
            val newPassword = newPasswordPolje.text.toString().trim()
            val confirmNewPassword = confirmNewPasswordPolje.text.toString().trim()

            //current password check
            if(currentPassword.isEmpty()){
                currentPasswordPolje.error = "Please enter your current password"
                currentPasswordPolje.requestFocus()
                return@setOnClickListener
            }

            //new password check
            if(newPassword.isEmpty()){
                newPasswordPolje.error = "Password required"
                newPasswordPolje.requestFocus()
                return@setOnClickListener
            }
            if(!validation.checkPassword(newPassword)){
                newPasswordPolje.error = "Password must contain minimum 8 characters, at least one uppercase letter, one lowercase letter and one number"
                newPasswordPolje.requestFocus()
                return@setOnClickListener
            }

            //confirm new password check
            if(confirmNewPassword.isEmpty()){
                confirmNewPasswordPolje.error = "Please enter your new password again"
                confirmNewPasswordPolje.requestFocus()
                return@setOnClickListener
            }

            //check if passwords match
            if(confirmNewPassword != newPassword){
                confirmNewPasswordPolje.error = "Passwords don't match"
                confirmNewPasswordPolje.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this@ChangePasswordActivity, "Uspesno", Toast.LENGTH_SHORT).show()
        }
    }
}