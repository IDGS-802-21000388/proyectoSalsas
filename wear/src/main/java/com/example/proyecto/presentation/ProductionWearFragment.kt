package com.example.proyecto.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.PedidoDetalles
import com.example.proyecto.presentation.models.Pedido
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductionWearFragment : Fragment() {

    private lateinit var recyclerView: WearableRecyclerView
    private lateinit var adapter: ProductionWearAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_production_wear, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_pedidos)
        recyclerView.isEdgeItemsCenteringEnabled = true
        recyclerView.layoutManager = WearableLinearLayoutManager(context)

        // Obtener el rol del usuario desde SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "empleado") ?: "empleado"

        // Inicializar el adaptador con la lógica de redirección según el rol
        adapter = ProductionWearAdapter(emptyList()) { pedido ->
            when (rol) {
                "admin" -> {
                    // Redirigir a PedidoDetalles si es admin
                    val intent = Intent(activity, PedidoDetalles::class.java)
                    intent.putExtra("pedido", pedido)
                    startActivity(intent)
                }
                else -> {
                    // Redirigir a PedidoPasosWearActivity si es empleado
                    val intent = Intent(activity, PedidoPasosWearActivity::class.java)
                    intent.putExtra("pedido", pedido)
                    startActivity(intent)
                }
            }
        }
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchPedidos()  // Recargar los pedidos cada vez que el fragmento esté en primer plano
    }

    private fun fetchPedidos() {
        val sharedPref = requireContext().getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val idUsuario = sharedPref.getInt("idUsuario", -1)
        val rol = sharedPref.getString("rol", "empleado") ?: "empleado"

        Log.d("ProductionWearFragment", "Fetching pedidos for user ID: $idUsuario with role: $rol")

        val call = if (rol == "admin") {
            RetrofitClient.instance.obtenerTodosLosPedidos()
        } else {
            RetrofitClient.instance.obtenerPedidos(idUsuario)
        }

        call.enqueue(object : Callback<List<Pedido>> {
            override fun onResponse(call: Call<List<Pedido>>, response: Response<List<Pedido>>) {
                if (response.isSuccessful) {
                    val pedidos = response.body() ?: emptyList()
                    Log.d("ProductionWearFragment", "Pedidos received: $pedidos")
                    adapter.setPedidos(pedidos)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductionWearFragment", "Error response code: ${response.code()}, Error body: $errorBody")
                    Toast.makeText(context, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Pedido>>, t: Throwable) {
                Log.e("ProductionWearFragment", "Error de conexión", t)
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
