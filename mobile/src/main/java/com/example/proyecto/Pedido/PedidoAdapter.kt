package com.example.proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.models.SolicitudProduccion

class PedidoAdapter(
    private val solicitudes: List<SolicitudProduccion>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(solicitud: SolicitudProduccion)
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtAsignarA: TextView = itemView.findViewById(R.id.txtAsignarA)
        val txtEstatus: TextView = itemView.findViewById(R.id.txtEstatus)
        val statusCircle: View = itemView.findViewById(R.id.statusCircle)

        fun bind(solicitud: SolicitudProduccion) {
            txtCliente.text = solicitud.nombreCliente

            // Mostrar solo los primeros 10 caracteres de la fecha
            txtFecha.text = solicitud.fechaSolicitud?.take(10) ?: ""
            txtEstatus.text = when (solicitud.estatus) {
                1 -> "Pendiente"
                2 -> "En Proceso"
                3 -> "Completado"
                else -> "Desconocido"
            }

            // Cambiar el color del círculo según el estatus
            statusCircle.setBackgroundResource(
                when (solicitud.estatus) {
                    1 -> R.drawable.circle_pending
                    2 -> R.drawable.circle_in_progress
                    3 -> R.drawable.circle_completed
                    else -> R.drawable.circle_pending
                }
            )

            // Mostrar el nombre del asignado o "Asignar a" si no está asignado
            txtAsignarA.text = solicitud.nombreAsignado ?: "Asignar a"

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
