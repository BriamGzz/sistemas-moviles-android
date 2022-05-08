package com.example.sismov

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
                                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
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