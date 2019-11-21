package com.supay.jallpa

import com.supay.jallpa.viewmodel.client.MapViewModelFactory
import com.supay.core.*
import com.supay.jallpa.viewmodel.ViewModelFactory

object Injector {

    fun provideViewModelFactory(): ViewModelFactory {

        return ViewModelFactory(trackerRepository)
    }

    val trackerRepository: TrackRepository by lazy { return@lazy TrackRepository(trackerService)}

    val trackerService: Trackservice by lazy { return@lazy RetrofitClient.webservice(Trackservice::class.java) }

    fun provideMapViewModelFactory(): MapViewModelFactory {

        return MapViewModelFactory(sellerRepository)
    }

    val sellerRepository: SellerRepository by lazy { return@lazy SellerRepository(sellerService)}

    val sellerService: SellerService by lazy { return@lazy RetrofitClient.webservice(SellerService::class.java) }




}