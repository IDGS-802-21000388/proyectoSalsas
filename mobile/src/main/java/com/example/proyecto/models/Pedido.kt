package com.example.proyecto.models

import android.os.Parcel
import android.os.Parcelable

data class Pedido(
    val idSolicitud: Int,
    val fechaSolicitud: String,
    val estatus: Int,
    val venta: Venta?,
    val usuarioCliente: String,
    val detallesProduccion: List<DetalleProduccion>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readParcelable(Venta::class.java.classLoader),
        parcel.readString() ?: "",
        parcel.createTypedArrayList(DetalleProduccion.CREATOR) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idSolicitud)
        parcel.writeString(fechaSolicitud)
        parcel.writeInt(estatus)
        parcel.writeParcelable(venta, flags)
        parcel.writeString(usuarioCliente)
        parcel.writeTypedList(detallesProduccion)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pedido> {
        override fun createFromParcel(parcel: Parcel): Pedido {
            return Pedido(parcel)
        }

        override fun newArray(size: Int): Array<Pedido?> {
            return arrayOfNulls(size)
        }
    }
}
