package com.lamdapay.core.exception

import java.lang.Exception

class AppNotFoundException( appPackage: String?): Exception(
    """
        No UPI app Found !!!
    """.trimIndent()
) {
}