package com.example.sismov

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sismov.Adapters.RestaurantsAdapter
import com.example.sismov.Clases.Restaurante
import com.google.gson.Gson
import com.vishnusivadas.advanced_httpurlconnection.PutData
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class HomeFragment : Fragment() {

    private lateinit var rvAllRestaurants : RecyclerView
    private lateinit var tvHome : TextView
    private lateinit var pbHome : ProgressBar
    private lateinit var allRestaurants : ArrayList<Restaurante>
    private lateinit var allRestaurantsJSON : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {

        rvAllRestaurants = view?.findViewById<RecyclerView>(R.id.rvAllRestaurants)!!
        tvHome = view?.findViewById<TextView>(R.id.tvHome)!!
        pbHome = view?.findViewById(R.id.pbHome)!!

        pbHome.visibility = View.VISIBLE;
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(1)
            field[0] = "table"

            //Creating array for data
            val data = arrayOfNulls<String>(1)
            data[0] = "restaurantes"

            val putData = PutData(
                //"http://192.168.1.64/php/signup.php",
                //"http://192.168.100.9/php/sistemas-moviles-php/login.php"
                "https://proyectodepsm.000webhostapp.com/getAllRestaurants.php",
                "POST",
                field,
                data
            )
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    pbHome.visibility = View.GONE;
                    Log.i("PutData", result)
                    if(!result.equals("Error: Database connection")) {
                        val gson = Gson()
                        val listType: Type = object : TypeToken<ArrayList<Restaurante>>() {}.type

                        allRestaurants = gson.fromJson<ArrayList<Restaurante>>(result, listType)

                        rvAllRestaurants.layoutManager = LinearLayoutManager(context)
                        rvAllRestaurants.setHasFixedSize(true)
                        getAllRestaurants()

                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    }
                }
            }
            //End Write and Read data with URL
        })

        super.onResume()
    }

    private fun getAllRestaurants() {
        val adapter = RestaurantsAdapter(allRestaurants)

        rvAllRestaurants.adapter = adapter

        adapter.setOnItemSetClickListener(object : RestaurantsAdapter.onItemClickListener{
            override fun onItemClick(position: Int, restaurant_id: String) {
                var bundle = Bundle()

                bundle.putString("restaurant_id", restaurant_id)

                val fragmentManager = activity?.supportFragmentManager
                val fragmentTransaction = fragmentManager?.beginTransaction()
                val fragment = RestaurantFragment()

                fragment.arguments = bundle

                fragmentTransaction?.replace(R.id.fragmentContainerHome, fragment)
                fragmentTransaction?.commit()

            }

        })

    }

}