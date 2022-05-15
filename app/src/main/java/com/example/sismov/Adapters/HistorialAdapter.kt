package com.example.sismov.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sismov.Clases.Cita
import com.example.sismov.R

class HistorialAdapter(private val datesByUser: ArrayList<Cita>) :
    RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    private lateinit var historialListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick( position: Int, restaurant_id : String )

    }

    fun setOnItemSetClickListener(listener: onItemClickListener) {
        historialListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.date_card, parent, false)

        return ViewHolder(itemView, historialListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = datesByUser[position]

        holder.tvIdDate.text = currentItem.id_cita.toString()
        holder.tvRestId.text = currentItem.restaurante_id.toString()
        holder.tvRestName.text = "Restaurante ${currentItem.restaurante_nombre}"
        holder.tvPersonas.text = "Para ${currentItem.personas} personas"
        holder.tvTotal.text = "Total a pagar:$${currentItem.total}"
        holder.tvFecha.text = currentItem.fecha
        holder.tvHora.text = currentItem.hora

    }

    override fun getItemCount(): Int {


        return datesByUser.size
    }

    class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvIdDate = itemView.findViewById<TextView>(R.id.tvDateCardId)
        val tvRestId = itemView.findViewById<TextView>(R.id.tvDateCardRestId)
        val tvRestName = itemView.findViewById<TextView>(R.id.tvDateCardRestaurant)
        val tvPersonas = itemView.findViewById<TextView>(R.id.tvDateCardPersonas)
        val tvTotal = itemView.findViewById<TextView>(R.id.tvDateCardPrecio)
        val tvFecha = itemView.findViewById<TextView>(R.id.tvDateCardFecha)
        val tvHora = itemView.findViewById<TextView>(R.id.tvDateCardHora)

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, tvRestId.text.toString())
            }

        }

    }
}