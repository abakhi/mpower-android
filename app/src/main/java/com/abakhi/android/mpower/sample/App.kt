package com.abakhi.android.mpower.sample

import android.app.Application
import com.abakhi.android.mpower.Mpower

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Mpower.init {
            apiKeys {
                masterKey = "0d9604dc-9f73-42fa-bbf9-86ca86d2d708"
                privateKey = "test_private_NcnmQgB4bNICkqjauOO0mxAfCr8"
                token = "001d7872cb14b9c6afc1"
            }

            store {
                name = "Sample Android store"
            }

            actions {
                return_url = "http://www.savekirk.com/mystore"
                cancel_url = "http://www.savekirk.com/cancelorder"
            }

        }


    }
}
