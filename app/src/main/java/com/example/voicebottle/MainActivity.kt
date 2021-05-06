package com.example.voicebottle

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.voicebottle.databinding.ActivityMainBinding
import com.example.voicebottle.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.internal.OsRealmConfig
import io.realm.kotlin.where
import java.io.File


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val AUDIO_RECORD_DIR = "/AudioRecording"

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private lateinit var player: MediaPlayer

    private lateinit var realm: Realm

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
        // 権限を得られなかった場合、終了する。
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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_record, R.id.navigation_reply
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        player = MediaPlayer.create(this, R.raw.sound_waves)
        player.isLooping = true
        player.setVolume(0.22F, 0.22F)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        realm = Realm.getDefaultInstance()
        val user = realm.where<User>().findAll().count()
        if (user == 0) {
            val dialog = EditTextDialog("名前を入力してください", "匿名さん", "OK", {})
            dialog.setCancelable(false)
            dialog.show(supportFragmentManager, "send_name_dialog")
        }


        val audioRecordDir = File(filesDir, "/AudioRecording")
        if(!audioRecordDir.exists()) {
            audioRecordDir.mkdirs()
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
}