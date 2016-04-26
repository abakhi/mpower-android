package com.abakhi.android.mpower

import com.github.kittinunf.fuel.core.Manager
import com.squareup.moshi.Moshi
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Android MPowerPayments API wrapper in Kotlin
 *
 * Checkout http://www.mpowerpayments.com/developers/http/ for more info.
 */
object Mpower {
    val live = "live"
    val test = "test"
    var mode: String by ModeDelegate()

    private val LIVE_BASE_URL = "https://app.mpowerpayments.com/api/v1/"
    private val TEST_BASE_URL = "https://app.mpowerpayments.com/sandbox-api/v1/"
    internal val checkOutInvoiceUrl =  processUrl("checkout-invoice/create")
    internal val confirmInvoiceUrl : (token: String) -> String = { processUrl("checkout-invoice/confirm/$it") }
    internal val oprUrl = processUrl("opr/create")
    internal val oprChargeUrl = processUrl("opr/charge")
    internal val directPayUrl = processUrl("direct-pay/credit-account")
    internal val directMobileUrl = LIVE_BASE_URL + "direct-mobile/charge"
    internal val directMobileStatusUrl = LIVE_BASE_URL + "direct-mobile/status"

    var apiKey: ApiKey by Delegates.notNull()
        private set

    var store: MpowerStore by Delegates.notNull()

    var actions: MpowerAction? = null

    /**
     * Call this to setup your MPowerPayment here
     */
    fun init(block: Mpower.() -> Unit): Mpower {
        Mpower.block()
        setHeaders()
        return Mpower
    }

    fun apiKeys(init: ApiKey.() -> Unit): ApiKey {
        val key = ApiKey()
        key.init()
        apiKey = key
        return key
    }

    fun store(init: MpowerStore.() -> Unit): MpowerStore {
        val store = MpowerStore()
        this.store = store
        store.init()

        return store
    }

    fun actions(init: MpowerAction.() -> Unit): MpowerAction {
        val actions = MpowerAction()
        actions.init()
        return actions
    }

    private fun processUrl(url: String) : String {
        return when(mode) {
            live -> LIVE_BASE_URL
            test -> TEST_BASE_URL
            else -> throw IllegalStateException("You can either set `mode` to live or test. Default is test")
        } + url
    }

    private fun setHeaders() {
        val headers = mapOf(
            "MP-Master-Key" to this.apiKey.masterKey,
            "MP-Private-Key" to this.apiKey.privateKey,
            "MP-Token" to this.apiKey.token
        )
        Manager.instance.baseHeaders = headers
    }

    internal fun moshi() : Moshi {
        return Moshi.Builder().build()
    }

    internal fun <T> parseResult(type: Class<T>, data: String) : T {
        return Mpower.moshi().adapter(type).let { it.fromJson(data) }
    }

    internal fun <T> toJson(type: Class<T>, value: T) : String {
        return Mpower.moshi().adapter(type).let { it.toJson(value) }
    }

    fun directPay(payee: String, amount: Double, callback: DirectPayCallback) {
        val adapter = Mpower.moshi().adapter(Map::class.java)
        val requestBody = adapter.toJson(
                mapOf(Pair("account_alias", payee), Pair("amount", amount))
        )

        MpowerClient().post(directPayUrl, requestBody, DirectPayResponse::class.java, object : MpowerClient.ClientCallback<DirectPayResponse> {
            override fun onResult(result: DirectPayResponse) {
                when(result.response_code) {
                    "00" -> callback.onSuccess(result)
                    else -> callback.onError(MpowerError(result))
                }
            }

        })

    }

    fun directMobile(data: DirectMobile, callback: DirectMobileCallback) {
        if (mode != live)
            throw IllegalStateException("Direct Mobile only allowed in live mode")

        val requestBody = Mpower.moshi().adapter(DirectMobile::class.java).let {
            it.toJson(data)
        }

        MpowerClient().post(directMobileUrl, requestBody, DirectMobileResponse::class.java, object : MpowerClient.ClientCallback<DirectMobileResponse> {
            override fun onResult(result: DirectMobileResponse) {
                when(result.response_code) {
                    "00" -> callback.onSuccess(result)
                    else -> callback.onError(MpowerError(result))
                }
            }

        })

    }

    fun directMobileStatus(token: String, callback: DirectMobileCallback) {
        if (mode != live)
            throw IllegalStateException("Direct Mobile only allowed in live mode")

        val requestBody = "{ \"token\": $token }"

        MpowerClient().post(directMobileStatusUrl, requestBody, DirectMobileResponse::class.java, object : MpowerClient.ClientCallback<DirectMobileResponse> {
            override fun onResult(result: DirectMobileResponse) {
                when(result.response_code) {
                    "00" -> callback.onSuccess(result)
                    else -> callback.onError(MpowerError(result))
                }
            }

        })

    }


    private class ModeDelegate : ReadWriteProperty<Any?, String> {
        private var value = "test"

        public override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return value
        }

        public override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            this.value = when (value.toLowerCase()) {
                live -> value
                test -> value
                else -> throw IllegalStateException("You can either set `mode` to live or test. Default is test")
            }.toLowerCase()
        }
    }


}