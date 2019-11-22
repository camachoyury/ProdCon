package com.supay.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class Location(val longitude:Double, val latitude:Double): Serializable {
    constructor() : this(0.0,0.0)
}