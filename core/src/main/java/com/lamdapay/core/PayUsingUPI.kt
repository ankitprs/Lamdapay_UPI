package com.lamdapay.core

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.lamdapay.core.exception.AppNotFoundException
import com.lamdapay.core.listener.PaymentStatusListener
import com.lamdapay.core.model.PaymentDataModel
import com.lamdapay.core.ui.PaymentUiActivity


class PayUsingUPI constructor(
    private val activity: Activity,
    private val payment: PaymentDataModel
) {

    companion object {
        const val TAG = "PayUsingUPI_SDK"
        const val UPI_PAYMENT_REQUEST_CODE = 135
    }

    lateinit var activityLifecycleObserver: LifecycleObserver

    init {
        if (activity is AppCompatActivity) {
            activityLifecycleObserver = object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyed() {
                    SingletonListener.listener = null
                }
            }
            registerLifecycleObserver(activity)
        }else{
            Log.w(TAG, """
                Current Activity isn't AppCompatActivity.
                You'll need to call EasyUpiPayment#detachListener() to remove listener.
            """.trimIndent())
        }
    }
    fun startPayment() {
        // Create Payment Activity Intent
        val payIntent = Intent(activity, PaymentUiActivity::class.java).apply {
            putExtra(PaymentUiActivity.EXTRA_KEY_PAYMENT, payment)
        }

        // Start Payment Activity
        activity.startActivity(payIntent)
    }
    fun setPaymentStatusListener(mListener: PaymentStatusListener) {
        SingletonListener.listener = mListener
    }
    fun removePaymentStatusListener() {
        SingletonListener.listener = null
    }
    private fun registerLifecycleObserver(mLifecycleOwner: LifecycleOwner) {
        mLifecycleOwner.lifecycle.addObserver(activityLifecycleObserver)
    }
    /** DEPRECATION **/
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

    /** DEPRECATION **/
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
                payeeAddress,
                payeeName,
                payeeMCC,
                txnID,
                txnRefId,
                txnNote,
                payeeAmount,
                currencyCode,
                refUrl
            )
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            val chooser = Intent.createChooser(intent, "Pay with...")
            startActivityForResult(chooser, UPI_PAYMENT_REQUEST_CODE, null)
        }
    }

    class Builder(private val activity: Activity){
        var payeeVpa: String? = null
        var payeeName: String? = null
        var payeeMerchantCode: String? = null
        var transactionId: String? = null
        var transactionRefId: String? = null
        var description: String? = null
        var amount: String? = null

        fun setPayeeVpa(vpa: String): Builder = apply { payeeVpa = vpa }
        fun setPayeeName(name: String): Builder = apply { payeeName = name }
        fun setPayeeMerchantCode(merchantCode: String): Builder = apply {
            this.payeeMerchantCode = merchantCode
        }
        fun setTransactionId(id: String): Builder = apply { this.transactionId = id }
        fun setTransactionRefId(refId: String): Builder = apply { this.transactionRefId = refId }
        fun setDescription(description: String): Builder = apply { this.description = description }
        fun setAmount(amount: String): Builder = apply { this.amount = amount }

        fun build(): PayUsingUPI {
            validate()
            val payment = PaymentDataModel(
                currency = "INR",
                vpa = payeeVpa!!,
                name = payeeName!!,
                payeeMerchantCode = payeeMerchantCode!!,
                txnId = transactionId!!,
                txnRefId = transactionRefId!!,
                description = description!!,
                amount = amount!!
            )
            return PayUsingUPI(activity, payment)
        }
        private fun validate() {

            payeeVpa.run {
                checkNotNull(this) { "Must call setPayeeVpa() before build()." }
                check(this.matches("""^[\w-.]+@([\w-])+""".toRegex())) {
                    "Payee VPA address should be valid (For e.g. example@vpa)"
                }
            }

            payeeMerchantCode?.run {
                checkNotNull(this) { "Payee Merchant Code Should be Valid!" }
            }

            transactionId.run {
                checkNotNull(this) { "Must call setTransactionId() before build" }
                check(this.isNotBlank()) { "Transaction ID Should be Valid!" }
            }

            transactionRefId.run {
                checkNotNull(this) { "Must call setTransactionRefId() before build" }
                check(this.isNotBlank()) { "RefId Should be Valid!" }
            }

            payeeName.run {
                checkNotNull(this) { "Must call setPayeeName() before build()." }
                check(this.isNotBlank()) { "Payee name Should be Valid!" }
            }

            amount.run {
                checkNotNull(this) { "Must call setAmount() before build()." }
                check(this.matches("""\d+\.\d*""".toRegex())) {
                    "Amount should be valid positive number and in decimal format (For e.g. 100.00)"
                }
            }

            description.run {
                checkNotNull(this) { "Must call setDescription() before build()." }
                check(this.isNotBlank()) { "Description Should be Valid!" }
            }
        }
    }
}

fun PayUsingUPI(activity: Activity, initializer: PayUsingUPI.Builder.() -> Unit): PayUsingUPI {
    return PayUsingUPI.Builder(activity).apply(initializer).build()
}