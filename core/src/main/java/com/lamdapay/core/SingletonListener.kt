package com.lamdapay.core

import com.lamdapay.core.listener.PaymentStatusListener

internal object SingletonListener {
    internal var listener: PaymentStatusListener? = null
}