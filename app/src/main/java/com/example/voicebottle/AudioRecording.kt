package com.example.voicebottle

import androidx.annotation.Nullable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*


open class AudioRecording : RealmObject() {
    @PrimaryKey
    var file_id: Long = 0
    var file_path: String = ""
    var sender_id: String = ""
    var sender_name: String = ""
    var created_at: String = ""
}