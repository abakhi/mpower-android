package com.abakhi.android.mpower

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result


internal class MpowerClient {

    fun <T : Any> post(url: String, body: String, response: Class<T>) : T? {
        var parsedResult: T? = null

        url.httpPost().body(body).responseObject(Deserializer(response)) { req, res, result ->
            when(result) {
                is Result.Failure -> {
                    throw result.error
                }

                is Result.Success -> {
                    parsedResult = result.value
                }
            }
        }

        return parsedResult
    }


    fun <T : Any> get(url: String, response: Class<T>, parameters: List<Pair<String, Any?>>? = null) : T? {
        var parsedResult: T? = null

        url.httpGet(parameters).responseObject(Deserializer(response)) { req, res, result ->
            when(result) {
                is Result.Failure -> {
                    throw result.error
                }

                is Result.Success -> {
                    parsedResult = result.value
                }
            }
        }

        return parsedResult
    }





   class Deserializer<T : Any>(val type: Class<T>) : ResponseDeserializable<T> {
        override fun deserialize(content: String) = Mpower.parseResult(type, content)
    }


}