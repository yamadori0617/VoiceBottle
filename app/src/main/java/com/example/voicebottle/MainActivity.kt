package com.example.voicebottle

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.voicebottle.databinding.ActivityMainBinding
import com.example.voicebottle.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.File

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    private lateinit var player: MediaPlayer
    private lateinit var realm: Realm
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
                        R.id.navigation_home, R.id.navigation_record, R.id.navigation_reply,
                        R.id.navigation_list, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        player = MediaPlayer.create(this, R.raw.sound_waves)
        player.isLooping = true
        player.setVolume(0.22F, 0.22F)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val audioRecordDir = File(filesDir, "/AudioRecording")
        if(!audioRecordDir.exists()) {
            audioRecordDir.mkdirs()
        }

        realm = Realm.getDefaultInstance()
        val count = realm.where<User>().findAll().count()
        if (count == 0) {
            val dialog = EditTextDialog("名前を入力してください", "匿名さん", "OK",
                fun(userText: String) {
                    val apiService = RestApiService()
                    val sendName = SendName(userText)
                    apiService.addUser(sendName) {
                        if (it?.success == true) {
                            realm.executeTransaction { db: Realm ->
                                val user = db.createObject<User>()
                                user.user_id = it.data.id
                                user.password = it.data.password
                                user.user_name = it.data.name
                                user.api_token = it.data.api_token
                            }
                        } else {
                            finish()
                        }
                    }
                }
            )
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "send_name_dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        player.start()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}