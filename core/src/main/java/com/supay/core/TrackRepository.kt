package com.supay.core

class TrackRepository(val trackservice: Trackservice) {

    suspend fun getTodo(id: Int) = trackservice.getTodo(id)

    suspend fun postLocation(location: Location) = trackservice.postLocation(location)

}