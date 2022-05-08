package com.example.sismov

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.vishnusivadas.advanced_httpurlconnection.PutData

import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var btnRegistar = findViewById<Button>(R.id.btnConfirmarRegistro);
        var btnToLogin = findViewById<Button>(R.id.btnIrALogin);
        var txtNombre = findViewById<EditText>(R.id.etNombre);
        var txtApellidos = findViewById<EditText>(R.id.etApellidos);
        var txtCorreo = findViewById<EditText>(R.id.etCorreo);
        var txtPassword = findViewById<EditText>(R.id.etPassword);
        var txtConfirmPassword = findViewById<EditText>(R.id.etPasswordConfirm);
        var pbRegister = findViewById<ProgressBar>(R.id.progressBarRegister);

        //Codigo para el tipo de usuario
        var rbType = findViewById<RadioButton>(R.id.rbClient);
        //

        btnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegistar.setOnClickListener {

            if(txtNombre.text.toString() != "" && txtApellidos.text.toString() != "" && txtCorreo.text.toString() != "" &&
                txtPassword.text.toString() != "" && (txtPassword.text.toString() == txtConfirmPassword.text.toString()) ) {
                pbRegister.visibility = View.VISIBLE;

                //Codigo para el tipo de usuario
                var isRestaurant = rbType.isChecked.not();
                var clientType: Int;
                
                if(isRestaurant) {
                    clientType = 1;
                } else {
                    clientType = 0;
                }
                //

                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(5)
                    field[0] = "name"
                    field[1] = "secondname"
                    field[2] = "email"
                    field[3] = "password"
                    field[4] = "usertype"
                    //Creating array for data
                    val data = arrayOfNulls<String>(5)
                    data[0] = txtNombre.text.toString()
                    data[1] = txtApellidos.text.toString()
                    data[2] = txtCorreo.text.toString()
                    data[3] = txtPassword.text.toString()
                    data[4] = clientType.toString();

                    val putData = PutData(
                        //"http://192.168.1.64/php/signup.php",
                        "http://192.168.100.9/php/sistemas-moviles-php/signup.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbRegister.visibility = View.GONE;
                            Log.i("PutData", result)
                            if(result.equals("Sign Up Success")) {
                                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                                val intent = Intent(this, LoginActivity::class.java)
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
                if(txtNombre.text.toString() == "" || txtApellidos.text.toString() == "" || txtCorreo.text.toString() == "" ||
                    txtPassword.text.toString() == "") {
                    Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
                }

                if(txtPassword.text.toString() != txtConfirmPassword.text.toString()) {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}