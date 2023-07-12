package com.lamdapay.core

interface PaymentResultDataListener {
    fun onPaymentSuccess(event: String?, paymentDataModel: PaymentDataModel?)

    fun onPaymentError(errorCode: Int, error: String?, paymentDataModel: PaymentDataModel?)
}