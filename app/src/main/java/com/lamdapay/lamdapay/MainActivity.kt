package com.lamdapay.lamdapay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.lamdapay.lamdapay.ui.theme.LamdapayTheme
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener


class MainActivity : ComponentActivity(), PaymentResultWithDataListener, ExternalWalletListener {
    val payeeAddress = "123 Main Street"
    val payeeName = "John Doe"
    val payeeMCC = "Retail"
    val txnID = "ABC123XYZ"
    val txnRefId = "98765432"
    val txnNote = "Payment for goods"
    val payeeAmount = "99.99"
    val currencyCode = "USD"
    val refUrl = "https://example.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val payUsingUPI = PayUsingUPI(this)
            LamdapayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen { upiId, payeeAmount ->
                        payUsingUPI.startPaymentUsingUPI(
                            upiId,
                            payeeName,
                            payeeMCC,
                            txnID,
                            txnRefId,
                            txnNote,
                            payeeAmount,
                            currencyCode,
                            refUrl
                        )
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PayUsingUPI.UPI_PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    if(data != null && data.getStringExtra("paymentStatus") == "SUCCESS"){
                        val paymentStatus = data.getStringExtra("paymentStatus")
                        val transactionId = data.getStringExtra("transactionId")
                        val amount = data.getStringExtra("amount")
                        val upiId = data.getStringExtra("upiId")
                        val merchantId = data.getStringExtra("merchantId")
                        val checksum = data.getStringExtra("checksum")

                        // TODO  -> handle the success payment here


                    }else{
                        Toast.makeText(this@MainActivity, "Payment Failed", Toast.LENGTH_SHORT).show()
                    }
                    // Do something with the payment data.
                }

                RESULT_CANCELED -> {
                    // The user canceled the payment.
                    Toast.makeText(this@MainActivity, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // An error occurred during the payment.
                    Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        TODO("Not yet implemented")
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
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