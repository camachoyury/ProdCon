package com.supay.jallpa.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.gson.Gson
import com.supay.core.Location
import com.supay.core.Seller
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.seller_form_activity.*
import java.security.MessageDigest

class SellerForm : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var address: EditText
    lateinit var product: EditText
    lateinit var additionalComments: EditText
    lateinit var saveButton: Button
    var phoneExist: Boolean = false

    lateinit var nombre: String
    lateinit var celular: String
    lateinit var direccion: String
    lateinit var producto: String
    lateinit var comentariosAdicionales: String

    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seller_form_activity)

        reference = FirebaseDatabase.getInstance().getReference("productores")

        name = findViewById(R.id.editTextName)
        phone = findViewById(R.id.editTextPhone)
        address = findViewById(R.id.editTextAddress)
        product = findViewById(R.id.editTextProduct)
        additionalComments = findViewById(R.id.editTextComments)
        saveButton = findViewById(R.id.buttonSave)

        saveButton.setOnClickListener {
            phoneExist = false
            checkData()
        }

    }

    private fun checkData(){

        nombre = editTextName.text.toString().trim()
        celular = editTextPhone.text.toString().trim()
        direccion = editTextAddress.text.toString().trim()
        producto = editTextProduct.text.toString().trim()
        comentariosAdicionales = editTextComments.text.toString().trim()

        if (nombre.isEmpty()){
            editTextName.error = "Por favor ingresa tu nombre completo."
            return
        } else if (celular.isEmpty()){
            editTextPhone.error = "Por favor ingresa tu celular."
            return
        } else if (producto.isEmpty()){
            editTextProduct.error = "Por favor ingresa el/los producto(s)."
            return
        } else if (comentariosAdicionales.isEmpty()){
            editTextComments.error = "Por favor ingresa un comentario adicional."
            return
        }

        checkPhoneInDB(celular)
    }

    fun checkPhoneInDB(phone: String){

        reference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Toast.makeText(applicationContext,"Error en la Base de Datos", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.exists()){

                    for (pr in p0.children){
                        val s = pr.getValue(Seller::class.java)
                        if (s!!.phone.equals(celular)){
                            phoneExist = true
                            break
                        }
                    }

                }

                if (phoneExist){
                    editTextPhone.error = "El número ya está registrado."
                    return
                }

                sendNewSellerInfo()

            }

        })
    }

    fun sendNewSellerInfo(){
        val sellerId = reference.push().key

        val location = Location(0.toDouble(),0.toDouble())

        val seller = Seller(sellerId.toString(), nombre, celular, direccion, producto, comentariosAdicionales, location)

        if (sellerId != null) {
            reference.child(sellerId).setValue(seller).addOnCompleteListener {

                saveSellerInfo(seller)

                Toast.makeText(applicationContext, "Registro Completado", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun saveSellerInfo(seller: Seller){

        val sharedPref = getSharedPreferences("Data",0)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(seller)
        editor.putString("SellerInfo", json)
        editor.apply()
    }


}