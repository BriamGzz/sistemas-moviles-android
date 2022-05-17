package com.example.sismov

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
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
import androidx.lifecycle.Lifecycle
import com.example.sismov.Clases.*
import com.google.gson.Gson
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RestaurantFragment : Fragment() {

    private lateinit var thisRestaurant : Restaurante
    private lateinit var thisRating : Calificacion
    private lateinit var tvName : TextView
    private lateinit var sbRating : SeekBar
    private lateinit var tvCategory : TextView
    private lateinit var tvApertura : TextView
    private lateinit var tvCierre : TextView
    private lateinit var etDetails : EditText
    private lateinit var etFecha : EditText
    private lateinit var etHora : EditText
    private lateinit var etPersonas : EditText
    private lateinit var etPrecio : EditText
    private lateinit var btnDate : Button
    private lateinit var ivDetailsImage : ImageView
    private lateinit var pbDetails : ProgressBar
    private lateinit var pbDate : ProgressBar
    private lateinit var tvNumberCalif : TextView

    private lateinit var linlay : LinearLayout

    var txtChanged = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    override fun onResume() {

        tvName = view?.findViewById(R.id.tvDetailsName)!!
        tvNumberCalif = view?.findViewById(R.id.tvNumberCalif)!!
        sbRating = view?.findViewById(R.id.sbRating)!!
        tvCategory = view?.findViewById(R.id.tvDetailsCategory)!!
        tvApertura = view?.findViewById(R.id.tvDetailsApertura)!!
        tvCierre = view?.findViewById(R.id.tvDetailsCierre)!!
        etDetails = view?.findViewById(R.id.etDetailsDesc)!!
        etFecha = view?.findViewById(R.id.etDetailsFechaCita)!!
        etHora = view?.findViewById(R.id.etDetailsHour)!!
        etPersonas = view?.findViewById(R.id.etDetailsPersonas)!!
        etPrecio = view?.findViewById(R.id.etDetailsPrecioCita)!!
        btnDate = view?.findViewById(R.id.btnDoDate)!!
        ivDetailsImage = view?.findViewById(R.id.ivDetailsImage)!!

        linlay = view?.findViewById(R.id.linlayCita)!!

        pbDetails = view?.findViewById(R.id.pbDetails)!!
        pbDate = view?.findViewById(R.id.pbDate)!!

        getRestaurantInfo()
        getRatingInfo()

        btnDate.setOnClickListener {
            registerDate()
        }

        etPersonas.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(etPersonas.text.isNotBlank()) {
                    if (etPersonas.text.last() == '0' || etPersonas.text.last() == '1' || etPersonas.text.last() == '2' || etPersonas.text.last() == '3' || etPersonas.text.last() == '4'
                        || etPersonas.text.last() == '5' || etPersonas.text.last() == '6' || etPersonas.text.last() == '7' || etPersonas.text.last() == '8' || etPersonas.text.last() == '9'
                    ) {
                    } else {
                        if (etPersonas.text.isNotEmpty()) etPersonas.setText(
                            etPersonas.text.dropLast(1).toString()
                        )
                    }
                }

                if (etPersonas.text.length == 1) {
                    if(etPersonas.text.last() == '0') etPersonas.setText("1")
                }
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

        sbRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tvNumberCalif.text = sbRating.progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if(ActiveUser.getInstance().user_type_id == 0) {
                    updateMyRating()
                } else {
                    Toast.makeText(context, "Necesitas una cuenta de Usuario para calificar", Toast.LENGTH_LONG).show()
                }
            }

        })

        super.onResume()
    }

    private fun getEreaserDates() {
        val sp = activity?.getSharedPreferences("EreaserDate", Context.MODE_PRIVATE)

        val restaurant_id = sp?.getString("restaurant_id", "")
        val fecha = sp?.getString("fecha", "")
        val hora = sp?.getString("hora", "")
        val personas = sp?.getString("personas", "")

        if (restaurant_id != null) {
            if(restaurant_id.equals(thisRestaurant.restaurant_id.toString())) {
                if (fecha!=null) {
                    etFecha.setText(fecha)
                }

                if (hora!=null) {
                    etHora.setText(hora)
                }

                if (personas!=null) {
                    etPersonas.setText(personas)
                }
            }
        }
    }

    private fun getRatingInfo() {
        val handler = Handler(Looper.getMainLooper())
        pbDetails.visibility = View.VISIBLE;

        handler.post(Runnable {
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(3)
            field[0] = "restaurant_id"
            field[1] = "usuario_id"
            field[2] = "calificaion"

            //Creating array for data
            val data = arrayOfNulls<String>(3)
            data[0] = thisRestaurant.restaurant_id.toString()
            data[1] = ActiveUser.getInstance().id.toString()
            data[2] = sbRating.progress.toString()

            val putData = PutData(
                "https://proyectodepsm.000webhostapp.com/getMyRating.php",
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
                        thisRating = try {
                            gson.fromJson(result, Calificacion::class.java)
                        }catch (e:Exception) {
                            Calificacion();
                        }
                        setMyRating()
                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    }
                }
            }
            //End Write and Read data with URL
        })
    }

    private fun setMyRating() {
        if(thisRating != Calificacion()) {
            sbRating.progress = thisRating.calificacion
            tvNumberCalif.text = thisRating.calificacion.toString()
        }
    }

    private fun updateMyRating() {
        pbDetails.visibility = View.VISIBLE

        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(3)
            field[0] = "restaurant_id"
            field[1] = "usuario_id"
            field[2] = "calificacion"

            //Creating array for data
            val data = arrayOfNulls<String>(3)
            data[0] = thisRestaurant.restaurant_id.toString()
            data[1] = ActiveUser.getInstance().id.toString()
            data[2] = sbRating.progress.toString()

            val putData = PutData(
                "https://proyectodepsm.000webhostapp.com/updateMyRating.php",
                "POST",
                field,
                data
            )
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    pbDetails.visibility = View.GONE;
                    Log.i("PutData", result)
                    if(result.equals("Data get")) {
                        getRatingInfo()
                        Toast.makeText(context, "Gracias por tu opinión ♥", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    }
                }
            }
            //End Write and Read data with URL
        })
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
            val restaurante_nombre = thisRestaurant.nombre

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
                pbDate.visibility = View.VISIBLE

                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    val field = arrayOfNulls<String>(7)
                    field[0] = "usuario_id"
                    field[1] = "restaurante_id"
                    field[2] = "fecha"
                    field[3] = "hora"
                    field[4] = "personas"
                    field[5] = "total"
                    field[6] = "restaurante_nombre"

                    //Creating array for data
                    val data = arrayOfNulls<String>(7)
                    data[0] = usuario_id
                    data[1] = restaurante_id
                    data[2] = fecha
                    data[3] = hora
                    data[4] = personas
                    data[5] = total.toString()
                    data[6] = restaurante_nombre

                    val putData = PutData(
                        "https://proyectodepsm.000webhostapp.com/newDate.php",
                        "POST",
                        field,
                        data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            pbDate.visibility = View.GONE;
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

    private fun setRestaurantInfo() {
        if(thisRestaurant.active != 0) {
            tvName.setText(thisRestaurant.categoria)

            tvCategory.setText(thisRestaurant.categoria)
            tvApertura.setText(thisRestaurant.fecha_apertura)
            tvCierre.setText(thisRestaurant.fecha_cierre)
            etDetails.setText(thisRestaurant.descripcion)

            if (thisRestaurant.image != null) {
                val byte = Base64.decode(thisRestaurant.image, 0)
                val bmp = BitmapFactory.decodeByteArray(byte, 0, byte.size)
                ivDetailsImage?.setImageBitmap(bmp)
            }

            if (ActiveUser.getInstance().user_type_id != 0) {
                linlay.visibility = View.GONE
            } else {
                linlay.visibility = View.VISIBLE
            }

            getEreaserDates()

        } else {
            val fragment = HomeFragment()
            val fragmentManager = activity?.supportFragmentManager
            val fragmentTransaction = fragmentManager?.beginTransaction()

            fragmentTransaction?.replace(R.id.fragmentContainerHome, fragment)
            fragmentTransaction?.commit()
        }
    }

    private fun getRestaurantInfo() {
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
                    var completed = false
                    val gson = Gson()
                    try {
                        thisRestaurant = gson.fromJson(result, Restaurante::class.java)
                        completed = true
                    } catch (e: java.lang.Exception) {
                        thisRestaurant = Restaurante()
                        completed = false
                    }

                    if (!completed) {
                        Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                    }

                    setRestaurantInfo()

                }
            }
            //End Write and Read data with URL
        })

    }

    override fun onDestroy() {
        val restaurant_id = thisRestaurant.restaurant_id.toString()
        val fecha = etFecha.text.toString()
        val hora = etHora.text.toString()
        val personas = etPersonas.text.toString()

        val sp = activity?.getSharedPreferences("EreaserDate", Context.MODE_PRIVATE)
        val editor = sp?.edit()

        if(etFecha.text.toString() != "" || etHora.text.toString() != "" || etPersonas.text.toString() != "") {

            var builder = AlertDialog.Builder(activity)

            builder.setTitle("Cita no registrada")
            builder.setMessage("Tiene datos sin registrar ¿Qué desea hacer?")
            builder.setPositiveButton("Guardar como borrador", DialogInterface.OnClickListener{ dialog, id ->

                editor?.putString("restaurant_id", restaurant_id)
                editor?.putString("fecha", fecha)
                editor?.putString("hora", hora)
                editor?.putString("personas", personas)
                editor?.commit()

                dialog.cancel()
            })
            builder.setNegativeButton("Descartar cambios", DialogInterface.OnClickListener{ dialog, id ->

                editor?.putString("restaurant_id", restaurant_id)
                editor?.putString("fecha", "")
                editor?.putString("hora", "")
                editor?.putString("personas", "")
                editor?.commit()

                dialog.cancel()
            })

            var alert = builder.create()
            alert.show()
        }

        super.onDestroy()
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