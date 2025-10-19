package com.abi.parcial

import android.content.SharedPreferences
import android.graphics.Color
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private const val TIEMPO_TOTAL_MS = 30000L

private const val INTERVALO_TEMPORIZADOR_MS = 1000L

private const val KEY_PUNTAJE_ALTO = "puntaje_alto"
private const val KEY_HISTORIAL_PUNTAJES = "historial_puntajes"

enum class ColorJuego(val nombreColorEs: String, val codigoColor: Int) {
    ROJO("ROJO", Color.RED),
    AZUL("AZUL", Color.BLUE),
    VERDE("VERDE", Color.GREEN),
    AMARILLO("AMARILLO", Color.YELLOW);

    companion object {
        private val valores = values()
        fun obtenerColoresAleatorios(cantidad: Int): List<ColorJuego> {
            return valores.toList().shuffled().take(cantidad)
        }
    }
}

data class ResultadoPuntaje(
    val puntaje: Int,
    val fecha: Long = System.currentTimeMillis()
)

data class EstadoJuego(
    val puntaje: Int = 0,
    val tiempoRestante: Long = TIEMPO_TOTAL_MS,
    val palabraMostrada: ColorJuego = ColorJuego.ROJO,
    val colorObjetivo: ColorJuego = ColorJuego.AZUL,
    val estaActivo: Boolean = false
)

class JuegoColoresEstado(private val prefs: SharedPreferences) : ViewModel() {

    private val _estadoJuego = MutableLiveData(EstadoJuego())
    val estadoJuego: LiveData<EstadoJuego> = _estadoJuego

    private val _juegoTerminado = MutableLiveData(false)
    val juegoTerminado: LiveData<Boolean> = _juegoTerminado

    private val _historialPuntajes = MutableLiveData<List<ResultadoPuntaje>>(emptyList())
    val historialPuntajes: LiveData<List<ResultadoPuntaje>> = _historialPuntajes

    private var temporizador: CountDownTimer? = null


    fun iniciarJuego() {

        temporizador?.cancel()

        val nuevoDesafio = generarDesafio()
        _estadoJuego.value = EstadoJuego(
            puntaje = 0,
            tiempoRestante = TIEMPO_TOTAL_MS,
            palabraMostrada = nuevoDesafio.first,
            colorObjetivo = nuevoDesafio.second,
            estaActivo = true
        )

        iniciarTemporizador()
    }

    private fun iniciarTemporizador() {
        temporizador = object : CountDownTimer(TIEMPO_TOTAL_MS, INTERVALO_TEMPORIZADOR_MS) {
            override fun onTick(millisUntilFinished: Long) {
                _estadoJuego.value = _estadoJuego.value?.copy(tiempoRestante = millisUntilFinished)
            }

            override fun onFinish() {
                finalizarJuego()
            }
        }.start()
    }

    private fun finalizarJuego() {
        temporizador?.cancel()
        val estadoFinal = _estadoJuego.value?.copy(
            tiempoRestante = 0,
            estaActivo = false
        )
        _estadoJuego.value = estadoFinal

        estadoFinal?.let { guardarPuntaje(it.puntaje) }

        _juegoTerminado.value = true
    }

    fun verificarRespuesta(respuesta: ColorJuego) {
        val estadoActual = _estadoJuego.value ?: return
        if (!estadoActual.estaActivo) return

        var nuevoPuntaje = estadoActual.puntaje

        if (respuesta == estadoActual.colorObjetivo) {
            nuevoPuntaje += 1
        }

        val nuevoDesafio = generarDesafio()
        _estadoJuego.value = estadoActual.copy(
            puntaje = nuevoPuntaje,
            palabraMostrada = nuevoDesafio.first,
            colorObjetivo = nuevoDesafio.second
        )
    }

    private fun generarDesafio(): Pair<ColorJuego, ColorJuego> {
        val palabras = ColorJuego.values()

        val palabraMostrada = palabras.random()

        val colorObjetivo = palabras.random()

        if (palabraMostrada == colorObjetivo) {
            val coloresRestantes = palabras.filter { it != colorObjetivo }
            return Pair(palabraMostrada, coloresRestantes.random())
        }

        return Pair(palabraMostrada, colorObjetivo)
    }

    private fun guardarPuntaje(puntaje: Int) = viewModelScope.launch(Dispatchers.IO) {

        val puntajeAltoActual = obtenerPuntajeAlto()
        if (puntaje > puntajeAltoActual) {
            prefs.edit().putInt(KEY_PUNTAJE_ALTO, puntaje).apply()
        }

        _historialPuntajes.postValue(listOf(ResultadoPuntaje(puntaje)))
    }

    fun obtenerPuntajeAlto(): Int {
        return prefs.getInt(KEY_PUNTAJE_ALTO, 0)
    }


    fun notificarNavegacionResultado() {
        _juegoTerminado.value = false
    }

    override fun onCleared() {
        super.onCleared()
        temporizador?.cancel()
    }
}
