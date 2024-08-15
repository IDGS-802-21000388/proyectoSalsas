package com.example.proyecto.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.example.proyecto.R
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

class ProductionWearActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productionWearAdapter: PedidoAdapter
    private val solicitudes = mutableListOf<SolicitudProduccion>()
    private val usuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_production_wear)

        recyclerView = findViewById(R.id.recycler_view_pedidos)
        recyclerView.layoutManager = WearableLinearLayoutManager(this)

        fetchUsuarios()
    }

    private fun setupAdapter() {
        val empleados = usuarios.filter { it.rol != "cliente" }

        productionWearAdapter = PedidoAdapter(solicitudes, empleados, object : PedidoAdapter.OnItemClickListener {
            override fun onItemClick(solicitud: SolicitudProduccion) {
                // Aquí manejas el clic en un elemento de la lista
                showAssignDialog(solicitud)
            }
        })

        recyclerView.adapter = productionWearAdapter
    }

    private fun showAssignDialog(solicitud: SolicitudProduccion) {
        val empleados = usuarios.filter { it.rol != "cliente" }
        val nombresEmpleados = empleados.map { it.nombre }

        AlertDialog.Builder(this)
            .setTitle("Asignar a")
            .setItems(nombresEmpleados.toTypedArray()) { _, which ->
                val usuarioSeleccionado = empleados[which]

                val updatedSolicitud = solicitud.copy(
                    idUsuario = usuarioSeleccionado.idUsuario,
                    nombreAsignado = usuarioSeleccionado.nombre,
                    nombreProducto = solicitud.nombreProducto ?: "Producto Desconocido" // Valor por defecto
                )

                updateSolicitud(updatedSolicitud)

                registrarDetalleSolicitud(updatedSolicitud, usuarioSeleccionado)
            }
            .show()
    }

    private fun updateSolicitud(updatedSolicitud: SolicitudProduccion) {
        val index = solicitudes.indexOfFirst { it.idSolicitud == updatedSolicitud.idSolicitud }
        if (index != -1) {
            solicitudes[index] = updatedSolicitud
            productionWearAdapter.notifyItemChanged(index)
        }
    }

    private fun fetchUsuarios() {
        val call = RetrofitClient.instance.getUsuarios()
        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful) {
                    usuarios.clear()
                    usuarios.addAll(response.body() ?: emptyList())

                    fetchSolicitudes() // Llama a fetchSolicitudes después de obtener usuarios
                } else {
                    Toast.makeText(this@ProductionWearActivity, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(this@ProductionWearActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                        solicitudes.add(
                            solicitud.copy(
                                nombreCliente = nombreCliente,
                                nombreProducto = solicitud.nombreProducto ?: "Producto Desconocido", // Valor por defecto si es null
                                detalleSolicituds = solicitud.detalleSolicituds ?: emptyList() // Valor por defecto si es null
                            )
                        )
                    }

                    setupAdapter() // Configura el adaptador después de obtener solicitudes
                } else {
                    Toast.makeText(this@ProductionWearActivity, "Error al obtener datos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<SolicitudProduccion>>, t: Throwable) {
                Toast.makeText(this@ProductionWearActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
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
                    Toast.makeText(this@ProductionWearActivity, "Error al verificar detalles de solicitud", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<DetalleSolicitud>>, t: Throwable) {
                Toast.makeText(this@ProductionWearActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun postDetalleSolicitud(detalleSolicitud: DetalleSolicitud) {
        val call = RetrofitClient.instance.postDetalleSolicitud(detalleSolicitud)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductionWearActivity, "Detalle de solicitud registrado con éxito", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@ProductionWearActivity, "Error al registrar detalle de solicitud", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductionWearActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateDetalleSolicitudUsuario(idDetalleSolicitud: Int, idUsuario: Int) {
        val call = RetrofitClient.instance.updateDetalleSolicitudUsuario(idDetalleSolicitud, idUsuario)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductionWearActivity, "Usuario asignado correctamente", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@ProductionWearActivity, "Error al actualizar usuario", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProductionWearActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
