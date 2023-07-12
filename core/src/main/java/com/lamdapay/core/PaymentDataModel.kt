package com.lamdapay.core

data class PaymentDataModel(
    val paymentStatus: String,
    val transactionId: String,
    val amount: String,
    val upiId: String,
    val merchantId: String,
    val checksum: String
)