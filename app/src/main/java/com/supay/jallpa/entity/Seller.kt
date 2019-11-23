package com.supay.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class Seller(val id: String, val name: String, val phone: String, val address: String, val product: String, val obs: String, val location: Location):Serializable {
    constructor() : this("", "", "", "","","", Location())
}