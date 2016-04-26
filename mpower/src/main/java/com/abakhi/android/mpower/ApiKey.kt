package com.abakhi.android.mpower

import kotlin.properties.Delegates

class ApiKey {
    var masterKey: String by Delegates.notNull()


    var privateKey: String by Delegates.notNull()


    var token: String by Delegates.notNull()

}
