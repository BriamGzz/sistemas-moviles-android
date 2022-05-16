package com.example.sismov

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sismov.Adapters.CreatedRestaurantsAdapter
import com.example.sismov.Adapters.HistorialAdapter
import com.example.sismov.Adapters.SearchAdapter
import com.example.sismov.Clases.Cita
import com.example.sismov.Clases.Restaurante
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishnusivadas.advanced_httpurlconnection.PutData
import java.lang.Exception
import java.lang.reflect.Type

class SearchFragment : Fragment() {

    private var advancedSearch = false

    lateinit var foundRestaurants : ArrayList<Restaurante>

    lateinit var etSearchName : EditText
    lateinit var spSearchCat : Spinner
    lateinit var ibSearch : ImageButton
    lateinit var ibAdvancedSearch : ImageButton
    lateinit var rbOrderCreacion : RadioButton
    lateinit var rbOrderCalificacion : RadioButton
    lateinit var cbAscendente : CheckBox
    lateinit var pbSearch : ProgressBar
    lateinit var rvSearch : RecyclerView

    lateinit var linlaySearch : LinearLayout
    lateinit var linlayAdvancedSearch : LinearLayout
    lateinit var linlaySearchError : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onResume() {
        etSearchName = view?.findViewById(R.id.etSearchName)!!
        spSearchCat = view?.findViewById(R.id.spSearchCat)!!
        ibSearch = view?.findViewById(R.id.ibSearch)!!
        pbSearch = view?.findViewById(R.id.pbSearch)!!
        rvSearch = view?.findViewById(R.id.rvSearch)!!

        ibAdvancedSearch = view?.findViewById(R.id.ibAdvancedSearch)!!
        rbOrderCreacion = view?.findViewById(R.id.rbOrderCreacion)!!
        rbOrderCalificacion = view?.findViewById(R.id.rbOrderCalificacion)!!
        cbAscendente = view?.findViewById(R.id.cbAscendente)!!

        linlaySearch = view?.findViewById(R.id.linlaySearch)!!
        linlayAdvancedSearch = view?.findViewById(R.id.linlayAdvancedSearch)!!
        linlaySearchError = view?.findViewById(R.id.linlaySearchError)!!

        ibSearch.setOnClickListener {
            Search()
        }

        ibAdvancedSearch.setOnClickListener {
            changeAdvancedSearch(!advancedSearch)
        }


        Search()

        super.onResume()
    }

    private fun changeAdvancedSearch(isAdvanced:Boolean) {
        advancedSearch = isAdvanced

        if(advancedSearch) {
            linlayAdvancedSearch.visibility = View.VISIBLE
            ibAdvancedSearch.setImageResource(android.R.drawable.arrow_up_float)
        } else {
            linlayAdvancedSearch.visibility = View.GONE
            ibAdvancedSearch.setImageResource(android.R.drawable.arrow_down_float)
        }
    }

    private fun Search() {
        var order = "restaurant_id"
        var ascdsc = "ASC"
        var cat = ""

        if(advancedSearch) {
            if(rbOrderCalificacion.isChecked) {
                order = "calificacion"
            } else if (rbOrderCreacion.isChecked) {
                order = "creation_date"
            }

            ascdsc = if (cbAscendente.isChecked) {
                "ASC"
            } else {
                "DESC"
            }

            cat = spSearchCat.selectedItem.toString();
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            pbSearch.visibility = View.VISIBLE
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(4)
            field[0] = "nombre"
            field[1] = "categoria"
            field[2] = "order"
            field[3] = "ascdsc"

            //Creating array for data
            val data = arrayOfNulls<String>(4)
            data[0] = etSearchName.text.toString()
            data[1] = cat
            data[2] = order
            data[3] = ascdsc

            val putData = PutData(
                "https://proyectodepsm.000webhostapp.com/searchForRestaurant.php",
                "POST",
                field,
                data
            )
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    pbSearch.visibility = View.GONE;
                    Log.i("PutData", result)
                    var completed = false
                    val gson = Gson()
                    val listType: Type = object : TypeToken<ArrayList<Restaurante>>() {}.type
                    try {
                        foundRestaurants = gson.fromJson(result, listType)
                        rvSearch.layoutManager = LinearLayoutManager(context)
                        rvSearch.setHasFixedSize(true)
                        completed = true
                    } catch (e: Exception) {
                        foundRestaurants = ArrayList()
                        completed = false
                    }

                    if (!completed) {
                        Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                    }

                    changeAdvancedSearch(false)
                    showResults()
                }
            }
            //End Write and Read data with URL
        })
    }

    private fun showResults() {
        if(foundRestaurants.size > 0) {
            linlaySearchError.visibility = View.GONE
            linlaySearch.visibility = View.VISIBLE
            val adapter = SearchAdapter(foundRestaurants)

            rvSearch.adapter = adapter

            adapter.setOnItemSetClickListener(object : SearchAdapter.onItemClickListener {
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
            linlaySearchError.visibility = View.VISIBLE
            linlaySearch.visibility = View.GONE
        }
    }

}