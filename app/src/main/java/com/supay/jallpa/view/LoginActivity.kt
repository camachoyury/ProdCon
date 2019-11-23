package com.supay.jallpa.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.gson.Gson
import com.supay.core.Seller
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    var TAG = "LoginActivity"
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var phone: String
    lateinit var code: String
    lateinit var auth: FirebaseAuth
    var storedVerificationId = ""
    var phoneExist: Boolean = false
    lateinit var sellerData: Seller

    lateinit var ref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        verifyCode.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("productores")

        buttonEnter.setOnClickListener {

            code = verifyCode.text.toString().trim()

            val p = loginPhone.text.toString().trim()
            if (p.contains("+591") || p.contains("00591")){
                phone = p
            } else {
                val prefix = "+591"
                phone = prefix.plus(p)
            }

            if (verifyCode.visibility == View.VISIBLE){

                if (code.isEmpty()){
                    verifyCode.error = "Por favor ingresa el código en el mensaje SMS que te enviamos"
                    return@setOnClickListener
                } else {
                    authenticate()
                }

            } else {

                if (phone.isEmpty()){
                    loginPhone.error = "Por favor ingresa el número de tu celular"
                    return@setOnClickListener
                } else if (phone.length < 8){
                    loginPhone.error = "Número de celular No Válido"
                    return@setOnClickListener
                }

                progress.visibility = View.VISIBLE
                verify()
            }
        }
    }

    private fun verificationCallbacks(){

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                progress.visibility = View.GONE
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    toast("Solicitud inválida. Error.")
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    toast("Demasiados intentos. Pruebe más tarde.")
                    // ...
                }

                progress.visibility = View.GONE
                toast("Hubo un error. Intente más tarde.")

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                progress.visibility = View.GONE
                verifyCode.visibility = View.VISIBLE
                loginPhone.isFocusableInTouchMode = false
                buttonEnter.setText("Comprobar Código de Verificación")
//                resendToken = token

                // ...
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    checkPhoneInDB(phone)

                    toast("Usuario autenticado")

                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        toast("Código inválido")

                    }
                }
            }
    }

    private fun verify(){

        verificationCallbacks()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
    }

    private fun authenticate(){

        code = verifyCode.text.toString().trim()

        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)

        signInWithPhoneAuthCredential(credential)

    }

    private fun toast (msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun checkPhoneInDB(cel: String){

        val celular: String

        if (cel.contains("+591")){
            celular = cel.removePrefix("+591")
        } else if (cel.contains("00591")){
            celular = cel.removePrefix("00591")
        } else {
            celular = cel
        }

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Toast.makeText(applicationContext,"Error en la Base de Datos", Toast.LENGTH_SHORT)
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.exists()){

                    for (pr in p0.children){
                        val s = pr.getValue(Seller::class.java)
                        if (s!!.phone.equals(celular)){
                            phoneExist = true
                            sellerData = s
                            break
                        }
                    }

                }

                if (phoneExist){
                    saveSellerInfo(sellerData)
                    startActivity(Intent(applicationContext, SellerActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(applicationContext, MenuActivity::class.java))
                    finish()
                }


            }

        })
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
