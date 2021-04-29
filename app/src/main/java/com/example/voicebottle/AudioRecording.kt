package com.example.voicebottle

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AudioRecording : RealmObject() {
    @PrimaryKey
    var file_id: Long = 0
    var file_name: String = ""
    var sender_id: String = ""
}