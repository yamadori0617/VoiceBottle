package com.example.voicebottle.ui.record

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.emrekose.recordbutton.OnRecordListener
import com.example.voicebottle.*
import com.example.voicebottle.databinding.FragmentRecordBinding
import com.example.voicebottle.ui.reply.ReplyFragmentArgs
import com.example.voicebottle.ui.reply.ReplyFragmentDirections
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val LOG_TAG = "RecordFragment"
private const val LATEST_REC_DATE = "LATEST_REC_DATE"

class RecordFragment : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm: Realm
    private lateinit var user: User

    private val audioFilePath = "/AudioRecording"
    private var fileName: String = ""
    lateinit var latestRecordingDate: String
    lateinit var sendNameFile: File
    lateinit var latestNameFile: File
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null


    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                setVolume(0.78F, 0.78F)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioEncodingBitRate(16 * 44100)
            setAudioSamplingRate(44100)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        latestRecordingDate = pref.getString(LATEST_REC_DATE, "").toString()
        user = realm.where<User>().findFirst()!!
        sendNameFile =  File(context?.filesDir,
            "${audioFilePath}/${user.user_id}_${latestRecordingDate}.3gp")
        binding.recordDateText.text = latestRecordingDate
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latestNameFile = File(context?.filesDir, "${audioFilePath}/latestRecord.3gp")
        if (latestNameFile.exists()) {
            binding.soundWaveImage.visibility = View.VISIBLE
            binding.recordDateText.visibility = View.VISIBLE
        }
        fileName = latestNameFile.toString()

        val reply_id = arguments?.getString("REPLY_ID", null)
        val reply_name = arguments?.getString("REPLY_NAME", null)
        if(reply_id != null) {
            binding.toNameText.text = "To: ${reply_name}"
        }

        val format = SimpleDateFormat("yyyyMMdd_HHmmss")
        val recordButton = binding.recordButton

        var mStartRecording = false
        recordButton.setRecordListener(object : OnRecordListener {
            override fun onRecord() {
                if (!mStartRecording) {
                    mStartRecording = true
                    try {
                        onRecord(mStartRecording)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "onRecord: ")
                    }
                }
            }

            override fun onRecordCancel() {
                mStartRecording = false
                try {
                    onRecord(mStartRecording)
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "onRecordCancel: ")
                }
                saveLatestRecording()
            }

            override fun onRecordFinish() {
                mStartRecording = false
                try {
                    onRecord(mStartRecording)
                    saveLatestRecording()
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "onRecordFinish: ")
                }
                saveLatestRecording()
            }

            private fun saveLatestRecording() {
                val date = Date()
                latestRecordingDate = format.format(date)
                sendNameFile = File(context?.filesDir,
                    "${audioFilePath}/${user.user_id}_${latestRecordingDate}.3gp"
                )
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = pref.edit()
                editor.putString(LATEST_REC_DATE, latestRecordingDate).apply()

                binding.soundWaveImage.visibility = View.VISIBLE
                binding.recordDateText.visibility = View.VISIBLE
                binding.recordDateText.text = latestRecordingDate
            }
        })

        val playbackButton = binding.playbackButton
        var mStartPlaying = true
        playbackButton.setOnClickListener {
            onPlay(mStartPlaying)

            when(mStartPlaying) {
                true -> {
                    playbackButton.setImageResource(R.drawable.ic_baseline_pause_24)

                }
                false -> {
                    playbackButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
            mStartPlaying = !mStartPlaying
        }

        binding.sendButton.setOnClickListener {
            if (latestNameFile.exists()) {
                val dialog = ConfirmDialog("送信しますか？", "送信", {
                    latestNameFile.renameTo(sendNameFile)

                    realm.executeTransaction { db: Realm ->
                        val maxId = db.where<AudioRecording>().max("file_id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1L
                        val audioRecording = db.createObject<AudioRecording>(nextId)
                        audioRecording.file_path = "${audioFilePath}/${user.user_id}_${latestRecordingDate}.3gp"
                        audioRecording.sender_id = user.user_id
                        audioRecording.sender_name = user.user_name
                        audioRecording.created_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    }

                    val apiService = RestApiService()
                    val sendAudio = SendAudio(reply_id, user.api_token,
                        "${audioFilePath}/${user.user_id}_${latestRecordingDate}.3gp",
                            Base64.getEncoder().encodeToString(sendNameFile.readBytes()))
                    apiService.addMessage(sendAudio) {
                        if(it?.success == true) {
                            Snackbar.make(view, "ボトルを流した！", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(view, "通信エラー", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    binding.soundWaveImage.visibility = View.INVISIBLE
                    binding.recordDateText.visibility = View.INVISIBLE
                    binding.toNameText.text = ""
                },
                    "キャンセル", {
                        Snackbar.make(it, "キャンセルしました", Snackbar.LENGTH_SHORT).show()
                    })
                dialog.show(parentFragmentManager, "send_bottle_dialog")
            }
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
}
