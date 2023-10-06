package com.lamdapay.core.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lamdapay.core.SingletonListener
import com.lamdapay.core.exception.AppNotFoundException
import com.lamdapay.core.model.PaymentDataModel
import com.lamdapay.core.model.TransactionDetailModel
import com.lamdapay.core.model.TransactionStatus
import com.lamdapay.core.ui.ui.theme.LamdapayTheme
import java.util.Locale

class PaymentUiActivity : ComponentActivity() {

    private lateinit var payment: PaymentDataModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        payment = (intent.getSerializableExtra(EXTRA_KEY_PAYMENT) as PaymentDataModel?)
            ?: throw IllegalStateException("Unable to parse payment details")

        createWhenCalled()

        setContent {
            LamdapayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = "Payments is going On \n Please, Don't go back!",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    private fun createWhenCalled(){
        // Set Parameters for UPI
        val paymentUri = Uri.Builder().apply {
            with(payment) {
                scheme("upi").authority("pay")
                appendQueryParameter("pa", vpa)
                appendQueryParameter("pn", name)
                appendQueryParameter("tid", txnId)
                appendQueryParameter("mc", payeeMerchantCode)
                appendQueryParameter("tr", txnRefId)
                appendQueryParameter("tn", description)
                appendQueryParameter("am", amount)
                appendQueryParameter("cu", currency)
            }
        }.build()

        val paymentIntent = Intent(Intent.ACTION_VIEW).apply {
            data = paymentUri
        }
        val appChooser = Intent.createChooser(paymentIntent, "Pay Using")

        if(paymentIntent.resolveActivity(packageManager)!= null){
            startActivityForResult(appChooser, PAYMENT_REQUEST, null)
        }else {
            Toast.makeText(this, "No UPI app found...!!!", Toast.LENGTH_SHORT).show()
            throwOnAppNotFound()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PAYMENT_REQUEST){
            if(data != null){
                val response = data.getStringExtra("response")
                if(response == null){
                    Log.d(TAG, "Payment Response is null")
                    callbackTransactionCancelled()
                }else{
                    runCatching {
                        val transactionDetail = getTransactionDetails(response)
                        callbackTransactionCompleted(transactionDetail)
                    }.getOrElse {
                        callbackTransactionCancelled()
                    }
                }
            }else{
                Log.e(TAG, "Intent Data is null. User cancelled")
                callbackTransactionCancelled()
            }
            finish()
        }
    }

    internal fun getTransactionDetails(response: String): TransactionDetailModel {
        return with(getMapFromQuery(response)){
            TransactionDetailModel(
                transactionId = get("txnId"),
                responseCode = get("responseCode"),
                approvalRefNo = get("ApprovalRefNo"),
                transactionRefId = get("txnRef"),
                amount = payment.amount,
                transactionStatus = TransactionStatus.valueOf(
                    get("Status")?.toUpperCase(Locale.getDefault())
                        ?: TransactionStatus.FAILURE.name
                )
            )
        }
    }

    private fun getMapFromQuery(queryString: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val keyValuePairs = queryString.split("&").map{ param ->
            param.split("=").let{ Pair(it[0], it[1]) }
        }
        map.putAll(keyValuePairs)
        return map
    }
    internal fun throwOnAppNotFound() {
        Log.e(TAG, "No UPI app found on device.")
        throw AppNotFoundException("not found")
    }
    internal fun callbackTransactionCancelled() {
        SingletonListener.listener?.onTransactionCancelled()
    }

    internal fun callbackTransactionCompleted(transactionDetails: TransactionDetailModel) {
        SingletonListener.listener?.onTransactionCompleted(transactionDetails)
    }
    companion object {
        const val TAG = "PaymentUiActivity"
        const val PAYMENT_REQUEST = 4400
        const val EXTRA_KEY_PAYMENT = "payment"
    }
}