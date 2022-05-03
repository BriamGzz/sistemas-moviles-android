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

class Register_restaurants_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_restaurants)

        var SaveRestaurant = findViewById<Button>(R.id.btnGuardarCambios);

        var RestaurantName = findViewById<EditText>(R.id.edRestaurantName);
        var RestaurantDescription = findViewById<EditText>(R.id.edRestaurantDescription);
        var Horario = findViewById<EditText>(R.id.edRestaurantSchedule);
        var Horario2 = findViewById<EditText>(R.id.edRestaurantSchedule2);
        var RestaurantPrice = findViewById<EditText>(R.id.edRestaurantPrice);

        var pbNewRestaurant = findViewById<ProgressBar>(R.id.progressBarProfile);

        SaveRestaurant.setOnClickListener(){

            if(RestaurantName.text.toString() != "" && RestaurantDescription.text.toString() != "" && Horario.text.toString() != "" && Horario2.text.toString() != "" &&
                RestaurantPrice.text.toString() != ""){


                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(5)
                    field[0] = "RestaurantName"
                    field[1] = "RestaurantDescription"
                    field[2] = "Horario"
                    field[3] = "Horario2"
                    field[4] = "RestaurantPrice"
                    //Creating array for data
                    val data = arrayOfNulls<String>(5)
                    data[0] = RestaurantName.text.toString()
                    data[1] = RestaurantDescription.text.toString()
                    data[2] = Horario.text.toString()
                    data[3] = Horario2.text.toString()
                    data[4] = RestaurantPrice.text.toString();

                    val putData = PutData(
                        "http://192.168.1.64/php/NewRestaurant.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbNewRestaurant.visibility = View.GONE;
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


            } else if(RestaurantName.text.toString() == "" && RestaurantDescription.text.toString() == "" && Horario.text.toString() == "" && Horario2.text.toString() == "" &&
                RestaurantPrice.text.toString() == ""){
                Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
            }


        }

    }
}