package com.example.proyecto

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssigmentFragment : Fragment(), PedidoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private val solicitudes = mutableListOf<SolicitudProduccion>()
    private val usuarios = mutableListOf<Usuario>()
    private val nombresClientes = mutableListOf<String>()
    private var idUsuarioSeleccionado: Int? = null
    private var solicitudId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_assigment, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        pedidoAdapter = PedidoAdapter(solicitudes, this)
        recyclerView.adapter = pedidoAdapter

        // Primero, obtener los usuarios
        fetchUsuarios()

        return view
    }

    private fun fetchSolicitudes() {
        val call = RetrofitClient.instance.getSolicitudProduccionAll()
        call.enqueue(object : Callback<List<SolicitudProduccion>> {
            override fun onResponse(call: Call<List<SolicitudProduccion>>, response: Response<List<SolicitudProduccion>>) {
                if (response.isSuccessful) {
                    solicitudes.clear()
                    val solicitudesList = response.body() ?: emptyList()
                    solicitudesList.forEachIndexed { index, solicitud ->
                        // Asignar el nombre del cliente según el índice
                        val nombreCliente = if (index < nombresClientes.size) nombresClientes[index] else "Cliente Desconocido"
                        solicitudes.add(solicitud.copy(nombreCliente = nombreCliente))

                        // Guardar el idSolicitud de la primera solicitud (o cualquier otra condición que prefieras)
                        if (index == 0) {
                            solicitudId = solicitud.idSolicitud
                        }
                    }
                    pedidoAdapter.notifyDataSetChanged()

                    // Usar el idSolicitud almacenado
                    solicitudId?.let {
                        Log.d("fetchSolicitudes", "El ID de la primera solicitud es: $it")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al obtener datos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<SolicitudProduccion>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchUsuarios() {
        val call = RetrofitClient.instance.getUsuarios()
        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful) {
                    usuarios.clear()
                    usuarios.addAll(response.body() ?: emptyList())
                    val clientes = usuarios.filter { it.rol == "cliente" }
                    nombresClientes.clear()
                    nombresClientes.addAll(clientes.map { it.nombre })

                    // Después de obtener los usuarios, obtener las solicitudes
                    fetchSolicitudes()
                } else {
                    Toast.makeText(requireContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(solicitud: SolicitudProduccion) {
        val empleados = usuarios.filter { it.rol != "cliente" }
        val nombresEmpleados = empleados.map { it.nombre }

        AlertDialog.Builder(requireContext())
            .setTitle("Asignar a")
            .setItems(nombresEmpleados.toTypedArray()) { _, which ->
                val usuarioSeleccionado = empleados[which]
                idUsuarioSeleccionado = usuarioSeleccionado.idUsuario

                val index = solicitudes.indexOf(solicitud)
                if (index != -1) {
                    solicitudes[index] = solicitud.copy(
                        idUsuario = usuarioSeleccionado.idUsuario,
                        nombreAsignado = usuarioSeleccionado.nombre // Actualiza el nombre asignado
                    )
                    pedidoAdapter.notifyItemChanged(index)
                }
            }
            .show()
    }
}
