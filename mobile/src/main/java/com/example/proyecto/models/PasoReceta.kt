package com.example.proyecto.models

import android.os.Parcel
import android.os.Parcelable

data class PasoReceta(
    val idPasoReceta: Int,
    val paso: Int,
    val descripcion: String,
    val idProducto: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPasoReceta)
        parcel.writeInt(paso)
        parcel.writeString(descripcion)
        parcel.writeInt(idProducto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PasoReceta> {
        override fun createFromParcel(parcel: Parcel): PasoReceta {
            return PasoReceta(parcel)
        }

        override fun newArray(size: Int): Array<PasoReceta?> {
            return arrayOfNulls(size)
        }
    }
}
