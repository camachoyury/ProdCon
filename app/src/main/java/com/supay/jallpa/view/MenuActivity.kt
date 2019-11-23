package com.supay.jallpa.view

import android.Manifest
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.supay.jallpa.R
import kotlinx.android.synthetic.main.main_layout.*
import java.util.ArrayList
import android.Manifest.permission.*
import android.view.LayoutInflater
import android.widget.Button
import com.google.gson.Gson


class MenuActivity: AppCompatActivity() {
    private val permissions = ArrayList<String>()
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.CALL_PHONE)
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setIcon(R.mipmap.ic_launcher_2_foreground)

        permissionsToRequest = findUnAskedPermissions(permissions)
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        val sharedPref = getSharedPreferences("Data",0)

        if (sharedPref.contains("SellerInfo")){
            val myIntent = Intent(this, SellerActivity::class.java)
            startActivity(myIntent)
            finish()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0)
                requestPermissions(permissionsToRequest!!.toTypedArray(),
                    ALL_PERMISSIONS_RESULT
                )
        }

        buyer.setOnClickListener {


            val myIntent = Intent(this, MapsActivity::class.java)
            startActivity(myIntent)
        }

        seller.setOnClickListener {
            popUpDialog()
        }
//
//        s.setOnClickListener {
//            val myIntent = Intent(this, SellerForm::class.java)
//            startActivity(myIntent)
//        }
    }

    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(permission) === PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            permissionsRejected.toTypedArray(),
                                            MenuActivity.ALL_PERMISSIONS_RESULT
                                        )
                                    }
                                })
                            return
                        }
                    }

                }
            }
        }

    }
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MenuActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    companion object {

        private val ALL_PERMISSIONS_RESULT = 101
    }

    fun popUpDialog(){

        val builder = AlertDialog.Builder(this)

        val inflater = LayoutInflater.from(this)

        val view = inflater.inflate(R.layout.login_dialog, null)

        val goLogin = view.findViewById<Button>(R.id.goLoginBtn)
        val goRegistry = view.findViewById<Button>(R.id.goRegistryBtn)

        builder.setView(view)

        goLogin.setOnClickListener {
            val myIntent = Intent(this, LoginActivity::class.java)
            startActivity(myIntent)
        }

        goRegistry.setOnClickListener {
            val myIntent = Intent(this, SellerForm::class.java)
            startActivity(myIntent)
        }

        val alert = builder
        alert.show()

    }



}