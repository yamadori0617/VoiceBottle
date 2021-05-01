package com.example.voicebottle.ui.reply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicebottle.AudioRecording
import com.example.voicebottle.R
import com.example.voicebottle.databinding.FragmentRecordBinding
import com.example.voicebottle.databinding.FragmentReplyBinding
import io.realm.Realm
import io.realm.kotlin.where

class ReplyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentReplyBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReplyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.layoutManager = LinearLayoutManager(context)
        val audio_recording = realm.where<AudioRecording>().findAll()
        val adapter = ReplyAdapter(audio_recording)
        binding.list.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}