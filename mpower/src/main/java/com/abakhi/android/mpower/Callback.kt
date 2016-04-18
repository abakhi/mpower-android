package com.abakhi.android.mpower

interface Callback<T> {
    fun onError(error: MpowerError)
    fun onSuccess(response: T)
}

interface ConfirmInvoiceCallback : Callback<ConfirmInvoiceResponse> {
    override fun onSuccess(response: ConfirmInvoiceResponse)
}

interface InvoiceCallback : Callback<InvoiceResponse> {
    override fun onSuccess(response: InvoiceResponse)
}

interface OprChargeCallback: Callback<OprChargeResponse> {
    override fun onSuccess(response: OprChargeResponse)
}

interface DirectPayCallback: Callback<DirectPayResponse> {
    override fun onSuccess(response: DirectPayResponse)
}

interface DirectMobileCallback: Callback<DirectMobileResponse> {
    override fun onSuccess(response: DirectMobileResponse)
}
