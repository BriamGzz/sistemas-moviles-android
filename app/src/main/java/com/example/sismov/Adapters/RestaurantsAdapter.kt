package com.example.sismov.Adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sismov.Clases.Restaurante
import com.example.sismov.R

class RestaurantsAdapter(private val allRestaurants: ArrayList<Restaurante>) :
    RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    private lateinit var restaurantsListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick( position: Int, restaurant_id : String )

    }

    fun setOnItemSetClickListener(listener: onItemClickListener) {
        restaurantsListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)

        return ViewHolder(itemView, restaurantsListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = allRestaurants[position]
        holder.tvID.text = currentItem.restaurant_id.toString()
        holder.tvNombre.text = currentItem.nombre
        holder.tvApertura.text = currentItem.fecha_apertura
        holder.tvCierre.text = currentItem.fecha_cierre

        if(currentItem.image != null) {
            val byte = Base64.decode(currentItem.image, 0)
            val bmp = BitmapFactory.decodeByteArray(byte, 0, byte.size)
            holder.ivImage.setImageBitmap(bmp)
        }
    }

    override fun getItemCount(): Int {


        return allRestaurants.size
    }

    class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvID = itemView.findViewById<TextView>(R.id.tvCardRestId)
        val ivImage = itemView.findViewById<ImageView>(R.id.ivCardImage)
        val tvNombre = itemView.findViewById<TextView>(R.id.tvCardNombre)
        val tvApertura = itemView.findViewById<TextView>(R.id.tvCardApertura)
        val tvCierre = itemView.findViewById<TextView>(R.id.tvCardCierre)

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, tvID.text.toString())
            }

        }

    }
}