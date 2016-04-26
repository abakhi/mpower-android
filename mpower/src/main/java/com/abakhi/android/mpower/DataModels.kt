package com.abakhi.android.mpower


data class MpowerItem(
        val name: String,
        val quantity: Int,
        val unit_price: Double,
        val total_price: Double,
        var description: String = ""
)

data class MpowerTax(
        val name: String,
        val amount: Double
)

data class MpowerAction(
        var return_url: String = "",
        var cancel_url: String = ""
)

data class MpowerStore(
    var name: String = "Untitled Store",
    var tagline: String? = null,
    var phone: String? = null,
    var postal_address: String? = null,
    var website_url: String? = null,
    var logo_url: String? = null
)

internal class InvoiceRequest(
        val invoice: MpowerInvoice.Invoice,
        val store: MpowerStore?,
        val actions: MpowerAction?,
        val custom_data: Map<String, Any>? = null
)

internal class OprInvoiceRequest(
        val invoice_data: InvoiceRequest,
        val opr_data: Map<String, String>,
        val custom_data: Map<String, Any>?
)



class InvoiceResponse(
        val token: String,
        val description: String,
        val invoice_token: String? = null
) : MpowerResponse()

open class MpowerResponse {
    var response_code: String? = null
    var response_text: String? = null
}

class ConfirmInvoiceResponse(
    val invoice: MpowerInvoice.Invoice,
    val mode: String,
    val status: String
) : MpowerResponse()

class OprChargeResponse (
    val description: String,
    val invoice_data: OprInvoiceResponse
) : MpowerResponse()

class OprInvoiceResponse (
    val invoice: MpowerInvoice.Invoice,
    val mode: String,
    val status: String,
    val receipt_url: String? = null,
    val customer: Map<String, String>? = null
)

class DirectPayResponse(
    val description: String,
    val transaction_id: String
) : MpowerResponse()

class DirectMobile(
    val customer_name: String,
    val customer_phone: String,
    val customer_email: String,
    val wallet_provider: String,
    val merchant_name: String,
    val amount: Double
)

class DirectMobileResponse(
    val description: String,
    val transaction_id: String,
    val mobile_invoice_no: String,
    val tx_status: String? = null,
    val cancel_reason: String? = null,
    val token: String? = null
) : MpowerResponse()


class MpowerError(resp: MpowerResponse) {
    private val res = resp
    fun get() : Exception = throw Exception(res.response_text)
    val errorCode = res.response_code
    val errorText = res.response_text

    override fun toString(): String = "Exception : $res.response_text"
}








