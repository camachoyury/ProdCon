package com.supay.jallpa.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.supay.core.Location
import com.supay.core.Seller
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.seller_form_activity.*
import java.security.MessageDigest

class SellerForm : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var password: EditText
    lateinit var address: EditText
    lateinit var product: EditText
    lateinit var additionalComments: EditText
    lateinit var saveButton: Button

    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seller_form_activity)

        reference = FirebaseDatabase.getInstance().getReference("productores")

        name = findViewById(R.id.editTextName)
        phone = findViewById(R.id.editTextPhone)
        password = findViewById(R.id.editTextPassword)
        address = findViewById(R.id.editTextAddress)
        product = findViewById(R.id.editTextProduct)
        additionalComments = findViewById(R.id.editTextComments)
        saveButton = findViewById(R.id.buttonSave)

        saveButton.setOnClickListener {
            checkData()
        }

    }

    private fun checkData(){

        val name = editTextName.text.toString().trim()
        val phone = editTextPhone.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val address = editTextAddress.text.toString().trim()
        val product = editTextProduct.text.toString().trim()
        val additionalComments = editTextComments.text.toString().trim()

        if (name.isEmpty()){
            editTextName.error = "Por favor ingresa tu nombre completo."
            return
        } else if (phone.isEmpty()){
            editTextPhone.error = "Por favor ingresa tu celular."
            return
        } else if (password.isEmpty()){
            editTextPassword.error = "Por favor ingresa una contraseña."
            return
        } else if (password.length < 8){
            editTextPassword.error = "Por favor ingresa una contraseña de al menos 8 caractéres."
            return
        } else if (product.isEmpty()){
            editTextProduct.error = "Por favor ingresa el/los producto(s)."
            return
        } else if (additionalComments.isEmpty()){
            editTextComments.error = "Por favor ingresa un comentario adicional."
            return
        }

        val sellerId = reference.push().key

        val hashedPassword = stringToHashedString(applicationContext,password)

        val location = Location(0.toDouble(),0.toDouble())

        val producer = Seller(sellerId.toString(), name, phone, hashedPassword, address, product, additionalComments, location)

        if (sellerId != null) {
            reference.child(sellerId).setValue(producer).addOnCompleteListener {
                Toast.makeText(applicationContext, "Enviado", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun stringToHashedString(context: Context, clearPassword: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(clearPassword.toByteArray(Charsets.UTF_8))
        val sb = StringBuilder()
        for (b in result) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }


}