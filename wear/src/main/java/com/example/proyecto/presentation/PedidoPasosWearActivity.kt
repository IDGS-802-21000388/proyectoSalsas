package com.example.proyecto.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.presentation.models.PasoReceta
import com.example.proyecto.presentation.models.Pedido
import com.example.proyecto.presentation.models.DetalleVenta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PedidoPasosWearActivity : AppCompatActivity() {

    private lateinit var orderTitle: TextView
    private lateinit var customerName: TextView
    private lateinit var orderDetails: TextView
    private lateinit var instructions: TextView
    private lateinit var statusPoint: ImageView
    private lateinit var nextButton: Button

    private var currentStep = 0
    private var currentProductIndex = 0
    private var steps: List<PasoReceta> = listOf()
    private var products: List<DetalleVenta> = listOf()
    private var detalleSolicitudId: Int = 0
    private var solicitudId: Int = 0
    private var idVenta: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidopasoswear)

        // Inicializar vistas
        orderTitle = findViewById(R.id.orderTitle)
        customerName = findViewById(R.id.customerName)
        orderDetails = findViewById(R.id.orderDetails)
        instructions = findViewById(R.id.instructions)
        statusPoint = findViewById(R.id.statusPoint)
        nextButton = findViewById(R.id.nextButton)

        val pedido = intent.getParcelableExtra<Pedido>("pedido")

        // Actualizar la UI con los detalles del Pedido
        pedido?.let {
            orderTitle.text = "Pedido de ${it.usuarioCliente}"
            customerName.text = it.detallesProduccion.firstOrNull()?.usuarioProduccion ?: "Desconocido"
            products = it.venta?.detalleVenta ?: listOf()

            // Guardar IDs necesarios para las actualizaciones
            detalleSolicitudId = it.detallesProduccion.firstOrNull()?.idDetalleSolicitud ?: 0
            solicitudId = it.idSolicitud
            idVenta = it.venta?.idVenta ?: 0

            // Actualizar estatus a 2
            updateSolicitudProduccionEstatus(solicitudId, 2)

            // Cargar el primer producto
            if (products.isNotEmpty()) {
                loadProductSteps(products[currentProductIndex])
            }
        }

        nextButton.setOnClickListener {
            if (currentStep < steps.size - 1) {
                currentStep++
                showStep(currentStep)
                updateDetalleSolicitudPaso(detalleSolicitudId, currentStep + 1)
            } else {
                // Si se completan todos los pasos de un producto, pasar al siguiente
                if (currentProductIndex < products.size - 1) {
                    currentProductIndex++
                    loadProductSteps(products[currentProductIndex])
                    nextButton.text = "Siguiente" // Resetear el texto del botón
                } else {
                    // Cambiar el estado de la solicitud a completado, actualizar el estatus a 3 y actualizar el estatus de envío
                    updateSolicitudProduccionEstatus(solicitudId, 3)
                    updateEnvioEstatus(idVenta, "pendiente de envío")
                    statusPoint.setImageResource(R.drawable.circle_completed) // Cambiar a drawable verde
                    nextButton.text = "Finalizar"
                    nextButton.setOnClickListener {
                        finish() // Regresar a la lista de pedidos
                    }
                }
            }
        }
    }

    private fun loadProductSteps(producto: DetalleVenta) {
        // Reseteamos el paso actual
        currentStep = 0

        // Actualizar los detalles del pedido
        orderDetails.text = "${producto.cantidad} Litros de ${producto.producto.nombreProducto}"

        // Actualizar el estado visual
        statusPoint.setImageResource(R.drawable.circle_in_progress)

        RetrofitClient.instance.getPasosReceta(producto.producto.idProducto).enqueue(object : Callback<List<PasoReceta>> {
            override fun onResponse(call: Call<List<PasoReceta>>, response: Response<List<PasoReceta>>) {
                if (response.isSuccessful) {
                    steps = response.body() ?: listOf()
                    if (steps.isNotEmpty()) {
                        showStep(currentStep)
                    }
                }
            }

            override fun onFailure(call: Call<List<PasoReceta>>, t: Throwable) {
                Toast.makeText(this@PedidoPasosWearActivity, "Error al obtener los pasos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showStep(stepIndex: Int) {
        val step = steps[stepIndex]
        instructions.text = "${step.paso}. ${step.descripcion}"
    }

    private fun updateSolicitudProduccionEstatus(id: Int, estatus: Int) {
        RetrofitClient.instance.updateSolicitudProduccionEstatus(id, estatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@PedidoPasosWearActivity, "Error al actualizar el estatus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PedidoPasosWearActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateDetalleSolicitudPaso(id: Int, paso: Int) {
        RetrofitClient.instance.updateDetalleSolicitudPaso(id, paso).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@PedidoPasosWearActivity, "Error al actualizar el paso", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PedidoPasosWearActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEnvioEstatus(idVenta: Int, nuevoEstatus: String) {
        RetrofitClient.instance.updateEnvioEstatus(idVenta, nuevoEstatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@PedidoPasosWearActivity, "Error al actualizar el estatus de envío", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PedidoPasosWearActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
