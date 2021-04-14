package com.example.voicebottle.ui.record

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.emrekose.recordbutton.OnRecordListener
import com.emrekose.recordbutton.RecordButton
import com.example.voicebottle.MainActivity
import com.example.voicebottle.R
import com.example.voicebottle.databinding.FragmentRecordBinding
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val LOG_TAG = "AudioRecordTest"

class RecordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private var fileName: String = ""
    private var sendfileName: String = ""

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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val latestFile = File(context?.filesDir, "/AudioRecord/latestRecord.3gp")
        if (latestFile.exists()) {
            binding.soundWaveImage.visibility = View.VISIBLE
        }
        fileName = latestFile.toString()

        //fileName = File(context?.externalCacheDir?.absolutePath,
        //        "/VoiceBottle/latestRecord.3gp").toString()
        val format = SimpleDateFormat("yyyyMMdd_HHmmss")

        val recordButton = binding.recordButton
        var mStartRecording = false

        recordButton.setRecordListener(object : OnRecordListener {
            override fun onRecord() {
                Log.e(LOG_TAG, "onRecord: ")
                if (!mStartRecording) {
                    mStartRecording = true
                    onRecord(mStartRecording)
                }
            }

            override fun onRecordCancel() {
                Log.e(LOG_TAG, "onRecordCancel: ")
                mStartRecording = false
                try {
                    onRecord(mStartRecording)
                    binding.soundWaveImage.visibility = View.VISIBLE
                    val date = Date()
                    binding.recordDateText.text = format.format(date)
                    //binding.recordDateText.text = fileName

                    sendfileName =  File(context?.filesDir,
                            "/AudioRecord/${date}.3gp").toString()
                } catch (e: Exception) {

                }
            }

            override fun onRecordFinish() {
                Log.e(LOG_TAG, "onRecordFinish: ")
                mStartRecording = false
                onRecord(mStartRecording)
                binding.soundWaveImage.visibility = View.VISIBLE
                val date = Date()
                binding.recordDateText.text = format.format(date)

                sendfileName =  File(context?.filesDir,
                        "/AudioRecord/${date}.3gp").toString()
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
        Log.e(LOG_TAG, fileName)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}