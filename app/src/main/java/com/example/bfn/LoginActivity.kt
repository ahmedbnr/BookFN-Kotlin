package com.example.bfn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.view.isVisible
import com.example.bfn.models.LoginResponse
import com.example.bfn.models.User
import com.example.bfn.prefs.PrefsManager
import com.example.bfn.util.ApiClient
import com.example.bfn.util.UserSession
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //login
        val loginBtn = findViewById<Button>(R.id.login_login_btn)
        val emailTXT = findViewById<EditText>(R.id.email_text)
        val pwdTXT = findViewById<EditText>(R.id.pass_text)
        val login_signup_btn = findViewById<Button>(R.id.login_signup_btn)
        val forgot_pwd_txt = findViewById<TextView>(R.id.forgot_pwd)
        forgot_pwd_txt.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPwdActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {

            if (emailTXT.text.isNullOrBlank()) {
                emailTXT.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }

            if (pwdTXT.text.isNullOrBlank()) {
                pwdTXT.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }

            ApiClient.apiService.login(
                User(
                    email = emailTXT.text.toString(),
                    pwd = pwdTXT.text.toString()
                )
            ).enqueue(
                object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>?,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val content = response.body()

                            if (response.code() == 200) {
                                val token = response.body().message._id
                                PrefsManager.seToken(this@LoginActivity, token = token)

                                val sharedPref = getSharedPreferences(
                                    getString(R.string.user), Context.MODE_PRIVATE)

                                with (sharedPref.edit()) {
                                    putString(getString(R.string.token), content.message.token)
                                    commit()
                                }

                                UserSession.id =
                                    content.message._id


                                UserSession.firstName =
                                    content.message.firstName

                                UserSession.lastName =
                                    content.message.lastName

                                UserSession.email =
                                    content.message.email

                                val profilePicture =  content.message.img

                                if(!profilePicture.isEmpty())
                                    UserSession.img = profilePicture

                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                            if (response.code() == 201) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Error Occurred: ${response.message()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            if (response.code() == 403) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Error Occurred: ${response.message()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }


                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "Error Occurred: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                        Toast.makeText(this@LoginActivity, "Network Failure", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            )


        }

        login_signup_btn.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}