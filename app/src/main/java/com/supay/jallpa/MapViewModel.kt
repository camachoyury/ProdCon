package com.supay.jallpa

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supay.core.Seller
import com.supay.core.SellerRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapViewModel(private val repository: SellerRepository): ViewModel() {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    val sellersLiveData = MutableLiveData<List<Seller>>()

    fun getSellers(){
        scope.launch {
            val sellers = repository.getSellers()
            sellersLiveData.postValue(sellers)


        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()



}