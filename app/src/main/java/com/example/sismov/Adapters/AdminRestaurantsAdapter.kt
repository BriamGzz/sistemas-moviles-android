package com.example.sismov.Adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sismov.Clases.Restaurante
import com.example.sismov.R
import com.vishnusivadas.advanced_httpurlconnection.PutData

class AdminRestaurantsAdapter(private val adminRestaurants: ArrayList<Restaurante>, private val callback:CallBack) :
    RecyclerView.Adapter<AdminRestaurantsAdapter.ViewHolder>() {

    private lateinit var restaurantsListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int, restaurant_id: String)

    }

    fun setOnItemSetClickListener(listener: onItemClickListener) {
        restaurantsListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)

        return ViewHolder(itemView, restaurantsListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = adminRestaurants[position]
        holder.linlayAdmin.visibility = View.VISIBLE
        holder.tvID.text = currentItem.restaurant_id.toString()
        holder.tvNombre.text = currentItem.nombre
        holder.tvApertura.text = currentItem.fecha_apertura
        holder.tvCierre.text = currentItem.fecha_cierre

        if (currentItem.image != null) {
            val byte = Base64.decode(currentItem.image, 0)
            val bmp = BitmapFactory.decodeByteArray(byte, 0, byte.size)
            holder.ivImage.setImageBitmap(bmp)
        }

        holder.btnAccept.setOnClickListener {
            holder.tvAccepted.setText("1")
            changeAccepted(holder.tvAccepted.text.toString(),currentItem.restaurant_id.toString())
        }

        holder.btnReject.setOnClickListener {
            holder.tvAccepted.setText("-1")
            changeAccepted(holder.tvAccepted.text.toString(),currentItem.restaurant_id.toString())
        }


    }

    private fun changeAccepted(accepted : String, restaurant_id : String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            //Starting Write and Read data with URL
            //Creating array for parameters
            val field = arrayOfNulls<String>(2)
            field[0] = "accepted"
            field[1] = "restaurant_id"

            //Creating array for data
            val data = arrayOfNulls<String>(2)
            data[0] = accepted
            data[1] = restaurant_id

            val putData = PutData(
                "https://proyectodepsm.000webhostapp.com/adminRestaurant.php",
                "POST",
                field,
                data
            )
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    val result = putData.result
                    if (result.equals("Hecho")) {
                        callback.actualizar()
                    } else {
                        callback.error()
                    }
                }
            }
            //End Write and Read data with URL
        })
    }

    override fun getItemCount(): Int {

        return adminRestaurants.size
    }

    class ViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvID = itemView.findViewById<TextView>(R.id.tvCardRestId)
        val ivImage = itemView.findViewById<ImageView>(R.id.ivCardImage)
        val tvNombre = itemView.findViewById<TextView>(R.id.tvCardNombre)
        val tvApertura = itemView.findViewById<TextView>(R.id.tvCardApertura)
        val tvCierre = itemView.findViewById<TextView>(R.id.tvCardCierre)
        val tvAccepted = itemView.findViewById<TextView>(R.id.tvRestaurantAccepted)

        val btnAccept = itemView.findViewById<ImageButton>(R.id.ibAcceptRest)
        val btnReject = itemView.findViewById<ImageButton>(R.id.ibRejectRest)

        val linlayAdmin = itemView.findViewById<LinearLayout>(R.id.linlayAdmin)

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, tvID.text.toString())
            }

        }

    }

    interface CallBack {
        fun actualizar()
        fun error()
    }

}