package com.abi.parcial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abi.parcial.R
import com.abi.parcial.JuegoColoresEstadoFactory
import com.abi.parcial.JuegoColoresEstado

class ResultadoFragment : Fragment() {

    private val estado: JuegoColoresEstado by activityViewModels {
        JuegoColoresEstadoFactory(requireActivity().applicationContext)
    }

    private lateinit var listaPuntajes: RecyclerView
    private lateinit var adaptador: AdaptadorHistorialPuntajes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resultado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textoPuntajeFinal: TextView = view.findViewById(R.id.texto_puntaje_final)
        val textoPuntajeAlto: TextView = view.findViewById(R.id.texto_puntaje_alto)
        listaPuntajes = view.findViewById(R.id.lista_historial_puntajes)
        val botonJugarOtraVez: Button = view.findViewById(R.id.boton_jugar_otra_vez)

        val puntajeFinal = estado.estadoJuego.value?.puntaje ?: 0
        val puntajeAlto = estado.obtenerPuntajeAlto()

        textoPuntajeFinal.text = getString(R.string.formato_puntaje_final, puntajeFinal)
        textoPuntajeAlto.text = getString(R.string.formato_puntaje_alto, puntajeAlto)

        listaPuntajes.layoutManager = LinearLayoutManager(context)

        estado.historialPuntajes.observe(viewLifecycleOwner) { listaHistorial ->
            adaptador = AdaptadorHistorialPuntajes(listaHistorial.toList())
            listaPuntajes.adapter = adaptador
        }

        botonJugarOtraVez.setOnClickListener {
            findNavController().navigate(R.id.accion_resultado_a_juego)
        }
    }
}
