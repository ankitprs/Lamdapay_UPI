package com.lamdapay.core

import com.lamdapay.core.model.PaymentDataModel

interface PaymentResultDataListener {
    fun onPaymentSuccess(event: String?, paymentDataModel: PaymentDataModel?)

    fun onPaymentError(errorCode: Int, error: String?, paymentDataModel: PaymentDataModel?)
}