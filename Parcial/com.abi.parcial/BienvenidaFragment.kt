package com.abi.parcial

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abi.parcial.R

class BienvenidaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bienvenida, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mostrarDialogoReglas()

        view.findViewById<Button>(R.id.boton_iniciar_juego).setOnClickListener {

            findNavController().navigate(R.id.accion_bienvenida_a_juego)
        }
    }

    private fun mostrarDialogoReglas() {
        val reglas = getString(R.string.reglas_juego)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.titulo_reglas))
            .setMessage(reglas)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
