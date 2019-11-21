package com.supay.jallpa.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.supay.core.Seller
import kotlinx.coroutines.*

class MapViewModel(): ViewModel() {

    private val parentJob = Job()
    val reference = FirebaseDatabase.getInstance().getReference("productores")

    var producers = MutableLiveData<List<Seller>>()
    val producerList: MutableList<Seller> = mutableListOf()

    fun updateUI(){

        reference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.exists()){

                    for (pr in p0.children){
                        val producer = pr.getValue(Seller::class.java)
                        producerList.add(producer!!)
                    }
                    producers.postValue(producerList)


                }
            }

        })
    }

}