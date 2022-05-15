package com.example.sismov

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.drawToBitmap
import com.example.sismov.Clases.ActiveUser
import com.example.sismov.Clases.DatePickerFragment
import com.example.sismov.Clases.TimePickerFragment
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.io.ByteArrayOutputStream

class NewRestaurantFragment : Fragment() {

    private lateinit var SaveRestaurant :Button

    private lateinit var RestaurantName :EditText
    private lateinit var RestaurantDescription :EditText
    private lateinit var Horario :EditText
    private lateinit var Horario2 :EditText
    private lateinit var RestaurantPrice :EditText
    private lateinit var RestaurantCategory :Spinner
    private lateinit var RegRestaurantImage :ImageView

    private lateinit var pbNewRestaurant :ProgressBar

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

        SaveRestaurant = view?.findViewById(R.id.btnGuardarCambios)!!

        RestaurantName = view?.findViewById(R.id.edRestaurantName)!!
        RestaurantDescription = view?.findViewById(R.id.edRestaurantDescription)!!
        Horario = view?.findViewById(R.id.edRestaurantSchedule)!!
        Horario2 = view?.findViewById(R.id.edRestaurantSchedule2)!!
        RestaurantPrice = view?.findViewById(R.id.edRestaurantPrice)!!
        RestaurantCategory = view?.findViewById(R.id.cbCategories)!!
        RegRestaurantImage = view?.findViewById(R.id.ivRegRestaurantImage)!!

        pbNewRestaurant = view?.findViewById(R.id.pbNewRestaurant)!!

        Horario.setOnClickListener {
            //showDatePickerDialog(Horario)
            popTimePicker(Horario)
        }

        Horario2.setOnClickListener {
            //showDatePickerDialog(Horario2)
            popTimePicker(Horario2)
        }

        RegRestaurantImage.setOnClickListener {
            pickImageFromGallery()
        }

        SaveRestaurant.setOnClickListener(){

            if(RestaurantName.text.toString() != "" && RestaurantDescription.text.toString() != "" && Horario.text.toString() != "" && Horario2.text.toString() != "" &&
                RestaurantPrice.text.toString() != ""){

                val strBlob = BitMapToString(RegRestaurantImage.drawToBitmap())
                pbNewRestaurant.visibility = View.VISIBLE;
                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(8)
                    field[0] = "name"
                    field[1] = "desc"
                    field[2] = "inicio"
                    field[3] = "cierre"
                    field[4] = "precio"
                    field[5] = "categoria"
                    field[6] = "usuario"
                    field[7] = "image"

                    //Creating array for data
                    val data = arrayOfNulls<String>(8)

                    data[0] = RestaurantName.text.toString()
                    data[1] = RestaurantDescription.text.toString()
                    data[2] = Horario.text.toString()
                    data[3] = Horario2.text.toString()
                    data[4] = RestaurantPrice.text.toString();
                    data[5] = RestaurantCategory.selectedItem.toString();
                    data[6] = ActiveUser.getInstance().id.toString();
                    data[7] = strBlob

                    val putData = PutData(
                        //"http://192.168.1.64/php/NewRestaurant.php",
                        //"http://192.168.100.9/php/sistemas-moviles-php/newRestaurant.php",
                        "https://proyectodepsm.000webhostapp.com/newRestaurant.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbNewRestaurant.visibility = View.GONE;
                            Log.i("PutData", result)
                            if(result.equals("Restaurante Creado")) {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();

                                RestaurantName.setText("")
                                RestaurantDescription.setText("")
                                Horario.setText("")
                                Horario2.setText("")
                                RestaurantPrice.setText("")

                                val fragmentManager = activity?.supportFragmentManager
                                val fragmentTransaction = fragmentManager?.beginTransaction()
                                fragmentTransaction?.replace(R.id.fragmentContainerHome, HomeFragment())
                                fragmentTransaction?.commit()

                            } else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    //End Write and Read data with URL
                })


            } else if(RestaurantName.text.toString() == "" && RestaurantDescription.text.toString() == "" && Horario.text.toString() == "" && Horario2.text.toString() == "" &&
                RestaurantPrice.text.toString() == ""){
                Toast.makeText(context, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
            }


        }



        super.onResume()
    }

    private fun popTimePicker(editText: EditText) {
        val timePicker = TimePickerFragment { onTimeSelected(it, editText) }

        activity?.supportFragmentManager?.let { timePicker.show(it, "time") }
    }

    private fun onTimeSelected(time:String, editText: EditText) {
        editText.setText("$time")
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null){
            val uri = data.data
            var bmp = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, uri)
            RegRestaurantImage.setImageBitmap(bmp)
        }
    }

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}