package com.supay.buyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supay.core.SellerRepository
import com.supay.core.TrackRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: SellerRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  MapViewModel(repository) as T
    }
}