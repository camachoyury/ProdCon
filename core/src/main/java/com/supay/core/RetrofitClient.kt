package com.supay.core

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object{
        fun <S>  webservice(serviceClass: Class<S>): S  {
           return Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(serviceClass)
        }

    }
}
