package com.example.sismov

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sismov.Clases.DatePickerFragment
import com.vishnusivadas.advanced_httpurlconnection.PutData

class NewRestaurantFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_restaurant, container, false)
    }

    private fun showDatePickerDialog(editText: EditText) {
        var selectedDate = ""
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            selectedDate = year.toString() + "-" + (month + 1) + "-" + day.toString()
            editText.setText(selectedDate)
        })

        activity?.supportFragmentManager?.let { newFragment.show(it, "datePicker") }
    }


    override fun onResume() {

        var SaveRestaurant = view?.findViewById<Button>(R.id.btnGuardarCambios);

        var RestaurantName = view?.findViewById<EditText>(R.id.edRestaurantName);
        var RestaurantDescription = view?.findViewById<EditText>(R.id.edRestaurantDescription);
        var Horario = view?.findViewById<EditText>(R.id.edRestaurantSchedule);
        var Horario2 = view?.findViewById<EditText>(R.id.edRestaurantSchedule2);
        var RestaurantPrice = view?.findViewById<EditText>(R.id.edRestaurantPrice);
        var RestaurantCategory = view?.findViewById<Spinner>(R.id.cbCategories);

        var pbNewRestaurant = view?.findViewById<ProgressBar>(R.id.progressBarProfile);

        Horario?.setOnClickListener {
            showDatePickerDialog(Horario)
        }

        Horario2?.setOnClickListener {
            showDatePickerDialog(Horario2)
        }

        SaveRestaurant?.setOnClickListener(){

            if(RestaurantName?.text.toString() != "" && RestaurantDescription?.text.toString() != "" && Horario?.text.toString() != "" && Horario2?.text.toString() != "" &&
                RestaurantPrice?.text.toString() != ""){


                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(7)
                    field[0] = "name"
                    field[1] = "desc"
                    field[2] = "inicio"
                    field[3] = "cierre"
                    field[4] = "precio"
                    field[5] = "categoria"
                    field[6] = "usuario"

                    //Creating array for data
                    val data = arrayOfNulls<String>(7)

                    data[0] = RestaurantName?.text.toString()
                    data[1] = RestaurantDescription?.text.toString()
                    data[2] = Horario?.text.toString()
                    data[3] = Horario2?.text.toString()
                    data[4] = RestaurantPrice?.text.toString();
                    data[5] = RestaurantCategory?.selectedItem.toString();
                    data[6] = ActiveUser.getInstance().id.toString();

                    val putData = PutData(
                        //"http://192.168.1.64/php/NewRestaurant.php",
                        "http://192.168.100.9/php/sistemas-moviles-php/newRestaurant.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbNewRestaurant?.visibility = View.GONE;
                            Log.i("PutData", result)
                            if(result.equals("Restaurante Creado")) {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    //End Write and Read data with URL
                })


            } else if(RestaurantName?.text.toString() == "" && RestaurantDescription?.text.toString() == "" && Horario?.text.toString() == "" && Horario2?.text.toString() == "" &&
                RestaurantPrice?.text.toString() == ""){
                Toast.makeText(context, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
            }


        }

        super.onResume()
    }


}