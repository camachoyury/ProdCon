package com.supay.jallpa.core

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.supay.core.Seller
import java.util.*

class SellerFireconnector() {

        var random = Random()



    fun saveSeller( seller: Seller){
        val  database = FirebaseDatabase.getInstance().reference
        database.child("sellers").child(Integer.toString(random.nextInt(100))).setValue(seller);
    }
}