package com.lamdapay.lamdapay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lamdapay.core.PayUsingUPI
import com.lamdapay.core.listener.PaymentStatusListener
import com.lamdapay.core.model.TransactionDetailModel
import com.lamdapay.core.model.TransactionStatus
import com.lamdapay.lamdapay.ui.theme.LamdapayTheme
import org.w3c.dom.Text


class MainActivity : ComponentActivity(), PaymentStatusListener {


    private lateinit var payUsingUPI: PayUsingUPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LamdapayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen { upiId, payeeAmount ->
                        pay(upiId, payeeAmount)
                    }
                }
            }
        }
    }

    private fun pay(upiId: String, payeeAmount: String){

        val payeeVpa = upiId
        val payeeName = "John Doe"
        val transactionId = "ABC123XYZ"
        val transactionRefId = "98765432"
        val payeeMerchantCode = "Retail"
        val description = "Payment for goods"
        val amount = payeeAmount

        try {
            // START PAYMENT INITIALIZATION
            payUsingUPI = PayUsingUPI(this) {
                this.payeeVpa = payeeVpa
                this.payeeName = payeeName
                this.transactionId = transactionId
                this.transactionRefId = transactionRefId
                this.payeeMerchantCode = payeeMerchantCode
                this.description = description
                this.amount = amount
            }
            // END INITIALIZATION

            // Register Listener for Events
            payUsingUPI.setPaymentStatusListener(this)

            // Start payment / transaction
            payUsingUPI.startPayment()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Error: ${e.message}")
        }
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetailModel) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString())

        when (transactionDetails.transactionStatus) {
            TransactionStatus.SUCCESS -> onTransactionSuccess()
            TransactionStatus.FAILURE -> onTransactionFailed()
            TransactionStatus.SUBMITTED -> onTransactionSubmitted()
        }
    }

    override fun onTransactionCancelled() {
        // Payment Cancelled by User
        toast("Cancelled by user")
    }

    private fun onTransactionSuccess() {
        // Payment Success
        toast("Success")
    }

    private fun onTransactionSubmitted() {
        // Payment Pending
        toast("Pending | Submitted")
    }

    private fun onTransactionFailed() {
        // Payment Failed
        toast("Failed")
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        payUsingUPI.removePaymentStatusListener()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onPayButtonClick: (upiId: String, amount: String) -> Unit) {
    val upiIdState = remember { mutableStateOf("") }
    val amountState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "UPI Payments",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 32.dp)
        )
        OutlinedTextField(
            value = upiIdState.value,
            onValueChange = { upiIdState.value = it },
            label = { Text(text = "UPI ID") },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(100.dp)
        )

        OutlinedTextField(
            value = amountState.value,
            onValueChange = { amountState.value = it },
            label = { Text(text = "Amount") },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(100.dp)
        )

        Button(
            onClick = { onPayButtonClick(upiIdState.value, amountState.value) },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Pay Using UPI",
                modifier = Modifier
                    .padding(bottom = 8.dp),
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HomeScreen { upiId, amount ->
    }

}