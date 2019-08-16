package com.example.demopagos

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Client {

    private var  ourInstance : Retrofit?=null

    val instance: Retrofit
        get(){
            if(ourInstance==null){
                ourInstance= Retrofit.Builder()
                    .baseUrl("https://api.chois.mx/public/index.php/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return ourInstance!!
        }
}