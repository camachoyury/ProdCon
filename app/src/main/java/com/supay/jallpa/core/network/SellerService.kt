package com.supay.core

import retrofit2.http.GET
import retrofit2.http.Path

interface SellerService {

    @GET("/seller/{id}")
suspend fun getSellers(@Path(value = "id") todoId: Int): List<Seller>


    @GET("/seller/")
    suspend fun getSellers(): List<Seller>
}