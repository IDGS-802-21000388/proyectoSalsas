package com.example.proyecto.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.presentation.models.ShippingModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShippingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shippingAdapter: ShippingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadShippingData()
    }

    private fun loadShippingData() {
        RetrofitClient.instance.getEnvio().enqueue(object : Callback<List<ShippingModel>> {
            override fun onResponse(call: Call<List<ShippingModel>>, response: Response<List<ShippingModel>>) {
                if (response.isSuccessful) {
                    val shippingList = response.body()
                    if (shippingList != null) {
                        shippingAdapter = ShippingAdapter(this@ShippingActivity, shippingList) { idEnvio ->
                            updateShippingStatus(idEnvio)
                        }
                        recyclerView.adapter = shippingAdapter
                    }
                } else {
                    Toast.makeText(this@ShippingActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ShippingModel>>, t: Throwable) {
                Toast.makeText(this@ShippingActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateShippingStatus(idEnvio: Int) {
        val statusMap = mapOf("estatus" to "entregado")
        RetrofitClient.instance.updateStatus(idEnvio, statusMap).enqueue(object : Callback<ResponseBody> {
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
