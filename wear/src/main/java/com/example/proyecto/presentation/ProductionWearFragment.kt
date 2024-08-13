package com.example.proyecto.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
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

        // Inicializamos el adaptador con una lista vacía para que la RecyclerView no esté vacía mientras se cargan los datos
        adapter = ProductionWearAdapter(emptyList()) { pedido ->
            val intent = Intent(activity, PedidoPasosWearActivity::class.java)
            intent.putExtra("pedido", pedido)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchPedidos()  // Recargar los pedidos cada vez que el fragmento esté en primer plano
    }

    private fun fetchPedidos() {
        // Recuperar el idUsuario de las SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val idUsuario = sharedPref.getInt("idUsuario", -1)

        RetrofitClient.instance.obtenerPedidos(idUsuario).enqueue(object : Callback<List<Pedido>> {
            override fun onResponse(call: Call<List<Pedido>>, response: Response<List<Pedido>>) {
                if (response.isSuccessful) {
                    val pedidos = response.body() ?: emptyList()
                    adapter.setPedidos(pedidos)  // Actualizar los datos en el adaptador
                } else {
                    Toast.makeText(context, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Pedido>>, t: Throwable) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
