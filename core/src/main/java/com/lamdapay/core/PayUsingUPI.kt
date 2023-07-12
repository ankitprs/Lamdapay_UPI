package com.lamdapay.core

import android.app.Activity
import android.content.Intent
import android.net.Uri


class PayUsingUPI (private val activity: Activity) {

    companion object {
        const val UPI_PAYMENT_REQUEST_CODE = 135
    }

    private fun getUPIString(
        payeeAddress: String,
        payeeName: String,
        payeeMCC: String,
        txnID: String,
        txnRefId: String,
        txnNote: String,
        payeeAmount: String,
        currencyCode: String,
        refUrl: String
    ): String {
        val upiUrl = (
                "upi://pay?pa=" + payeeAddress + "&pn=" + payeeName
                        + "&mc=" + payeeMCC + "&tid=" + txnID + "&tr=" + txnRefId
                        + "&tn=" + txnNote + "&am=" + payeeAmount + "&cu=" + currencyCode
                        + "&refUrl=" + refUrl
                )
        return upiUrl.replace(" ", "+")
    }

    fun startPaymentUsingUPI(
        payeeAddress: String,
        payeeName: String,
        payeeMCC: String,
        txnID: String,
        txnRefId: String,
        txnNote: String,
        payeeAmount: String,
        currencyCode: String = "INR",
        refUrl: String
    ) {
        activity.apply {
            val url = getUPIString(
                payeeAddress, payeeName, payeeMCC, txnID, txnRefId, txnNote, payeeAmount, currencyCode, refUrl
            )
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            val chooser = Intent.createChooser(intent, "Pay with...")
            startActivityForResult(chooser, UPI_PAYMENT_REQUEST_CODE , null)
        }
    }
}