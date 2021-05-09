package com.example.voicebottle

import io.realm.RealmObject

open class User : RealmObject() {
    var user_id: String = ""
    var password: String = ""
    var user_name: String = ""
    var api_token: String = ""
}