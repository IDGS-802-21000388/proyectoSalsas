package com.example.proyecto.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.presentation.models.Pedido
import java.text.SimpleDateFormat
import java.util.Locale

class ProductionWearAdapter(
    private var pedidos: List<Pedido>,
    private val onPedidoClick: (Pedido) -> Unit
) : RecyclerView.Adapter<ProductionWearAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedidoproduccionwear, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int = pedidos.size


    fun setPedidos(nuevosPedidos: List<Pedido>) {
        this.pedidos = nuevosPedidos
        notifyDataSetChanged()  // Notificar al RecyclerView que los datos han cambiado
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clienteTextView: TextView = itemView.findViewById(R.id.clienteTextView)
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        private val asignarATextView: TextView = itemView.findViewById(R.id.asignarATextView)
        private val estatusTextView: TextView = itemView.findViewById(R.id.estatusTextView)
        private val estatusIndicator: ImageView = itemView.findViewById(R.id.estatusIndicator)

        fun bind(pedido: Pedido) {
            clienteTextView.text = pedido.usuarioCliente
            fechaTextView.text = formatFecha(pedido.fechaSolicitud)
            asignarATextView.text = "Asignado a: ${pedido.detallesProduccion.firstOrNull()?.usuarioProduccion ?: "No asignado"}"
            when (pedido.estatus) {
                1 -> {
                    estatusTextView.text = "Pendiente"
                    estatusIndicator.setImageResource(R.drawable.circle_pending)
                }
                2 -> {
                    estatusTextView.text = "En Progreso"
                    estatusIndicator.setImageResource(R.drawable.circle_in_progress)
                }
                3 -> {
                    estatusTextView.text = "Completado"
                    estatusIndicator.setImageResource(R.drawable.circle_completed)
                }
            }

            itemView.setOnClickListener {
                onPedidoClick(pedido)
            }
        }

        private fun formatFecha(fecha: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return try {
                val date = inputFormat.parse(fecha)
                outputFormat.format(date)
            } catch (e: Exception) {
                fecha
            }
        }
    }
}
