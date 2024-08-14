package com.example.proyecto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.DetalleVenta
import com.example.proyecto.models.PasoReceta
import com.example.proyecto.models.Pedido
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisibilityFragment : Fragment() {

    private lateinit var orderTitle: TextView
    private lateinit var customerName: TextView
    private lateinit var orderDetails: TextView
    private lateinit var statusPoint: ImageView
    private lateinit var instructions: TextView
    private lateinit var nextButton: Button

    private var currentStep = 0
    private var steps: List<PasoReceta> = listOf()
    private var productos: List<DetalleVenta> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_visibility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar las vistas
        orderTitle = view.findViewById(R.id.order_title)
        customerName = view.findViewById(R.id.customer_name)
        orderDetails = view.findViewById(R.id.order_details)
        statusPoint = view.findViewById(R.id.status_point)
        instructions = view.findViewById(R.id.instructions)
        nextButton = view.findViewById(R.id.next_button)

        // Hacer que el botón "Siguiente" esté oculto
        nextButton.visibility = View.GONE

        // Obtener el pedido pasado como argumento
        val pedido = arguments?.getParcelable<Pedido>("pedido")

        if (pedido != null) {
            setupUI(pedido)
        } else {
            Log.e("RecipeFragment", "No se recibió un pedido válido.")
        }
    }

    private fun setupUI(pedido: Pedido) {
        orderTitle.text = "Pedido de ${pedido.usuarioCliente}"
        customerName.text = pedido.detallesProduccion.firstOrNull()?.usuarioProduccion ?: "Desconocido"

        // Obtener los productos de la venta
        productos = pedido.venta?.detalleVenta ?: listOf()
        if (productos.isNotEmpty()) {
            showProductDetails(productos[0])
            obtenerPasosReceta(productos[0].producto.idProducto)
        } else {
            Log.e("RecipeFragment", "No se encontraron productos en el pedido.")
        }
    }

    private fun showProductDetails(detalleVenta: DetalleVenta) {
        orderDetails.text = "${detalleVenta.cantidad} Litros de ${detalleVenta.producto.nombreProducto}"
        statusPoint.setImageResource(R.drawable.circle_in_progress)
    }

    private fun obtenerPasosReceta(idProducto: Int) {
        RetrofitClient.instance.getPasosReceta(idProducto).enqueue(object : Callback<List<PasoReceta>> {
            override fun onResponse(call: Call<List<PasoReceta>>, response: Response<List<PasoReceta>>) {
                if (response.isSuccessful) {
                    steps = response.body() ?: listOf()
                    if (steps.isNotEmpty()) {
                        showStep(0) // Muestra solo el primer paso
                    }
                } else {
                    Log.e("RecipeFragment", "Error en la respuesta de pasos de receta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<PasoReceta>>, t: Throwable) {
                Log.e("RecipeFragment", "Error al obtener pasos de receta", t)
            }
        })
    }

    private fun showStep(stepIndex: Int) {
        val step = steps[stepIndex]
        instructions.text = "${step.paso}. ${step.descripcion}"
    }
}
