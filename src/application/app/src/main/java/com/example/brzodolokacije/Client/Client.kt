package com.example.brzodolokacije.Client

import android.content.Context
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Managers.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object Client {

    //private val Auth = "Basic "+ Base64.encodeToString("")

    private const val BASE_URL = "http://10.0.2.2:7281/api/Auth/"
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        //.client(okhttpClient(this)) // Add our Okhttp client
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }

    /**
     * Initialize OkhttpClient with our interceptor
     */
   /* private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }*/
}