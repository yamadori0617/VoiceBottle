package com.example.voicebottle.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicebottle.AudioRecording
import com.example.voicebottle.User
import com.example.voicebottle.databinding.FragmentListBinding
import io.realm.Realm
import io.realm.kotlin.where

class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentListBinding? = null
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
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.list.layoutManager = linearLayoutManager
        val myid = realm.where<User>().findFirst()?.user_id
        val audioRecording = realm.where<AudioRecording>().equalTo("sender_id",myid).findAll()
        val count = audioRecording.count()
        if (count > 0) {
            val adapter = ListAdapter(audioRecording)
            val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            binding.list.addItemDecoration(itemDecoration)
            binding.list.adapter = adapter
            binding.noDataText2.visibility = View.INVISIBLE
        } else {
            binding.noDataText2.visibility = View.VISIBLE
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