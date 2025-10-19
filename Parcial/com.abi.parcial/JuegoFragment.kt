package com.abi.parcial

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.abi.parcial.databinding.FragmentJuegoBinding

private val COLOR_ROJO_BOTON = Color.parseColor("#F44336")
private val COLOR_AZUL_BOTON = Color.parseColor("#2196F3")
private val COLOR_VERDE_BOTON = Color.parseColor("#4CAF50")
private val COLOR_AMARILLO_BOTON = Color.parseColor("#FFEB3B")


class JuegoFragment : Fragment() {

    private var _binding: FragmentJuegoBinding? = null
    private val binding get() = _binding!!

    private val estado: JuegoColoresEstado by activityViewModels {
        JuegoColoresEstadoFactory(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJuegoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarBotonesDificultad()
        setupBotonesRespuesta()

        configurarUIEspera()

        setupObservers()

    }

    private fun configurarUIEspera() {
        setBotonesDificultadHabilitados(true)
        setBotonesRespuestaHabilitados(false)

        binding.tvPalabra.text = getString(R.string.dificultad)

        val puntajeAlto = estado.obtenerPuntajeAlto()

        binding.cardJuego.setCardBackgroundColor(Color.WHITE)
        binding.tvPalabra.setTextColor(Color.BLACK)
        binding.tvPuntuacion.text = getString(R.string.formato_puntaje, 0)

        binding.tvTiempo.text = getString(R.string.formato_tiempo, 30)
    }

    private fun inicializarBotonesDificultad() {

        binding.btnFacil.text = "FÁCIL"
        binding.btnMedia.text = "MEDIA"
        binding.btnDificil.text = "DIFÍCIL"

        val listener = View.OnClickListener {
            estado.iniciarJuego()
        }

        binding.btnFacil.setOnClickListener(listener)
        binding.btnMedia.setOnClickListener(listener)
        binding.btnDificil.setOnClickListener(listener)
    }

    private fun setupBotonesRespuesta() {
        binding.btnColor0.apply { text = ColorJuego.ROJO.nombreColorEs; setOnClickListener { verificar(ColorJuego.ROJO) } }
        binding.btnColor1.apply { text = ColorJuego.AZUL.nombreColorEs; setOnClickListener { verificar(ColorJuego.AZUL) } }
        binding.btnColor2.apply { text = ColorJuego.VERDE.nombreColorEs; setOnClickListener { verificar(ColorJuego.VERDE) } }
        binding.btnColor3.apply { text = ColorJuego.AMARILLO.nombreColorEs; setOnClickListener { verificar(ColorJuego.AMARILLO) } }
    }

    private fun verificar(color: ColorJuego) {
        estado.verificarRespuesta(color)
    }

    private fun setupObservers() {
        estado.estadoJuego.observe(viewLifecycleOwner) { estadoActual ->
            actualizarUI(estadoActual)
            val activo = estadoActual.estaActivo
            setBotonesDificultadHabilitados(!activo)
            setBotonesRespuestaHabilitados(activo)

            if (!activo && estadoActual.puntaje > 0) {
                configurarUIEspera()
            }
        }

        estado.juegoTerminado.observe(viewLifecycleOwner) { terminado ->
            if (terminado == true) {
                Log.i("JuegoFragment", "Juego terminado, navegando a resultados.")

                try {
                    findNavController().navigate(R.id.accion_juego_a_resultado)
                } catch (e: Exception) {
                    Log.e("JuegoFragment", "Error de navegación: ${e.message}")
                } finally {
                    estado.notificarNavegacionResultado()
                }
            }
        }
    }

    private fun actualizarUI(estadoActual: EstadoJuego) {
        binding.tvPuntuacion.text = getString(R.string.formato_puntaje_final, estadoActual.puntaje)
        val segundos = estadoActual.tiempoRestante / 1000
        binding.tvTiempo.text = getString(R.string.formato_tiempo, segundos)

        if (estadoActual.estaActivo) {
            binding.tvPalabra.text = estadoActual.palabraMostrada.nombreColorEs
            binding.tvPalabra.setTextColor(estadoActual.colorObjetivo.codigoColor)
        }
    }

    private fun setBotonesRespuestaHabilitados(habilitado: Boolean) {
        val botonesConColor = listOf(
            binding.btnColor0 to COLOR_ROJO_BOTON,
            binding.btnColor1 to COLOR_AZUL_BOTON,
            binding.btnColor2 to COLOR_VERDE_BOTON,
            binding.btnColor3 to COLOR_AMARILLO_BOTON
        )

        botonesConColor.forEach { (button, color) ->
            button.isEnabled = habilitado

            if (habilitado) {
                button.backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun setBotonesDificultadHabilitados(habilitado: Boolean) {
        listOf(binding.btnFacil, binding.btnMedia, binding.btnDificil).forEach {
            it.isEnabled = habilitado
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}