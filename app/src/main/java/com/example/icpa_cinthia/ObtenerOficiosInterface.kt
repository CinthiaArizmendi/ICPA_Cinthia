package com.example.icpa_cinthia
import SQLlite.Oficio
import retrofit.http.FormUrlEncoded
import retrofit2.Call
import retrofit2.http.GET
interface ObtenerOficiosInterface {
    @FormUrlEncoded
    @GET("oficios.php")
    open fun misOficios():
            Call<List<Oficio?>?>?

}