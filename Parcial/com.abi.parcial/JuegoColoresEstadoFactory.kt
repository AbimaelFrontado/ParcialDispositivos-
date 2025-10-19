package com.abi.parcial

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class JuegoColoresEstadoFactory(private val contexto: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JuegoColoresEstado::class.java)) {
            val preferencias = contexto.getSharedPreferences(
                "JuegoColoresPreferencias",
                Context.MODE_PRIVATE
            )

            return JuegoColoresEstado(preferencias) as T
        }
        throw IllegalArgumentException("Clase de estado desconocida: ${modelClass.name}")
    }
}
