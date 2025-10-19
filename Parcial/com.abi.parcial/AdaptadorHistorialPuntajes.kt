package com.abi.parcial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorHistorialPuntajes(private val historial: List<ResultadoPuntaje>) :
    RecyclerView.Adapter<AdaptadorHistorialPuntajes.VistaPuntajeHolder>() {

    class VistaPuntajeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textoPuntaje: TextView = view.findViewById(R.id.texto_item_puntaje)
        val textoFecha: TextView = view.findViewById(R.id.texto_item_fecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaPuntajeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_puntaje, parent, false)
        return VistaPuntajeHolder(view)
    }

    override fun onBindViewHolder(holder: VistaPuntajeHolder, position: Int) {
        val item = historial[position]
        val formatoFecha = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        holder.textoPuntaje.text = holder.itemView.context.getString(
            R.string.formato_item_historial,
            historial.size - position,
            item.puntaje
        )
        holder.textoFecha.text = formatoFecha.format(Date(item.fecha))
    }

    override fun getItemCount() = historial.size
}
