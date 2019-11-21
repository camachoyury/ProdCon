package com.supay.jallpa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supay.core.TrackRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: TrackRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  TrackViewModel(repository) as T
    }
}