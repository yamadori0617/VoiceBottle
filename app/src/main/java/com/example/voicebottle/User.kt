package com.example.voicebottle

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {
    @PrimaryKey
    var user_id: String = ""
    var password: String = ""
    var name: String = ""
    var api_token: String = ""
}