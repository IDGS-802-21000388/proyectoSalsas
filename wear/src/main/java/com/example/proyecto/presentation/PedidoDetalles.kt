package com.example.proyecto

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.presentation.models.DetalleVenta
import com.example.proyecto.presentation.models.PasoReceta
import com.example.proyecto.presentation.models.Pedido
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PedidoDetalles : AppCompatActivity() {

    private lateinit var orderTitle: TextView
    private lateinit var customerName: TextView
    private lateinit var orderDetails: TextView
    private lateinit var instructions: TextView
    private lateinit var statusPoint: ImageView

    private var steps: List<PasoReceta> = listOf()
    private var currentStepIndex = 0
    private var products: List<DetalleVenta> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detalles)

        // Inicializar vistas
        orderTitle = findViewById(R.id.orderTitle)
        customerName = findViewById(R.id.customerName)
        orderDetails = findViewById(R.id.orderDetails)
        instructions = findViewById(R.id.instructions)
        statusPoint = findViewById(R.id.statusPoint)

        val pedido = intent.getParcelableExtra<Pedido>("pedido")

        // Actualizar la UI con los detalles del Pedido
        pedido?.let {
            orderTitle.text = "Pedido de ${it.usuarioCliente}"
            customerName.text = it.detallesProduccion.firstOrNull()?.usuarioProduccion ?: "Desconocido"
            products = it.venta?.detalleVenta ?: listOf()

            // Cargar el primer producto
            if (products.isNotEmpty()) {
                loadProductSteps(products[0]) // Solo carga el primer producto
            }
        }
    }

    private fun loadProductSteps(producto: DetalleVenta) {
        // Actualizar los detalles del pedido
        orderDetails.text = "${producto.cantidad} Litros de ${producto.producto.nombreProducto}"

        // Actualizar el estado visual
        statusPoint.setImageResource(R.drawable.circle_in_progress)

        // Obtener los pasos de la receta
        RetrofitClient.instance.getPasosReceta(producto.producto.idProducto).enqueue(object : Callback<List<PasoReceta>> {
            override fun onResponse(call: Call<List<PasoReceta>>, response: Response<List<PasoReceta>>) {
                if (response.isSuccessful) {
                    steps = response.body() ?: listOf()
                    if (steps.isNotEmpty()) {
                        // Mostrar el paso actual
                        showCurrentStep()
                    }
                }
            }

            override fun onFailure(call: Call<List<PasoReceta>>, t: Throwable) {
                Toast.makeText(this@PedidoDetalles, "Error al obtener los pasos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showCurrentStep() {
        if (steps.isNotEmpty()) {
            val currentStep = steps[currentStepIndex]
            instructions.text = "${currentStep.paso}. ${currentStep.descripcion}"

            // Actualizar el estado visual según el paso actual
            statusPoint.setImageResource(R.drawable.circle_in_progress) // Puedes cambiar a otro drawable si es necesario

            // Aquí puedes agregar lógica adicional si necesitas actualizar el estado de la solicitud o hacer algo más
        } else {
            // Si no hay pasos, muestra un mensaje o cambia el estado visual
            instructions.text = "No hay pasos disponibles."
            statusPoint.setImageResource(R.drawable.circle_pending) // Un drawable que indique que no hay pasos
        }
    }
}
