package com.example.proyecto.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.Usuario
import java.text.SimpleDateFormat
import java.util.Locale

class PedidoAdapter(
    private var solicitudes: List<SolicitudProduccion>,
    private val usuarios: List<Usuario>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(solicitud: SolicitudProduccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val solicitud = solicitudes[position]
        holder.bind(solicitud)
    }

    override fun getItemCount(): Int = solicitudes.size

    fun setSolicitudes(nuevasSolicitudes: List<SolicitudProduccion>) {
        this.solicitudes = nuevasSolicitudes
        notifyDataSetChanged()
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.clienteTextView)
        private val txtFecha: TextView = itemView.findViewById(R.id.fechaTextView)
        private val spinnerNames: Spinner = itemView.findViewById(R.id.asignarATextView)
        private val estatusIndicator: ImageView = itemView.findViewById(R.id.estatusIndicator)

        fun bind(solicitud: SolicitudProduccion) {
            txtNombre.text = solicitud.nombreCliente
            txtFecha.text = formatFecha(solicitud.fechaSolicitud)

            val nombresPersonal = usuarios.map { it.nombre }
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, nombresPersonal)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerNames.adapter = adapter

            val position = nombresPersonal.indexOf(solicitud.nombreAsignado)
            if (position >= 0) {
                spinnerNames.setSelection(position)
            }

            when (solicitud.estatus) {
                1 -> estatusIndicator.setImageResource(R.drawable.circle_pending)
                2 -> estatusIndicator.setImageResource(R.drawable.circle_status)
                3 -> estatusIndicator.setImageResource(R.drawable.circle_completed)
                else -> estatusIndicator.setImageResource(R.drawable.circle_pending)
            }

            spinnerNames.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    solicitud.nombreAsignado = nombresPersonal[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }


            itemView.setOnClickListener {
                onItemClickListener.onItemClick(solicitud)
            }
        }

        private fun formatFecha(fecha: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return try {
                val date = fecha?.let { inputFormat.parse(it) }
                outputFormat.format(date ?: return fecha ?: "")
            } catch (e: Exception) {
                fecha ?: ""
            }
        }
    }
}
