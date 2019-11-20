package com.supay.core

class SellerRepository(val sellerService: SellerService) {

    suspend fun getSellers(): List<Seller> {
        return sellerService.getSellers()
    }
}