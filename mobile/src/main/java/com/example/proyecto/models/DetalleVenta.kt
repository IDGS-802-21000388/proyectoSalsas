package com.example.proyecto.models

import android.os.Parcel
import android.os.Parcelable

data class DetalleVenta(
    val idDetalleVenta: Int,
    val cantidad: Double,
    val subtotal: Double,
    val producto: Producto
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readParcelable(Producto::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idDetalleVenta)
        parcel.writeDouble(cantidad)
        parcel.writeDouble(subtotal)
        parcel.writeParcelable(producto, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DetalleVenta> {
        override fun createFromParcel(parcel: Parcel): DetalleVenta {
            return DetalleVenta(parcel)
        }

        override fun newArray(size: Int): Array<DetalleVenta?> {
            return arrayOfNulls(size)
        }
    }
}
