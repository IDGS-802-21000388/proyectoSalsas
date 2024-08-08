package com.example.proyecto.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.widget.Toast
import com.example.proyecto.PedidoAdapter

class AssigmentFragment : ComponentActivity(), PedidoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private val pedidos = mutableListOf<SolicitudProduccion>()
    private val empleados = mutableListOf<Usuario>()
    private var idUsuarioSeleccionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_solicitud)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador con listas vacías temporalmente
        pedidoAdapter = PedidoAdapter(pedidos, empleados, this)
        recyclerView.adapter = pedidoAdapter

        fetchEmpleados()  // Llamar primero a la función para obtener los empleados
    }

    private fun fetchEmpleados() {
        val call = RetrofitClient.instance.getUsuarios()
        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful) {
                    empleados.clear()
                    empleados.addAll(response.body()?.filter { it.rol == "Empleado" } ?: emptyList())
                    pedidoAdapter.notifyDataSetChanged()  // Notificar al adaptador de que los datos de empleados se han actualizado
                    fetchPedidos()  // Después de obtener los empleados, ahora se obtienen las solicitudes
                } else {
                    Toast.makeText(this@AssigmentFragment, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(this@AssigmentFragment, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPedidos() {
        val call = RetrofitClient.instance.getSolicitudProduccionAll()
        call.enqueue(object : Callback<List<SolicitudProduccion>> {
            override fun onResponse(call: Call<List<SolicitudProduccion>>, response: Response<List<SolicitudProduccion>>) {
                if (response.isSuccessful) {
                    pedidos.clear()
                    pedidos.addAll(response.body() ?: emptyList())
                    pedidoAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AssigmentFragment, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SolicitudProduccion>>, t: Throwable) {
                Toast.makeText(this@AssigmentFragment, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(pedido: SolicitudProduccion) {
        val nombresEmpleados = empleados.map { it.nombre }

        AlertDialog.Builder(this)
            .setTitle("Asignar a")
            .setItems(nombresEmpleados.toTypedArray()) { _, which ->
                val usuarioSeleccionado = empleados[which]
                idUsuarioSeleccionado = usuarioSeleccionado.idUsuario  // Guarda el ID del usuario seleccionado

                val index = pedidos.indexOf(pedido)
                if (index != -1) {
                    pedidos[index] = pedido.copy(nombreAsignado = usuarioSeleccionado.nombre)
                    pedidoAdapter.notifyItemChanged(index)
                }
            }
            .show()
    }
}
