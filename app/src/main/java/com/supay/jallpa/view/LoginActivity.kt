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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.supay.core.Seller
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var phone: String
    lateinit var code: String
    lateinit var auth: FirebaseAuth
    var storedVerificationId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        verifyCode.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()

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
//                Log.d(TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
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
                buttonEnter.setText("Enviar Código")
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
                    toast("Usuario autenticado")
                    startActivity(Intent(this, MapsActivity::class.java))
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

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

}
