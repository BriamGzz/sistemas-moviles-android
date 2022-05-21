package com.example.sismov

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.sismov.Clases.ActiveUser
import com.vishnusivadas.advanced_httpurlconnection.PutData

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var btnEntrar = findViewById<Button>(R.id.btnIniciarSesion);
        var btnToRegister = findViewById<Button>(R.id.btnIrARegistro);
        var txtCorreo = findViewById<EditText>(R.id.etLoginEmail);
        var txtPassword = findViewById<EditText>(R.id.etLoginPassword);
        var pbLogin = findViewById<ProgressBar>(R.id.progressBarLogin);


        val sp = getSharedPreferences("ActiveUser", Context.MODE_PRIVATE)

        if(sp.getInt("id",-1) != -1) {
            ActiveUser.getInstance().id = sp.getInt("id",-1)
            ActiveUser.getInstance().active = sp.getInt("active",-1)
            ActiveUser.getInstance().creation_date = sp.getString("creation_date","")
            ActiveUser.getInstance().email = sp.getString("email","")
            ActiveUser.getInstance().imagen = sp.getString("imagen","")
            ActiveUser.getInstance().name = sp.getString("name","")
            ActiveUser.getInstance().second_name = sp.getString("second_name","")
            ActiveUser.getInstance().password = sp.getString("password","")
            ActiveUser.getInstance().phone = sp.getString("phone","")
            ActiveUser.getInstance().user_type_id = sp.getInt("user_type_id",-1)
            ActiveUser.getInstance().profileOnce = sp.getBoolean("profileOnce",false)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        /*val fecha = sp.getString("fecha", "")
        val hora = sp.getString("hora", "")
        val personas = sp.getString("personas", "")
        */

        btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnEntrar.setOnClickListener {

            if(txtCorreo.text.toString() != "" && txtPassword.text.toString() != "") {

                pbLogin.visibility = View.VISIBLE;
                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(2)
                    field[0] = "email"
                    field[1] = "password"
                    //Creating array for data
                    val data = arrayOfNulls<String>(2)
                    data[0] = txtCorreo.text.toString()
                    data[1] = txtPassword.text.toString()
                    val putData = PutData(
                        //"http://192.168.1.64/php/signup.php",
                        //"http://192.168.100.9/php/sistemas-moviles-php/login.php"
                        "https://proyectodepsm.000webhostapp.com/login.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbLogin.visibility = View.GONE;
                            Log.i("PutData", result)
                            if(result.equals("Login Success")) {
                                var activeUser =
                                    ActiveUser()
                                activeUser.email = txtCorreo.text.toString();
                                Toast.makeText(this, "Bienvenido", Toast.LENGTH_LONG).show();
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    //End Write and Read data with URL
                })
            } else {
                if(txtCorreo.text.toString() == "" || txtPassword.text.toString() == "") {
                    Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}