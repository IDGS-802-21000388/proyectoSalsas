package com.example.proyecto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.models.Pedido
import java.text.SimpleDateFormat
import java.util.Locale

class ProduccionAdapter(
    private val context: Context,
    private var pedidos: List<Pedido>,
    private val onPedidoClick: (Pedido) -> Unit
) : RecyclerView.Adapter<ProduccionAdapter.ProduccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProduccionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pedidoproduccion, parent, false)
        return ProduccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProduccionViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    fun setPedidos(pedidos: List<Pedido>) {
        this.pedidos = pedidos
        notifyDataSetChanged()
    }

    inner class ProduccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clienteTextView: TextView = itemView.findViewById(R.id.clienteTextView)
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        private val asignarATextView: TextView = itemView.findViewById(R.id.asignarATextView)
        private val estatusTextView: TextView = itemView.findViewById(R.id.estatusTextView)
        private val estatusIndicator: View = itemView.findViewById(R.id.estatusIndicator)

        fun bind(pedido: Pedido) {
            clienteTextView.text = pedido.usuarioCliente
            fechaTextView.text = formatFecha(pedido.fechaSolicitud)
            asignarATextView.text = pedido.detallesProduccion.firstOrNull()?.usuarioProduccion ?: "No Asignado"

            when (pedido.estatus) {
                1 -> {
                    estatusTextView.text = "Pendiente"
                    estatusIndicator.setBackgroundResource(R.drawable.circle_pending)
                }
                2 -> {
                    estatusTextView.text = "En Progreso"
                    estatusIndicator.setBackgroundResource(R.drawable.circle_in_progress)
                }
                3 -> {
                    estatusTextView.text = "Completado"
                    estatusIndicator.setBackgroundResource(R.drawable.circle_completed)
                }
            }

            // Configurar el click listener
            itemView.setOnClickListener {
                onPedidoClick(pedido)
            }
        }
    }

    private fun formatFecha(fecha: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(fecha)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            fecha
        }
    }
}
