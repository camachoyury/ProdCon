package com.supay.jallpa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_validate_phone.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.supay.core.Seller
import com.supay.jallpa.view.MainActivity


class ValidatePhoneActivity :AppCompatActivity(){


    companion object{
        val PREFERENCE_FILE_KEY = "jallpa"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_phone)
        var mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        buttonEnter.setOnClickListener {

            if (!editTextPhone.text.isNullOrEmpty()){
                var query = mFirebaseDatabaseReference.child("productores")
                    .orderByChild("phone").equalTo(editTextPhone.text.trim().toString());
                query.addValueEventListener(valueEventListener);
            }else{
                Toast.makeText(this,"Introdusca su numero de telefono",Toast.LENGTH_LONG).show()

            }
        }
    }




    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot!!.exists()) {
                dataSnapshot.children.elementAt(0).value
                val producer = dataSnapshot.children.elementAt(0).getValue(Seller::class.java)

//                print(producer.toString())

                val sharedPref = getSharedPreferences(
                    PREFERENCE_FILE_KEY, Context.MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                val gson = Gson()
                val json = gson.toJson(producer)

                editor.putString("USER", json);
                editor.commit();
                var intent = Intent(this@ValidatePhoneActivity, MainActivity::class.java)
                startActivity(intent)
            }

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

}