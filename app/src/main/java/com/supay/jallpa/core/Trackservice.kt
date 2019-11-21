package com.supay.core


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Trackservice {

    @GET("/todos/{id}")
    suspend fun getTodo(@Path(value = "id") todoId: Int)

    @POST("/location")
    suspend fun postLocation(@Body location: Location)



}