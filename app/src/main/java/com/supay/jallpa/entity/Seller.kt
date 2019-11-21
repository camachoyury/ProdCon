package com.supay.core

data class Seller(val name:String, val phone:String, val address: String, val product: String, val obs: String, val location: Location){
    constructor() : this("", "", "", "", "", Location())

}