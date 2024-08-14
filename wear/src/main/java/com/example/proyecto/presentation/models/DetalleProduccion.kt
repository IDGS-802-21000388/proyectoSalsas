package com.example.proyecto.presentation.models

import android.os.Parcel
import android.os.Parcelable

data class DetalleProduccion(
    val idDetalleSolicitud: Int,
    val fechaInicio: String,
    val fechaFin: String?,
    val estatus: Boolean,
    val numeroPaso: Int,
    val usuarioProduccion: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idDetalleSolicitud)
        parcel.writeString(fechaInicio)
        parcel.writeString(fechaFin)
        parcel.writeByte(if (estatus) 1 else 0)
        parcel.writeInt(numeroPaso)
        parcel.writeString(usuarioProduccion)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DetalleProduccion> {
        override fun createFromParcel(parcel: Parcel): DetalleProduccion {
            return DetalleProduccion(parcel)
        }

        override fun newArray(size: Int): Array<DetalleProduccion?> {
            return arrayOfNulls(size)
        }
    }
}
