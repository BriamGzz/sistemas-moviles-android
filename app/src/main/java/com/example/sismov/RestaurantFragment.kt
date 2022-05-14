package com.example.sismov

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.example.sismov.Clases.DatePickerFragment
import com.example.sismov.Clases.Restaurante
import com.example.sismov.Clases.TimePickerFragment
import com.google.gson.Gson
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RestaurantFragment : Fragment() {

    private lateinit var thisRestaurant : Restaurante
    private lateinit var tvName : TextView
    private lateinit var ivDetailsRating : RatingBar
    private lateinit var tvCategory : TextView
    private lateinit var tvApertura : TextView
    private lateinit var tvCierre : TextView
    private lateinit var etDetails : EditText
    private lateinit var etFecha : EditText
    private lateinit var etHora : EditText
    private lateinit var etPersonas : EditText
    private lateinit var etPrecio : EditText
    private lateinit var btnDate : Button
    private lateinit var btnRate : Button
    private lateinit var ivDetailsImage : ImageView
    private lateinit var pbDetails : ProgressBar

    private lateinit var linlay : LinearLayout



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    override fun onResume() {

        tvName = view?.findViewById(R.id.tvDetailsName)!!
        ivDetailsRating = view?.findViewById(R.id.ivDetailsRatingBar)!!
        tvCategory = view?.findViewById(R.id.tvDetailsCategory)!!
        tvApertura = view?.findViewById(R.id.tvDetailsApertura)!!
        tvCierre = view?.findViewById(R.id.tvDetailsCierre)!!
        etDetails = view?.findViewById(R.id.etDetailsDesc)!!
        etFecha = view?.findViewById(R.id.etDetailsFechaCita)!!
        etHora = view?.findViewById(R.id.etDetailsHour)!!
        etPersonas = view?.findViewById(R.id.etDetailsPersonas)!!
        etPrecio = view?.findViewById(R.id.etDetailsPrecioCita)!!
        btnDate = view?.findViewById(R.id.btnDoDate)!!
        btnRate = view?.findViewById(R.id.btnRate)!!
        ivDetailsImage = view?.findViewById(R.id.ivDetailsImage)!!

        linlay = view?.findViewById(R.id.linlayCita)!!

        pbDetails = view?.findViewById(R.id.pbDetails)!!
        val bundle = this.arguments
        val id = bundle?.get("restaurant_id").toString()

        pbDetails.visibility = View.VISIBLE;
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(2)
            field[0] = "table"
            field[1] = "id"

            //Creating array for data
            val data = arrayOfNulls<String>(2)
            data[0] = "restaurantes"
            data[1] = id

            val putData = PutData(
                //"http://192.168.1.64/php/signup.php",
                //"http://192.168.100.9/php/sistemas-moviles-php/login.php"
                "https://proyectodepsm.000webhostapp.com/getRestaurantById.php",
                "POST",
                field,
                data
            )
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    pbDetails.visibility = View.GONE;
                    Log.i("PutData", result)
                    if(!result.equals("Error: Database connection")) {
                        val gson = Gson()
                        thisRestaurant = gson.fromJson(result, Restaurante::class.java)
                        getRestaurantInfo()

                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    }
                }
            }
            //End Write and Read data with URL
        })

        btnDate.setOnClickListener {
            registerDate()
        }

        etPersonas.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(etPersonas.text.toString() != "") {
                    val total = Integer.parseInt(etPersonas.text.toString()) * thisRestaurant.precio
                    etPrecio.setText(total.toString())
                } else {
                    etPrecio.setText("")
                }
            }

        })

        etHora.setOnClickListener {
            popTimePicker(etHora)
        }

        etFecha.setOnClickListener {
            showDatePickerDialog(etFecha)
        }

        super.onResume()
    }

    private fun registerDate() {

        if (etFecha.text.toString() != "" && etHora.text.toString() != "" &&
            etPersonas.text.toString() != "" && etPrecio.text.toString() != "") {
            var allInfoCorrect = true

            val usuario_id = ActiveUser.getInstance().id.toString()
            val restaurante_id = thisRestaurant.restaurant_id.toString()
            val fecha = etFecha.text.toString()
            val hora = etHora.text.toString()
            val personas = etPersonas.text.toString()
            val total = Integer.parseInt(personas) * thisRestaurant.precio

            //region Validacion Hora
            val strApertura = "2000-01-03 ${thisRestaurant.fecha_apertura}"
            val strCierre = "2000-01-03 ${thisRestaurant.fecha_cierre}"
            val strHoraSeleccionada = "2000-01-03 $hora"

            val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            var HoraDeapertura = LocalDateTime.parse(strApertura,formatterHour)
            var HoraDecierre = LocalDateTime.parse(strCierre,formatterHour)
            var horaAComparar = LocalDateTime.parse(strHoraSeleccionada,formatterHour)

            when {
                horaAComparar < HoraDeapertura -> {
                    Toast.makeText(
                        context,
                        "Selecciona una hora después de la apertura",
                        Toast.LENGTH_SHORT
                    ).show()

                    allInfoCorrect = false
                }

                horaAComparar > HoraDecierre -> {
                    Toast.makeText(
                        context,
                        "Selecciona una hora antes del cierre",
                        Toast.LENGTH_SHORT
                    ).show()

                    allInfoCorrect = false
                }
            }

            //endregion

            //region Validacion Fecha

            var today = LocalDate.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            var dateSelected = LocalDate.parse(fecha,formatter)

            when {
                today > dateSelected -> {
                    Toast.makeText(
                        context,
                        "Selecciona una fecha después de hoy",
                        Toast.LENGTH_SHORT
                    ).show()

                    allInfoCorrect = false
                }
            }

            //endregion

            if (allInfoCorrect) {

                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(6)
                    field[0] = "usuario_id"
                    field[1] = "restaurante_id"
                    field[2] = "fecha"
                    field[3] = "hora"
                    field[4] = "personas"
                    field[5] = "total"

                    //Creating array for data
                    val data = arrayOfNulls<String>(6)
                    data[0] = usuario_id
                    data[1] = restaurante_id
                    data[2] = fecha
                    data[3] = hora
                    data[4] = personas
                    data[5] = total.toString()

                    val putData = PutData(
                        "https://proyectodepsm.000webhostapp.com/newDate.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbDetails.visibility = View.GONE;
                            Log.i("PutData", result)
                            if (result.equals("Cita agendada correctamente")) {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                                etFecha.setText("")
                                etHora.setText("")
                                etPersonas.setText("")
                                etPrecio.setText("")

                            } else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    //End Write and Read data with URL
                })

            }
        }
        else {
            Toast.makeText(context, "Toda la información es requerida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRestaurantInfo() {
        tvName.setText(thisRestaurant.categoria)

        ivDetailsRating.rating = thisRestaurant.calificacion

        tvCategory.setText(thisRestaurant.categoria)
        tvApertura.setText(thisRestaurant.fecha_apertura)
        tvCierre.setText(thisRestaurant.fecha_cierre)
        etDetails.setText(thisRestaurant.descripcion)

        if(thisRestaurant.image != null) {
            val byte = Base64.decode(thisRestaurant.image, 0)
            val bmp = BitmapFactory.decodeByteArray(byte, 0, byte.size)
            ivDetailsImage?.setImageBitmap(bmp)
        }

        if(ActiveUser.getInstance().user_type_id == 1) {
            linlay.visibility = View.GONE
            btnDate.visibility = View.GONE
        } else {
            linlay.visibility = View.VISIBLE
            btnRate.visibility = View.VISIBLE
        }
    }


    //region Funciones raras

    private fun popTimePicker(editText: EditText) {
        val timePicker = TimePickerFragment { onTimeSelected(it, editText) }

        activity?.supportFragmentManager?.let { timePicker.show(it, "time") }
    }

    private fun onTimeSelected(time:String, editText: EditText) {
        editText.setText("$time")
    }

    private fun showDatePickerDialog(editText: EditText) {
        var selectedDate = ""
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            var MM = month.toString()
            if(month+1 < 10) {
                var m = month+1
                MM = "0$m"
            }
            selectedDate = "$year-$MM-$day"
            editText.setText(selectedDate)
        })

        activity?.supportFragmentManager?.let { newFragment.show(it, "datePicker") }
    }
    //endregion

}