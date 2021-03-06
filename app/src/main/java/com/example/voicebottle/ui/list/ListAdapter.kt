package com.example.voicebottle.ui.list


import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicebottle.AudioRecording
import com.example.voicebottle.R
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.io.File
import java.io.IOException


class ListAdapter (data: OrderedRealmCollection<AudioRecording>) :
    RealmRecyclerViewAdapter<AudioRecording, ListAdapter.ViewHolder>(data, true){
    private val LOG_TAG = "ListFragment"
    private var listener: ((String?) -> Unit)? = null
    private var player: MediaPlayer? = null
    private lateinit var fileName: String

    fun setOnItemClickListener(listener:(String?) -> Unit) {
        this.listener = listener
    }

    init {
        setHasStableIds(true)
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


    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell) {
        val created_at: TextView = cell.findViewById(R.id.textView1)
        val sender_name: TextView = cell.findViewById(R.id.textView2)
        val cellPlaybackButton: ImageButton = cell.findViewById(R.id.cellPlaybackButton)
        var mStartPlaying = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audioRecording: AudioRecording? = getItem(position)
        holder.created_at.text = audioRecording?.created_at
        holder.sender_name.text = audioRecording?.sender_name
        holder.cellPlaybackButton.setOnClickListener {
            if (holder.mStartPlaying) {
                fileName = File(it.context.filesDir, "${audioRecording?.file_path}").toString()
                onPlay(holder.mStartPlaying)
                holder.mStartPlaying = false
                holder.cellPlaybackButton.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                onPlay(holder.mStartPlaying)
                holder.mStartPlaying = true
                holder.cellPlaybackButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.invoke(audioRecording?.file_path)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.file_id ?: 0
    }

}
