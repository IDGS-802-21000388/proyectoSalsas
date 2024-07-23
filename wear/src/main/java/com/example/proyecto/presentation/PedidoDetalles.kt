package com.example.proyecto

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.PasoReceta
import com.example.proyecto.models.SolicitudProduccion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PedidoDetalles : AppCompatActivity() {

    private lateinit var orderTitle: TextView
    private lateinit var customerName: TextView
    private lateinit var orderDetails: TextView
    private lateinit var statusPoint: ImageView
    private lateinit var instructions: TextView

    private var steps: List<PasoReceta> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detalles) // Asegúrate de que este layout sea adecuado para Wear OS

        // Inicializar las vistas
        orderTitle = findViewById(R.id.order_title)
        customerName = findViewById(R.id.customer_name)
        orderDetails = findViewById(R.id.order_details)
        statusPoint = findViewById(R.id.status_point)
        instructions = findViewById(R.id.instructions)

        // Obtener el ID de la solicitud de producción estático
        val solicitudId = 1

        // Obtener la solicitud de producción
        RetrofitClient.instance.getSolicitudProduccion(solicitudId).enqueue(object : Callback<SolicitudProduccion> {
            override fun onResponse(call: Call<SolicitudProduccion>, response: Response<SolicitudProduccion>) {
                if (response.isSuccessful) {
                    val solicitud = response.body()
                    solicitud?.let {
                        // Log para verificar los datos recibidos
                        Log.d("PedidoDetallesActivity", "Solicitud de producción recibida: $solicitud")

                        // Actualizar las vistas con los datos de la solicitud
                        orderTitle.text = "Pedido de ${it.nombreCliente}"
                        customerName.text = it.detalleSolicituds.firstOrNull()?.nombreUsuario ?: "Desconocido"
                        orderDetails.text = "${it.cantidadProduccion} Litros de ${it.nombreProducto}"

                        // Actualizar el estado del punto de progreso
                        statusPoint.setImageResource(R.drawable.circle_in_progress)

                        // Obtener los pasos de la receta
                        RetrofitClient.instance.getPasosReceta(it.idProducto).enqueue(object : Callback<List<PasoReceta>> {
                            override fun onResponse(call: Call<List<PasoReceta>>, response: Response<List<PasoReceta>>) {
                                if (response.isSuccessful) {
                                    steps = response.body() ?: listOf()
                                    if (steps.isNotEmpty()) {
                                        // Mostrar el primer paso
                                        showStep(0)
                                    }
                                } else {
                                    Log.e("PedidoDetallesActivity", "Error en la respuesta de pasos de receta: ${response.errorBody()}")
                                }
                            }

                            override fun onFailure(call: Call<List<PasoReceta>>, t: Throwable) {
                                Log.e("PedidoDetallesActivity", "Error al obtener pasos de receta", t)
                            }
                        })
                    }
                } else {
                    Log.e("PedidoDetallesActivity", "Error en la respuesta de solicitud de producción: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<SolicitudProduccion>, t: Throwable) {
                Log.e("PedidoDetallesActivity", "Error al obtener solicitud de producción", t)
            }
        })
    }

    private fun showStep(stepIndex: Int) {
        if (steps.isNotEmpty() && stepIndex < steps.size) {
            val step = steps[stepIndex]
            instructions.text = "${step.paso}. ${step.descripcion}"
        }
    }
}
