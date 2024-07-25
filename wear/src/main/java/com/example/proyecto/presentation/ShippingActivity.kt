package com.example.proyecto.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.presentation.models.ShippingModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShippingActivity : AppCompatActivity() {

    private lateinit var tvNombreCliente: TextView
    private lateinit var tvProducto: TextView
    private lateinit var tvEstatusEnvio: TextView
    private lateinit var btnActualizarEstatus: Button
    private lateinit var cardLayout: LinearLayout
    private lateinit var labelNombreCliente: TextView
    private lateinit var labelProducto: TextView
    private lateinit var labelEstatusEnvio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping)

        tvNombreCliente = findViewById(R.id.tvNombreCliente)
        tvProducto = findViewById(R.id.tvProducto)
        tvEstatusEnvio = findViewById(R.id.tvEstatusEnvio)
        btnActualizarEstatus = findViewById(R.id.btnActualizarEstatus)
        cardLayout = findViewById(R.id.cardLayout)
        labelNombreCliente = findViewById(R.id.labelNombreCliente)
        labelProducto = findViewById(R.id.labelProducto)
        labelEstatusEnvio = findViewById(R.id.labelEstatusEnvio)

        loadShippingData()

        btnActualizarEstatus.setOnClickListener {
            updateShippingStatus()
        }
    }

    private fun loadShippingData() {
        RetrofitClient.instance.getEnvio().enqueue(object : Callback<ShippingModel> {
            override fun onResponse(call: Call<ShippingModel>, response: Response<ShippingModel>) {
                if (response.isSuccessful) {
                    val shippingModel = response.body()
                    shippingModel?.let {
                        tvNombreCliente.text = it.nombreCliente
                        tvProducto.text = it.nombreProducto
                        tvEstatusEnvio.text = it.estatusEnvio
                        btnActualizarEstatus.tag = it.idEnvio

                        if (it.estatusEnvio.equals("entregado", ignoreCase = true)) {
                            cardLayout.setBackgroundColor(ContextCompat.getColor(this@ShippingActivity, R.color.secondary_text_color))
                            labelNombreCliente.setTextColor(ContextCompat.getColor(this@ShippingActivity, android.R.color.black))
                            labelProducto.setTextColor(ContextCompat.getColor(this@ShippingActivity, android.R.color.black))
                            labelEstatusEnvio.setTextColor(ContextCompat.getColor(this@ShippingActivity, android.R.color.black))
                            tvNombreCliente.setTextColor(ContextCompat.getColor(this@ShippingActivity, android.R.color.black))
                            tvProducto.setTextColor(ContextCompat.getColor(this@ShippingActivity, android.R.color.black))
                            tvEstatusEnvio.setTextColor(ContextCompat.getColor(this@ShippingActivity, android.R.color.black))
                            btnActualizarEstatus.visibility = Button.GONE
                        }
                    }
                } else {
                    Toast.makeText(this@ShippingActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ShippingModel>, t: Throwable) {
                Toast.makeText(this@ShippingActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateShippingStatus() {
        val idEnvio = btnActualizarEstatus.tag as? Int
        idEnvio?.let {
            val statusMap = mapOf("estatus" to "entregado")
            RetrofitClient.instance.updateStatus(it, statusMap).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ShippingActivity, "Estatus actualizado", Toast.LENGTH_SHORT).show()
                        loadShippingData()
                    } else {
                        Toast.makeText(this@ShippingActivity, "Error al actualizar estatus", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ShippingActivity, "Error al actualizar estatus", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
