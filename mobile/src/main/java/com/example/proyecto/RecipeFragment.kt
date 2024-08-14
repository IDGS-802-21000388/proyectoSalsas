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

class RecipeFragment : Fragment() {

    private lateinit var orderTitle: TextView
    private lateinit var customerName: TextView
    private lateinit var orderDetails: TextView
    private lateinit var statusPoint: ImageView
    private lateinit var instructions: TextView
    private lateinit var nextButton: Button

    private var currentStep = 0
    private var currentProductIndex = 0
    private var steps: List<PasoReceta> = listOf()
    private var productos: List<DetalleVenta> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe, container, false)
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

        // Obtener el pedido pasado como argumento
        val pedido = arguments?.getParcelable<Pedido>("pedido")

        if (pedido != null) {
            // Actualizar el estatus a 2 (En Progreso) al iniciar la receta
            updateSolicitudProduccionEstatus(pedido.idSolicitud, 2)
            setupUI(pedido)
        } else {
            Log.e("RecipeFragment", "No se recibió un pedido válido.")
        }

        // Configurar el botón de siguiente
        nextButton.setOnClickListener {
            if (currentStep < steps.size - 1) {
                val nextStep = currentStep + 1
                updateDetalleSolicitudPaso(pedido!!.detallesProduccion.firstOrNull()?.idDetalleSolicitud ?: 0, nextStep + 1)
                currentStep = nextStep
                showStep(currentStep)
                if (currentStep == steps.size - 1 && currentProductIndex == productos.size - 1) {
                    nextButton.text = "Finalizar"
                }
            } else if (currentProductIndex < productos.size - 1) {
                // Avanzar al siguiente producto
                currentProductIndex++
                currentStep = 0
                showProductDetails(productos[currentProductIndex])
                obtenerPasosReceta(productos[currentProductIndex].producto.idProducto)
            } else {
                // Completar el pedido
                statusPoint.setImageResource(R.drawable.circle_completed)
                updateSolicitudProduccionEstatus(pedido!!.idSolicitud, 3, pedido.venta?.idVenta ?: 0)
            }
        }
    }

    private fun setupUI(pedido: Pedido) {
        orderTitle.text = "Pedido de ${pedido.usuarioCliente}"
        customerName.text = pedido.detallesProduccion.firstOrNull()?.usuarioProduccion ?: "Desconocido"

        // Obtener los productos de la venta
        productos = pedido.venta?.detalleVenta ?: listOf()
        if (productos.isNotEmpty()) {
            showProductDetails(productos[currentProductIndex])
            obtenerPasosReceta(productos[currentProductIndex].producto.idProducto)
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
                        currentStep = 0
                        showStep(currentStep)
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

    // Función para actualizar el estatus de producción
    private fun updateSolicitudProduccionEstatus(id: Int, estatus: Int, idVenta: Int = -1) {
        RetrofitClient.instance.updateSolicitudProduccionEstatus(id, estatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Log.e("RecipeFragment", "Error al actualizar el estatus de la solicitud: ${response.errorBody()}")
                } else if (estatus == 3 && idVenta != -1) {
                    updateEnvioEstatus(idVenta, "pendiente de envío")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RecipeFragment", "Error al actualizar el estatus de la solicitud", t)
            }
        })
    }

    // Función para actualizar el estatus del envío
    private fun updateEnvioEstatus(idVenta: Int, estatus: String) {
        RetrofitClient.instance.updateEnvioEstatus(idVenta, estatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Log.e("RecipeFragment", "Error al actualizar el estatus de envío: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RecipeFragment", "Error al actualizar el estatus de envío", t)
            }
        })
    }

    private fun updateDetalleSolicitudPaso(id: Int, paso: Int) {
        RetrofitClient.instance.updateDetalleSolicitudPaso(id, paso).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("RecipeFragment", "Paso actualizado a $paso en el servidor")
                } else {
                    Log.e("RecipeFragment", "Error al actualizar el paso del detalle de solicitud: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RecipeFragment", "Error al actualizar el paso del detalle de solicitud", t)
            }
        })
    }
}
