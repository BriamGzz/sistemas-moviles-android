package com.example.sismov

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sismov.Adapters.CreatedRestaurantsAdapter
import com.example.sismov.Adapters.HistorialAdapter
import com.example.sismov.Clases.ActiveUser
import com.example.sismov.Clases.Cita
import com.example.sismov.Clases.Restaurante
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.lang.Exception
import java.lang.reflect.Type


class HistorialFragment : Fragment() {

    private lateinit var datesByUser : ArrayList<Cita>
    private lateinit var restaurantsByUser : ArrayList<Restaurante>

    private lateinit var tvHistorial : TextView
    private lateinit var rvHistorial : RecyclerView
    private lateinit var rvCreatedRest : RecyclerView
    private lateinit var pbHistorial : ProgressBar
    private lateinit var linlayDatesNoData : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onResume() {
        tvHistorial = view?.findViewById(R.id.tvHistorial)!!
        rvHistorial = view?.findViewById(R.id.rvHistorial)!!
        rvCreatedRest = view?.findViewById(R.id.rvCreatedRest)!!
        pbHistorial = view?.findViewById(R.id.pbHistorial)!!
        linlayDatesNoData = view?.findViewById(R.id.linlayDatesNoData)!!

        val handler = Handler(Looper.getMainLooper())

        if(ActiveUser.getInstance().user_type_id == 0) {
            tvHistorial.setText(" Tu historial de Citas ")
            rvHistorial.visibility = View.VISIBLE
            rvCreatedRest.visibility = View.GONE

            handler.post(Runnable {
                //Starting Write and Read data with URL
                //Creating array for parameters
                val field = arrayOfNulls<String>(1)
                field[0] = "usuario_id"

                //Creating array for data
                val data = arrayOfNulls<String>(1)
                data[0] = ActiveUser.getInstance().id.toString()

                val putData = PutData(
                    "https://proyectodepsm.000webhostapp.com/getDatesByUser.php",
                    "POST",
                    field,
                    data
                )
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        val result = putData.result
                        pbHistorial.visibility = View.GONE;
                        Log.i("PutData", result)
                        var completed = false
                        val gson = Gson()
                        val listType: Type = object : TypeToken<ArrayList<Cita>>() {}.type
                        try {
                            datesByUser = gson.fromJson(result, listType)
                            rvHistorial.layoutManager = LinearLayoutManager(context)
                            rvHistorial.setHasFixedSize(true)
                            completed = true
                        } catch (e: Exception) {
                            datesByUser = ArrayList()
                            completed = false
                        }

                        if (!completed) {
                            Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_LONG)
                                .show();
                        }

                        getAllDatesByUser()
                    }
                }
                //End Write and Read data with URL
            })
        } else {
            tvHistorial.setText(" Tus restaurantes creados ")
            rvHistorial.visibility = View.GONE
            rvCreatedRest.visibility = View.VISIBLE

            handler.post(Runnable {
                //Starting Write and Read data with URL
                //Creating array for parameters
                val field = arrayOfNulls<String>(1)
                field[0] = "usuario_id"

                //Creating array for data
                val data = arrayOfNulls<String>(1)
                data[0] = ActiveUser.getInstance().id.toString()

                val putData = PutData(
                    "https://proyectodepsm.000webhostapp.com/getRestaurantsByUser.php",
                    "POST",
                    field,
                    data
                )
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        val result = putData.result
                        pbHistorial.visibility = View.GONE;
                        Log.i("PutData", result)
                        var completed = false
                        val gson = Gson()
                        val listType: Type = object : TypeToken<ArrayList<Restaurante>>() {}.type
                        try {
                            restaurantsByUser = gson.fromJson(result, listType)
                            rvCreatedRest.layoutManager = LinearLayoutManager(context)
                            rvCreatedRest.setHasFixedSize(true)
                            completed = true
                        } catch (e: Exception) {
                            restaurantsByUser = ArrayList()
                            completed = false
                        }

                        if (!completed) {
                            Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                        }

                        getAllRestaurantsByUser()
                    }
                }
                //End Write and Read data with URL
            })
        }

        super.onResume()
    }

    private fun getAllRestaurantsByUser() {
        if(restaurantsByUser.size > 0) {
            val adapter = CreatedRestaurantsAdapter(restaurantsByUser)

            rvCreatedRest.adapter = adapter

            adapter.setOnItemSetClickListener(object : CreatedRestaurantsAdapter.onItemClickListener {
                override fun onItemClick(position: Int, restaurant_id: String) {
                    val fragment = RestaurantFragment()

                    var bundle = Bundle()
                    bundle.putString("restaurant_id", restaurant_id)

                    val fragmentManager = activity?.supportFragmentManager
                    val fragmentTransaction = fragmentManager?.beginTransaction()

                    fragment.arguments = bundle

                    fragmentTransaction?.replace(R.id.fragmentContainerHome, fragment)
                    fragmentTransaction?.commit()
                }

            })
        } else {
            linlayDatesNoData.visibility = View.VISIBLE
        }
    }

    private fun getAllDatesByUser() {
        if(datesByUser.size > 0) {
            val adapter = HistorialAdapter(datesByUser)

            rvHistorial.adapter = adapter

            adapter.setOnItemSetClickListener(object : HistorialAdapter.onItemClickListener {
                override fun onItemClick(position: Int, restaurant_id: String) {
                    val fragment = RestaurantFragment()

                    var bundle = Bundle()
                    bundle.putString("restaurant_id", restaurant_id)

                    val fragmentManager = activity?.supportFragmentManager
                    val fragmentTransaction = fragmentManager?.beginTransaction()

                    fragment.arguments = bundle

                    fragmentTransaction?.replace(R.id.fragmentContainerHome, fragment)
                    fragmentTransaction?.commit()
                }

            })
        } else {
            linlayDatesNoData.visibility = View.VISIBLE
        }

    }

}