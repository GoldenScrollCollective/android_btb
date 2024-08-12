package org.lemonadestand.btb.features.login.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import org.json.JSONException
import org.json.JSONObject
import org.lemonadestand.btb.R
import org.lemonadestand.btb.utils.Utils
import org.lemonadestand.btb.databinding.ActivityLoginBinding
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.extenstions.hide
import org.lemonadestand.btb.extenstions.invisible
import org.lemonadestand.btb.features.login.models.LoginResponse
import org.lemonadestand.btb.network.RestClient
import org.lemonadestand.btb.extenstions.show
import org.lemonadestand.btb.singleton.Singleton.authToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    private var check = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        setFullScreenBg()

        /// for testing
        //mBinding.edtEmail.setText(getString(R.string.test_user))
        //mBinding.edtPassword.setText(getString(R.string.test_password))


        mBinding.tvForgot.setOnClickListener { forgotClick() }

        mBinding.btnLogin.setOnClickListener {

            if (check) {
                login()
            } else {
                forget()
            }
        }

        mBinding.txtRegister.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://buildthenbless.com/registration")
            )
            startActivity(browserIntent)
        }
    }

    private fun setFullScreenBg() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun login() {
        val email: String = mBinding.edtEmail.text.toString()
        val password: String = mBinding.edtPassword.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
            return
        }
        try {

            showLoading(true)
            val json = JSONObject()
            json.put("email", email)
            json.put("password", password)
            json.put("token_name", "android")
            val jsonObject = JsonParser().parse(json.toString()).asJsonObject

            Log.e("Request_data=>", jsonObject.toString())

            val call = RestClient.post().login(jsonObject)
            call!!.enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {

                        val datas = response.body()

                        if (datas!!.status && datas!!.user != null)
                        {
                            Log.i("Response_Datas_token=>", datas!!.user.token.rawToken.toString())
                            Log.i("Response_Datas_uniqId=>", datas!!.user.uniqId.toString())
                            Log.i("Response_Datas_orgId=>", datas!!.user.orgId.toString())
                            Log.i("Response_Datas_userName=>", datas!!.user.name.toString())

                            Toast.makeText(this@LoginActivity, datas!!.message, Toast.LENGTH_SHORT).show()
                            Utils.saveData(this@LoginActivity, Utils.TOKEN, datas.user.token.rawToken)
                            Utils.saveData(this@LoginActivity, Utils.UID, datas.user.uniqId.toString())
                            Utils.saveData(this@LoginActivity, Utils.ORG_ID, datas.user.orgId.toString())
                            Utils.saveData(this@LoginActivity, Utils.USER_NAME, datas.user.name.toString())
                            Utils.saveData(this@LoginActivity, Utils.PICTURE, datas.user.picture)
                            Utils.saveData(this@LoginActivity, Utils.ORG_PICTURE, datas.user.organization.picture)
                            Utils.saveUser(this@LoginActivity,datas.user)
                            authToken = "Bearer " + datas.user.token.rawToken
                            if (datas.status) {
                                val i = Intent(this@LoginActivity, DashboardActivity::class.java)
                                startActivity(i)
                                finish()
                            }
                        }
                        else{
                            Toast.makeText(
                                this@LoginActivity,
                                datas.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Something Went Wrong...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@LoginActivity, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (e: JSONException) {
            Log.e("Error Message=>", "Can not find user")
            showLoading(false)
            throw RuntimeException(e)
        }
    }

    private fun forget() {

        val email: String = mBinding.edtEmail.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            showLoading(true)
            val json = JSONObject()
            json.put("email", email)
            val jsonObject = JsonParser().parse(json.toString()).asJsonObject
            val call = RestClient.post().forget(jsonObject)
            call!!.enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val datas = response.body()
                        Toast.makeText(this@LoginActivity, datas!!.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Something Went Wrong...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@LoginActivity, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (e: JSONException) {
            showLoading(false)
            throw RuntimeException(e)
        }
    }

    private fun forgotClick() {
        if (check) {
            check = false
            mBinding.edtPassword.isEnabled = false
            mBinding.edtPassword.setBackgroundColor(Color.parseColor("#FAF3EC"))
            mBinding.btnReset.text = getString(R.string.reset_password)
        } else {
            check = true
            mBinding.edtPassword.isEnabled = true
            mBinding.edtPassword.setBackgroundColor(Color.parseColor("#FDFCFB"))
            mBinding.btnReset.text = getString(R.string.log_in)
        }
    }

    fun showLoading(isShow :Boolean)
    {
        if (isShow)
        {
            mBinding.loadingWavy.show()
            mBinding.btnReset.invisible()
        }
        else
        {
            mBinding.btnReset.show()
            mBinding.loadingWavy.hide()
        }
    }
}