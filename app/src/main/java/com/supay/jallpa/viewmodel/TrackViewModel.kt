package com.supay.jallpa.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supay.core.Location
import com.supay.core.TrackRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TrackViewModel(val repository: TrackRepository): ViewModel() {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)



    val popularMoviesLiveData = MutableLiveData<Todo>()

//    fun fetchMovies(){
//        scope.launch {
//            val retrivedTodo = repository.getTodo(1)
//            popularMoviesLiveData.postValue(retrivedTodo)
//        }
//    }

    fun setCurrentLocation(location: Location){
        scope.launch {
            val location = repository.postLocation(location)

        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()


}