package com.example.voicebottle.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.voicebottle.AudioRecording
import com.example.voicebottle.RestApiService
import com.example.voicebottle.SendApiToken
import com.example.voicebottle.User
import com.example.voicebottle.databinding.FragmentHomeBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.File
import java.text.SimpleDateFormat
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reloadButton.setOnClickListener{
            getNewMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun getNewMessage() {
        val user = realm.where<User>().findFirst()
        val apiService = RestApiService()
        val sendApiToken = SendApiToken(user?.api_token)
        apiService.getMessage(sendApiToken) {
            if (it?.success == true) {
                binding.noMsgText.visibility = View.INVISIBLE

                realm.executeTransaction { db: Realm ->
                    val maxId = db.where<AudioRecording>().max("file_id")
                    val nextId = (maxId?.toLong() ?: 0L) + 1L
                    val audioRecording = db.createObject<AudioRecording>(nextId)
                    audioRecording.file_path = it.data.audio_path
                    audioRecording.sender_id = it.data.from_id
                    audioRecording.sender_name = it.data.sender_name
                    audioRecording.created_at = it.data.created_at
                }
                File(context?.filesDir, it.data.audio_path).writeBytes(
                    Base64.getDecoder().decode(it.data.audio_content))
            } else {
                binding.noMsgText.visibility = View.VISIBLE
            }
        }
    }
}