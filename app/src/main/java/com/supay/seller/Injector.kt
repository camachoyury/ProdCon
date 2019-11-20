package com.supay.seller

import com.supay.core.RetrofitClient
import com.supay.core.TrackRepository
import com.supay.core.Trackservice

object Injector {

    fun provideViewModelFactory(): ViewModelFactory{

        return  ViewModelFactory(trackerRepository)
    }

    val trackerRepository: TrackRepository by lazy { return@lazy TrackRepository(trackerService)}

    val trackerService: Trackservice by lazy { return@lazy RetrofitClient.webservice(Trackservice::class.java) }


}