package com.abakhi.android.mpower

import kotlin.properties.Delegates

class MpowerInvoice {

    var items: MutableMap<String, MpowerItem> by Delegates.notNull()
        private set

    var taxes: MutableMap<String, MpowerTax>? = null
        private set

    var customData: Map<String, Any>? = null

    var totalAmount: Double by Delegates.notNull()
    var description: String by Delegates.notNull()


    internal fun <T> formatItems(items: T, prefix: String) : MutableMap<String, Any?> {
        var newList = mutableMapOf<String, Any?>()
        if (items is List<*>) {
            items.mapIndexedNotNull {
                i, item ->
                newList.set("${prefix}_$i", item)
            }
        }
        return newList
    }

    @Suppress("UNCHECKED_CAST")
    fun addItems(_items: List<MpowerItem>) {
        items = formatItems(_items, "item") as MutableMap<String, MpowerItem>
    }

    @Suppress("UNCHECKED_CAST")
    fun addTaxes(_taxes: List<MpowerTax>) {
        taxes = formatItems(_taxes, "tax") as MutableMap<String, MpowerTax>
    }



    fun create(callback: InvoiceCallback) {
        var invoiceRequest = InvoiceRequest(
            Invoice(items, taxes, totalAmount, description),
                Mpower.store,
                Mpower.actions,
                customData
        )

        val requestData = Mpower.toJson(InvoiceRequest::class.java, invoiceRequest)

        MpowerClient().post(Mpower.checkOutInvoiceUrl, requestData, InvoiceResponse::class.java)?.let {
            when(it.response_code) {
                "00" -> callback.onSuccess(it)
                else -> callback.onError(MpowerError(it))
            }
        }

    }

    fun confirm(token: String, callback: ConfirmInvoiceCallback) {
        MpowerClient().get(Mpower.confirmInvoiceUrl(token), ConfirmInvoiceResponse::class.java)?.let {
            when(it.response_code) {
                "00" -> callback.onSuccess(it)
                else -> callback.onError(MpowerError(it))
            }
        }
    }

    fun createOpr(accountAlias: String, callback: InvoiceCallback) {
        var invoiceRequest = InvoiceRequest(
                Invoice(items, taxes, totalAmount, description),
                Mpower.store,
                Mpower.actions
        )
        var oprRequest = OprInvoiceRequest (
            invoiceRequest,
            mapOf(Pair("account_alias", accountAlias)),
            customData
        )
        val requestData = Mpower.toJson(OprInvoiceRequest::class.java, oprRequest)

        MpowerClient().post(Mpower.oprUrl, requestData, InvoiceResponse::class.java)?.let {
            when(it.response_code) {
                "00" -> callback.onSuccess(it)
                else -> callback.onError(MpowerError(it))
            }
        }
    }

    fun oprCharge(oprToken: String, confirmToken: String, callback: OprChargeCallback) {

        val chargeRequest = Mpower.toJson(Map::class.java,
                mapOf(Pair("token", oprToken), Pair("confirm_token", confirmToken))
        )

        MpowerClient().post(Mpower.oprChargeUrl, chargeRequest, OprChargeResponse::class.java)?.let {
            when(it.response_code) {
                "00" -> callback.onSuccess(it)
                else -> callback.onError(MpowerError(it))
            }
        }
    }

   data class Invoice(
            val items: MutableMap<String, MpowerItem>?,
            val taxes: MutableMap<String, MpowerTax>?,
            val total_amount: Double,
            val description: String

    )

}