package com.example.proyecto

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.PasoReceta
import com.example.proyecto.models.SolicitudProduccion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductionActivity : AppCompatActivity() {

    private lateinit var orderTitle: TextView
    private lateinit var customerName: TextView
    private lateinit var orderDetails: TextView
    private lateinit var statusPoint: ImageView
    private lateinit var timer: TextView
    private lateinit var timerIcon: ImageView
    private lateinit var instructions: TextView
    private lateinit var nextButton: Button

    private var currentStep = 0
    private var steps: List<PasoReceta> = listOf()
    private var detalleSolicitudId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_production_mobile)

        // Inicializar las vistas
        orderTitle = findViewById(R.id.order_title)
        customerName = findViewById(R.id.customer_name)
        orderDetails = findViewById(R.id.order_details)
        statusPoint = findViewById(R.id.status_point)
        timer = findViewById(R.id.timer)
        timerIcon = findViewById(R.id.timer_icon)
        instructions = findViewById(R.id.instructions)
        nextButton = findViewById(R.id.next_button)

        // Obtener el ID de la solicitud de producción estatico
        val solicitudId = 1

        // Obtener la solicitud de producción
        RetrofitClient.instance.getSolicitudProduccion(solicitudId).enqueue(object : Callback<SolicitudProduccion> {
            override fun onResponse(call: Call<SolicitudProduccion>, response: Response<SolicitudProduccion>) {
                if (response.isSuccessful) {
                    val solicitud = response.body()
                    solicitud?.let {
                        // Log para verificar los datos recibidos
                        Log.d("MainActivity", "Solicitud de producción recibida: $solicitud")

                        // Actualizar las vistas con los datos de la solicitud
                        orderTitle.text = "Pedido de ${it.nombreCliente}"
                        customerName.text = it.detalleSolicituds.firstOrNull()?.nombreUsuario ?: "Desconocido"
                        orderDetails.text = "${it.cantidadProduccion} Litros de ${it.nombreProducto}"

                        // Obtener el ID de detalleSolicitud para actualizar el paso
                        detalleSolicitudId = it.detalleSolicituds.firstOrNull()?.idDetalleSolicitud ?: 0

                        // Actualizar el estado del punto de progreso
                        statusPoint.setImageResource(R.drawable.circle_in_progress)

                        // Actualizar el estado de la solicitud a "En Proceso"
                        updateSolicitudProduccionEstatus(it.idSolicitud, 2)

                        // Obtener los pasos de la receta
                        RetrofitClient.instance.getPasosReceta(it.idProducto).enqueue(object : Callback<List<PasoReceta>> {
                            override fun onResponse(call: Call<List<PasoReceta>>, response: Response<List<PasoReceta>>) {
                                if (response.isSuccessful) {
                                    steps = response.body() ?: listOf()
                                    if (steps.isNotEmpty()) {
                                        currentStep = 0
                                        showStep(currentStep)
                                        updateDetalleSolicitudPaso(detalleSolicitudId, currentStep + 1) // Paso inicial es 1
                                    }
                                } else {
                                    Log.e("MainActivity", "Error en la respuesta de pasos de receta: ${response.errorBody()}")
                                }
                            }

                            override fun onFailure(call: Call<List<PasoReceta>>, t: Throwable) {
                                Log.e("MainActivity", "Error al obtener pasos de receta", t)
                            }
                        })
                    }
                } else {
                    Log.e("MainActivity", "Error en la respuesta de solicitud de producción: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<SolicitudProduccion>, t: Throwable) {
                Log.e("MainActivity", "Error al obtener solicitud de producción", t)
            }
        })

        // Configurar el botón de siguiente
        nextButton.setOnClickListener {
            if (currentStep < steps.size - 1) {
                val nextStep = currentStep + 1
                updateDetalleSolicitudPaso(detalleSolicitudId, nextStep + 1) // Actualiza al siguiente paso
                currentStep = nextStep
                showStep(currentStep)
                if (currentStep == steps.size - 1) {
                    nextButton.text = "Finalizar"
                }
            } else {
                // Acciones cuando se han completado todos los pasos
                statusPoint.setImageResource(R.drawable.circle_completed)
                updateSolicitudProduccionEstatus(solicitudId, 3) // Cambiar estatus a completado (3)
            }
        }
    }

    private fun showStep(stepIndex: Int) {
        val step = steps[stepIndex]
        instructions.text = "${step.paso}. ${step.descripcion}"
    }

    private fun updateSolicitudProduccionEstatus(id: Int, estatus: Int) {
        RetrofitClient.instance.updateSolicitudProduccionEstatus(id, estatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Log.e("MainActivity", "Error al actualizar el estatus de la solicitud: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error al actualizar el estatus de la solicitud", t)
            }
        })
    }

    private fun updateDetalleSolicitudPaso(id: Int, paso: Int) {
        RetrofitClient.instance.updateDetalleSolicitudPaso(id, paso).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Paso actualizado a $paso en el servidor")
                } else {
                    Log.e("MainActivity", "Error al actualizar el paso del detalle de solicitud: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error al actualizar el paso del detalle de solicitud", t)
            }
        })
    }
}
