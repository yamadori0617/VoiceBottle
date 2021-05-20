package com.example.voicebottle.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.voicebottle.*
import com.example.voicebottle.databinding.FragmentHomeBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.File
import java.lang.Exception
import java.util.*

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reloadButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.noMsgText.text = "loading..."
            try {
                val user = realm.where<User>().findFirst()
                val apiService = RestApiService()
                val sendApiToken = SendApiToken(user?.api_token)
                apiService.getMessage(sendApiToken) {
                    if (it?.success == true) {
                        realm.executeTransaction { db: Realm ->
                            val maxId = db.where<AudioRecording>().max("file_id")
                            val nextId = (maxId?.toLong() ?: 0L) + 1L
                            val audioRecording = db.createObject<AudioRecording>(nextId)
                            audioRecording.file_path = it.data.audio_path
                            audioRecording.sender_id = it.data.from_id
                            audioRecording.sender_name = it.data.sender_name
                            audioRecording.created_at = it.data.created_at

                            binding.fromNameText.text = "from: ${it.data.sender_name}"
                        }
                        File(context?.filesDir, it.data.audio_path).writeBytes(
                                Base64.getDecoder().decode(it.data.audio_content))

                        binding.noMsgText.text = ""
                        binding.newGlassBottleImage.animation = startScaling()
                        binding.newSoundWaveImage.animation = startScaling()
                        binding.newGlassBottleImage.visibility = View.VISIBLE
                        binding.newSoundWaveImage.visibility = View.VISIBLE
                    } else {
                        binding.noMsgText.text = "no bottle"
                        binding.newGlassBottleImage.visibility = View.INVISIBLE
                        binding.newSoundWaveImage.visibility = View.INVISIBLE
                    }
                }
            } catch (e: Exception) { }
            binding.progressBar.visibility = View.INVISIBLE
        }

        binding.newGlassBottleImage.setOnClickListener {
            it.findNavController().navigate(
                    R.id.action_navigation_home_to_navigation_reply, Bundle().apply {
                        putBoolean("FROM_HOME", true)
            })
        }
        binding.reloadButton.performClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun startScaling(): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.duration = 1000
        scaleAnimation.repeatCount = 10
        scaleAnimation.fillAfter = true
        return scaleAnimation
    }
}