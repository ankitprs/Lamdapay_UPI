package com.lamdapay.core.listener

import com.lamdapay.core.model.TransactionDetailModel
import com.lamdapay.core.model.TransactionStatus

interface PaymentStatusListener {
    fun onTransactionCompleted(transactionDetails: TransactionDetailModel)
    fun onTransactionCancelled()
}