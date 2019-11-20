package com.supay.buyer

import com.supay.core.*

object Injector {

    fun provideViewModelFactory(): ViewModelFactory{

        return  ViewModelFactory(sellerRepository)
    }

    val sellerRepository: SellerRepository by lazy { return@lazy SellerRepository(sellerService)}

    val sellerService: SellerService by lazy { return@lazy RetrofitClient.webservice(SellerService::class.java) }


}