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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sismov.Clases.ActiveUser
import com.example.sismov.Clases.Usuario
import com.google.gson.Gson
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    var thisUser = Usuario()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var btnEntrar = findViewById<Button>(R.id.btnIniciarSesion);
        var btnToRegister = findViewById<Button>(R.id.btnIrARegistro);
        var txtCorreo = findViewById<EditText>(R.id.etLoginEmail);
        var txtPassword = findViewById<EditText>(R.id.etLoginPassword);
        var pbLogin = findViewById<ProgressBar>(R.id.progressBarLogin);


        val sp = getSharedPreferences("ActiveUser", Context.MODE_PRIVATE)
        var id = sp.getInt("id",-1)
        if(id != -1) {
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
                            var completed = false
                            val result = putData.result
                            pbLogin.visibility = View.GONE;
                            Log.i("PutData", result)
                            try {
                                var gson = Gson()
                                thisUser = gson.fromJson(result, Usuario::class.java)
                                completed = true
                            } catch (e: Exception) {
                                completed = false
                            }

                            if(!completed) {
                                Toast.makeText(this, "Error iniciando sesión. Compureba tus credenciales", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                                setActiveUser()

                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
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

    private fun setActiveUser() {

        ActiveUser.getInstance().id = thisUser.id
        ActiveUser.getInstance().active = thisUser.active
        ActiveUser.getInstance().creation_date = thisUser.creation_date
        ActiveUser.getInstance().email = thisUser.email
        ActiveUser.getInstance().imagen = thisUser.imagen
        ActiveUser.getInstance().name = thisUser.name
        ActiveUser.getInstance().second_name = thisUser.second_name
        ActiveUser.getInstance().password = thisUser.password
        ActiveUser.getInstance().phone = thisUser.phone
        ActiveUser.getInstance().user_type_id = thisUser.User_type_id
        ActiveUser.getInstance().profileOnce = false

        val sp = getSharedPreferences("ActiveUser", Context.MODE_PRIVATE)
        val editor = sp.edit()

        editor.putInt("id", ActiveUser.getInstance().id)
        editor.putInt("user_type_id", ActiveUser.getInstance().user_type_id)
        editor.putString("name", ActiveUser.getInstance().name)
        editor.putString("second_name", ActiveUser.getInstance().second_name)
        editor.putString("email", ActiveUser.getInstance().email)
        editor.putString("password", ActiveUser.getInstance().password)
        editor.putString("imagen", ActiveUser.getInstance().imagen)
        editor.putString("phone", ActiveUser.getInstance().phone)
        editor.putString("creation_date", ActiveUser.getInstance().creation_date)
        editor.putInt("active", ActiveUser.getInstance().active)
        editor.putBoolean("profileOnce", ActiveUser.getInstance().profileOnce)
        editor.commit()
    }
}