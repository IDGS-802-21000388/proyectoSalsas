package com.example.proyecto.apiservice

import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.PasoReceta
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {
    @GET("api/SolicitudesProduccion/{id}")
    fun getSolicitudProduccion(@Path("id") id: Int): Call<SolicitudProduccion>

    @GET("api/PasoReceta/producto/{idProducto}")
    fun getPasosReceta(@Path("idProducto") idProducto: Int): Call<List<PasoReceta>>

    @PUT("api/SolicitudesProduccion/{id}/estatus")
    fun updateSolicitudProduccionEstatus(@Path("id") id: Int, @Body estatus: Int): Call<Void>

    @PUT("api/DetalleSolicitud/{id}/paso")
    fun updateDetalleSolicitudPaso(@Path("id") id: Int, @Body paso: Int): Call<Void>
}
