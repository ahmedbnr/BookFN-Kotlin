package com.example.bfn

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.bfn.models.User
import com.example.bfn.util.ApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        //Sign UP
        val firstName = findViewById<EditText>(R.id.firstName_text)
        val lastName = findViewById<EditText>(R.id.lastName_text)
        val email = findViewById<EditText>(R.id.register_email_text)
        val password = findViewById<EditText>(R.id.register_pass_text)
        val confirmPwd = findViewById<EditText>(R.id.confirm_pwd)
        val signUpBTN = findViewById<Button>(R.id.signup_btn)
        val forgot_pwd_txt = findViewById<TextView>(R.id.forgotpass_signuptxt)
        forgot_pwd_txt.setOnClickListener{
            val intent = Intent(this@RegisterActivity, ForgotPwdActivity::class.java)
            startActivity(intent)
        }

        signUpBTN.setOnClickListener {

            if (firstName.text.isNullOrBlank()) {
                firstName.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }

            if (lastName.text.isNullOrBlank()) {
                lastName.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if (email.text.isNullOrBlank()) {
                email.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if (password.text.isNullOrBlank()) {
                password.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if (confirmPwd.text.isNullOrBlank()) {
                confirmPwd.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if (password.text.toString() != confirmPwd.text.toString()) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Error Occurred: Password and confirm password fields need to be identical",
                    Toast.LENGTH_LONG
                ).show()
            }
            ApiClient.apiService.register(User(id="", firstName = firstName.text.toString(), lastName = lastName.text.toString(), email = email.text.toString(), pwd = password.text.toString())).enqueue(
                object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        t.printStackTrace()

                    }
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val content = response.body()

                            if(response.code() == 200)
                            {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Sign up completed!",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        else if(response.code() == 403)
                        {
                            val text = "Error Occurred: Email already associated within an existing account !"
                            Toast.makeText(
                                this@RegisterActivity,text,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                         else{

                            Toast.makeText(
                                this@RegisterActivity,
                                "Error Occurred: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            )

        }
    }
}