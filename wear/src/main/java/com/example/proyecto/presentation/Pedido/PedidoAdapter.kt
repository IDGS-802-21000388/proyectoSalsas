package com.example.proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.Usuario
import com.example.proyecto.R

class PedidoAdapter(
    private val solicitudes: List<SolicitudProduccion>,
    private val usuarios: List<Usuario>,  // Lista de usuarios filtrados de tipo 'Empleado'
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(solicitud: SolicitudProduccion)
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        private val spinnerNames: Spinner = itemView.findViewById(R.id.spinnerNames)
        private val statusCircle: View = itemView.findViewById(R.id.statusCircle)

        fun bind(solicitud: SolicitudProduccion) {
            txtNombre.text = solicitud.nombreCliente

            // Mostrar solo los primeros 10 caracteres de la fecha
            txtFecha.text = solicitud.fechaSolicitud?.take(10) ?: ""

            // Configurar el Spinner con los nombres de los usuarios empleados
            val nombresPersonal = usuarios.map { it.nombre }
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, nombresPersonal)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerNames.adapter = adapter

            // Seleccionar el nombre asignado en el Spinner
            val position = nombresPersonal.indexOf(solicitud.nombreAsignado)
            if (position >= 0) {
                spinnerNames.setSelection(position)
            }

            // Cambiar el color del círculo según el estatus
            statusCircle.setBackgroundResource(
                when (solicitud.estatus) {
                    1 -> R.drawable.circle_pending
                    2 -> R.drawable.circle_status
                    3 -> R.drawable.circle_completed
                    else -> R.drawable.circle_pending
                }
            )

            // Listener para el Spinner
            spinnerNames.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    solicitud.nombreAsignado = nombresPersonal[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No hacer nada
                }
            }

            itemView.setOnClickListener {
                itemClickListener.onItemClick(solicitud)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(solicitudes[position])
    }

    override fun getItemCount() = solicitudes.size
}
