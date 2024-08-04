package com.example.proyecto.apiservice

import com.example.proyecto.models.LoginModel
import com.example.proyecto.models.SolicitudProduccion
import com.example.proyecto.models.PasoReceta
import com.example.proyecto.models.RegistroModel
import com.example.proyecto.models.ShippingModel
import com.example.proyecto.models.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {
    @GET("api/SolicitudesProduccion/{id}")
    fun getSolicitudProduccion(@Path("id") id: Int): Call<SolicitudProduccion>

    @GET("api/SolicitudesProduccion")
    fun getSolicitudProduccionAll(): Call<List<SolicitudProduccion>>

    @GET("api/Usuarios")
    fun getUsuarios(): Call<List<Usuario>>

    @GET("api/PasoReceta/producto/{idProducto}")
    fun getPasosReceta(@Path("idProducto") idProducto: Int): Call<List<PasoReceta>>

    @PUT("api/SolicitudesProduccion/{id}/estatus")
    fun updateSolicitudProduccionEstatus(@Path("id") id: Int, @Body estatus: Int): Call<Void>

    @PUT("api/DetalleSolicitud/{id}/paso")
    fun updateDetalleSolicitudPaso(@Path("id") id: Int, @Body paso: Int): Call<Void>

    @POST("api/Login/login")
    fun postLogin(@Body params: LoginModel): Call<ResponseBody>

    @POST("api/Login/register")
    fun postRegistrar(@Body params: RegistroModel): Call<ResponseBody>

    @GET("api/Shipping/getShippingApi")
    fun getEnvio(): Call<List<ShippingModel>>

    @PUT("api/Shipping/updateStatus/{id}")
    fun updateStatus(@Path("id") id: Int, @Body status: Map<String, String>): Call<ResponseBody>

}
