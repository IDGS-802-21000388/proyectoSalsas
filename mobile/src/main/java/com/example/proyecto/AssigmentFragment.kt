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
import com.example.proyecto.models.DetalleSolicitud
import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        val view = inflater.inflate(R.layout.fragment_assigment, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        pedidoAdapter = PedidoAdapter(solicitudes, this)
        recyclerView.adapter = pedidoAdapter

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

                    val clientesMap = usuarios.filter { it.rol == "cliente" }
                        .associateBy { it.idUsuario }

                    solicitudesList.forEach { solicitud ->
                        val nombreCliente = clientesMap[solicitud.idUsuario]?.nombre ?: "Cliente Desconocido"
                        solicitudes.add(solicitud.copy(nombreCliente = nombreCliente))
                    }

                    pedidoAdapter.notifyDataSetChanged()

                    solicitudId = solicitudes.firstOrNull()?.idSolicitud
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
                        nombreAsignado = usuarioSeleccionado.nombre
                    )
                    pedidoAdapter.notifyItemChanged(index)

                    registrarDetalleSolicitud(solicitud, usuarioSeleccionado)
                }
            }
            .show()
    }

    private fun registrarDetalleSolicitud(solicitud: SolicitudProduccion, usuario: Usuario) {
        val call = RetrofitClient.instance.getDetalle()
        call.enqueue(object : Callback<List<DetalleSolicitud>> {
            override fun onResponse(call: Call<List<DetalleSolicitud>>, response: Response<List<DetalleSolicitud>>) {
                if (response.isSuccessful) {
                    val detalles = response.body() ?: emptyList()

                    val detalleExistente = detalles.find { it.idDetalleSolicitud == solicitud.idSolicitud }

                    if (detalleExistente != null) {
                        updateDetalleSolicitudUsuario(detalleExistente.idDetalleSolicitud, usuario.idUsuario)
                    } else {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val fechaActual = LocalDate.now().format(formatter)

                        val nuevoDetalle = DetalleSolicitud(
                            idDetalleSolicitud = 0,
                            idSolicitud = solicitud.idSolicitud,
                            fechaInicio = fechaActual,
                            fechaFin = fechaActual,
                            idUsuario = usuario.idUsuario,
                            nombreUsuario = usuario.nombre,
                            estatus = true,
                            numeroPaso = 1
                        )

                        postDetalleSolicitud(nuevoDetalle)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al verificar detalles de solicitud", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<DetalleSolicitud>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun postDetalleSolicitud(detalleSolicitud: DetalleSolicitud) {
        val call = RetrofitClient.instance.postDetalleSolicitud(detalleSolicitud)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Detalle de solicitud registrado con éxito", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error al registrar detalle de solicitud", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateDetalleSolicitudUsuario(idDetalleSolicitud: Int, idUsuario: Int) {
        val call = RetrofitClient.instance.updateDetalleSolicitudUsuario(idDetalleSolicitud, idUsuario)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Usuario asignado correctamente", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar usuario", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


}
