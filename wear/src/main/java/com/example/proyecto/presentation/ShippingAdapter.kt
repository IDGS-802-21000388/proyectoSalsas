package com.example.proyecto.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.presentation.models.ShippingModel

class ShippingAdapter(
    private val context: Context,
    private val shippingList: List<ShippingModel>,
    private val updateStatus: (Int) -> Unit
) : RecyclerView.Adapter<ShippingAdapter.ShippingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_shipping_card, parent, false)
        return ShippingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShippingViewHolder, position: Int) {
        val shippingModel = shippingList[position]
        holder.tvNombreCliente.text = shippingModel.nombreCliente
        holder.tvProducto.text = shippingModel.nombreProducto
        holder.tvEstatusEnvio.text = shippingModel.estatusEnvio
        holder.btnActualizarEstatus.tag = shippingModel.idEnvio

        if (shippingModel.estatusEnvio.equals("entregado", ignoreCase = true)) {
            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.secondary_text_color))
            holder.labelNombreCliente.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.labelProducto.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.labelEstatusEnvio.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.tvNombreCliente.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.tvProducto.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.tvEstatusEnvio.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.btnActualizarEstatus.visibility = Button.GONE
        } else {
            holder.btnActualizarEstatus.visibility = Button.VISIBLE
            holder.btnActualizarEstatus.setOnClickListener {
                updateStatus(shippingModel.idEnvio)
            }
        }
    }

    override fun getItemCount(): Int = shippingList.size

    inner class ShippingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreCliente: TextView = view.findViewById(R.id.tvNombreCliente)
        val tvProducto: TextView = view.findViewById(R.id.tvProducto)
        val tvEstatusEnvio: TextView = view.findViewById(R.id.tvEstatusEnvio)
        val btnActualizarEstatus: Button = view.findViewById(R.id.btnActualizarEstatus)
        val cardLayout: LinearLayout = view.findViewById(R.id.cardLayout)
        val labelNombreCliente: TextView = view.findViewById(R.id.labelNombreCliente)
        val labelProducto: TextView = view.findViewById(R.id.labelProducto)
        val labelEstatusEnvio: TextView = view.findViewById(R.id.labelEstatusEnvio)
    }
}
