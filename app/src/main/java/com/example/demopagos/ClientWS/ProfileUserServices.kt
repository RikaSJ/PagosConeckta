package com.example.demopagos

import com.example.demopagos.Objects.*
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileUserServices {

    @FormUrlEncoded
    @POST("api/pagosOxxo")
    fun pagosOxxo(
        @Field("Articulo") Articulo : String,
        @Field("PrecioxUnidad") PrecioxUnidad : String,
        @Field("Cantidad") Cantidad : String?,
        @Field("Nombre") Nombre : String,
        @Field("Email") Email : String,
        @Field("Phone") Phone : String
    ) :Observable<ResponsePagosOxxo>

    @FormUrlEncoded
    @POST("api/pagosTarjeta")
    fun pagosTarjeta(
        @Field("Articulo") Articulo : String,
        @Field("PrecioxUnidad") PrecioxUnidad : String,
        @Field("Cantidad") Cantidad : String?,
        @Field("Nombre") Nombre : String,
        @Field("Email") Email : String,
        @Field("Phone") Phone : String,
        @Field("token") token : String
    ) :Observable<ResponsePagosOxxo>
}