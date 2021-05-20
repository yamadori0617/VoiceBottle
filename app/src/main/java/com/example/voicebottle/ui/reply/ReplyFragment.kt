package com.example.voicebottle.ui.reply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicebottle.AudioRecording
import com.example.voicebottle.User
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
        _binding = FragmentReplyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.list.layoutManager = linearLayoutManager

        val myid = realm.where<User>().findFirst()?.user_id
        val audioRecording = realm.where<AudioRecording>().notEqualTo("sender_id", myid).findAll()
        val count = audioRecording.count()
        if (count > 0) {
            val adapter = ReplyAdapter(audioRecording)
            val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            binding.list.addItemDecoration(itemDecoration)
            binding.list.adapter = adapter
            binding.noDataText.visibility = View.INVISIBLE

            val fromHome = arguments?.getBoolean("FROM_HOME", false)
            if (fromHome == true) {
                binding.exclamationImage.visibility = View.VISIBLE
            } else {
                binding.exclamationImage.visibility = View.INVISIBLE
            }

        } else {
            binding.noDataText.visibility = View.VISIBLE
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