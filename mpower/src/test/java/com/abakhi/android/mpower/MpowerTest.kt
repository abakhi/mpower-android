package com.abakhi.android.mpower

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MpowerTest {

    @Before
    fun init() {
        Mpower.init {
            apiKeys {
                masterKey = "0d9604dc-9f73-42fa-bbf9-86ca86d2d7088"
                privateKey = "test_private_NcnmQgB4bNICkqjauOO0mxAfCr88"
                token = "001d7872cb14b9c6afc11"
            }

            store {
                name = "Sample Android store"
            }

            actions {
                return_url = "http://www.samplestore.com/mystore"
                cancel_url = "http://www.samplestore.com/cancelorder"
            }
        }
    }

    @Test
    fun default_mode_is_test() {
        Mpower.init {  }
        Assert.assertEquals(Mpower.test, Mpower.mode)
    }

    @Test
    fun url_should_match_current_mode() {
        val expectedUrl = "https://app.mpowerpayments.com/sandbox-api/v1/checkout-invoice/create"
        Assert.assertEquals(expectedUrl, Mpower.checkOutInvoiceUrl)
    }

    @Test
    fun invoice_items_are_properly_formatted() {
        val items1 = MpowerItem("Football", 2, 2.2, 4.4)
        val items2 = MpowerItem("Shoe", 4, 4.0, 16.0)
        val expected = mutableMapOf(
                "item_0" to items1,
                "item_1" to items2
        )
        val invoice = MpowerInvoice()
        invoice.addItems(listOf(items1, items2))

        Assert.assertEquals(expected, invoice.items)
    }

    @Test
    fun taxes_are_properly_formatted() {
        val tax1 = MpowerTax("Football", 2.0)
        val tax2 = MpowerTax("Show", 4.0)
        val expected = mutableMapOf(
                "tax_0" to tax1,
                "tax_1" to tax2
        )
        val invoice = MpowerInvoice()
        invoice.addTaxes(listOf(tax1, tax2))

        Assert.assertEquals(expected, invoice.taxes)
    }

    @Test(expected = IllegalStateException::class)
    fun direct_mobile_only_in_live_mode() {
        val dm = DirectMobile("Test User", "024222222", "mpower@abakhi.com", "mtn", "Abakhi", 23.0)
        Mpower.directMobile(dm, object : DirectMobileCallback{
            override fun onSuccess(response: DirectMobileResponse) {
            }

            override fun onError(error: MpowerError) {
            }

        })
    }

    @Test(expected = IllegalStateException::class)
    fun direct_mobile_status_only_live_mode() {
        Mpower.directMobileStatus("34545", object : DirectMobileCallback {
            override fun onSuccess(response: DirectMobileResponse) {
            }

            override fun onError(error: MpowerError) {
            }

        })

    }

    @Test
    fun convert_object_to_json() {
        val data = mapOf(Pair("one", 1), Pair("two", "test"))
        val expected = "{\"one\":1,\"two\":\"test\"}"

        Assert.assertEquals(expected, Mpower.toJson(Map::class.java, data))
    }

    @Test
    fun convert_json_to_object() {
        val code = "400"
        val text = "Error getting input"
        val data = "{\"response_code\":\"$code\",\"response_text\":\"$text\"}";
        val parsed = Mpower.parseResult(MpowerResponse::class.java, data);

        Assert.assertEquals(code, parsed.response_code)
        Assert.assertEquals(text, parsed.response_text)
    }

    @Test(expected = IllegalStateException::class)
    fun mode_can_either_be_live_or_test() {
        Mpower.init { mode = "text" }
    }

}

