package com.example.proyecto.models

import android.os.Parcel
import android.os.Parcelable

data class Venta(
    val idVenta: Int,
    val fechaVenta: String,
    val total: Double,
    val detalleVenta: List<DetalleVenta>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.createTypedArrayList(DetalleVenta.CREATOR) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idVenta)
        parcel.writeString(fechaVenta)
        parcel.writeDouble(total)
        parcel.writeTypedList(detalleVenta)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Venta> {
        override fun createFromParcel(parcel: Parcel): Venta {
            return Venta(parcel)
        }

        override fun newArray(size: Int): Array<Venta?> {
            return arrayOfNulls(size)
        }
    }
}
