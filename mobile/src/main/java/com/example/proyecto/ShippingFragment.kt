package com.example.proyecto

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.ShippingModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShippingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchEnvio(view)
    }

    private fun fetchEnvio(view: View) {
        // Contenedor donde se añadirán los CardViews
        val container: ViewGroup = view.findViewById(R.id.container)
        container.removeAllViews()

        // Llamada a la API para obtener el envío
        RetrofitClient.instance.getEnvio().enqueue(object : Callback<ShippingModel> {
            override fun onResponse(call: Call<ShippingModel>, response: Response<ShippingModel>) {
                if (response.isSuccessful) {
                    val envio = response.body()
                    envio?.let {
                        // Inflar el layout del CardView
                        val cardView = layoutInflater.inflate(R.layout.item_shipping, container, false) as CardView

                        // Obtener referencias a los TextView y Button del CardView
                        val tvNombreCliente: TextView = cardView.findViewById(R.id.tvNombreCliente)
                        val tvProducto: TextView = cardView.findViewById(R.id.tvProducto)
                        val tvFechaEnvio: TextView = cardView.findViewById(R.id.tvFechaEnvio)
                        val tvNombrePaqueteria: TextView = cardView.findViewById(R.id.tvNombrePaqueteria)
                        val tvDomicilio: TextView = cardView.findViewById(R.id.tvDomicilio)
                        val tvTotal: TextView = cardView.findViewById(R.id.tvTotal)
                        val tvEstatusEnvio: TextView = cardView.findViewById(R.id.tvEstatusEnvio)
                        val btnActualizarEstatus: Button = cardView.findViewById(R.id.btnActualizarEstatus)

                        // Asignar los datos obtenidos de la API a los TextView
                        tvNombreCliente.text = envio.nombreCliente
                        tvProducto.text = envio.nombreProducto
                        tvFechaEnvio.text = envio.fechaEnvio
                        tvNombrePaqueteria.text = envio.nombrePaqueteria
                        tvDomicilio.text = envio.domicilio
                        tvTotal.text = envio.total.toString()
                        tvEstatusEnvio.text = envio.estatusEnvio

                        // Establecer el idEnvio en el botón usando la propiedad tag
                        btnActualizarEstatus.tag = envio.idEnvio

                        // Configurar el evento del botón
                        btnActualizarEstatus.setOnClickListener { view ->
                            val idEnvio = view.tag as Int
                            actualizarEstatusEntrega(idEnvio, "entregado")
                        }

                        // Desactivar el CardView y ocultar el botón si el estatus es "entregado"
                        if (envio.estatusEnvio == "entregado") {
                            cardView.setCardBackgroundColor(Color.LTGRAY)
                            btnActualizarEstatus.visibility = View.GONE
                        }

                        // Añadir el CardView al contenedor
                        container.addView(cardView)
                    }
                } else {
                    Toast.makeText(context, "Error al obtener el envío", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ShippingModel>, t: Throwable) {
                // Manejar el error
                Toast.makeText(context, "Error al obtener el envío", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarEstatusEntrega(idEnvio: Int, estatus: String) {
        val statusUpdate = mapOf("estatus" to estatus)

        RetrofitClient.instance.updateStatus(idEnvio, statusUpdate).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Estatus actualizado a 'entregado'", Toast.LENGTH_SHORT).show()
                    // Volver a obtener el envío para actualizar la vista
                    fetchEnvio(requireView())
                } else {
                    Toast.makeText(context, "Error al actualizar el estatus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error al actualizar el estatus", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
