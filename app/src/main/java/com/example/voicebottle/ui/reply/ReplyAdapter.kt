package com.example.voicebottle.ui.reply

import android.annotation.SuppressLint
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

class ReplyAdapter (data: OrderedRealmCollection<AudioRecording>) :
    RealmRecyclerViewAdapter<AudioRecording, ReplyAdapter.ViewHolder>(data, true){

    private var listener: ((String?) -> Unit)? = null

    fun setOnItemClickListener(listener:(String?) -> Unit) {
        this.listener = listener

    }

    init {
        setHasStableIds(true)
    }

    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell) {
        val created_at: TextView = cell.findViewById(R.id.textView1)
        val sender_name: TextView = cell.findViewById(R.id.textView2)
        val cellPlaybackButton: ImageButton = cell.findViewById(R.id.cellPlaybackButton)
        val cellReplyButton: ImageButton = cell.findViewById(R.id.cellReplyButton)
        var mStartPlaying = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audioRecording: AudioRecording? = getItem(position)
        holder.created_at.text = audioRecording?.created_at
        holder.sender_name.text = audioRecording?.sender_name
        holder.cellPlaybackButton.setOnClickListener {
            if (holder.mStartPlaying) {
                holder.mStartPlaying = false
                holder.cellPlaybackButton.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                holder.mStartPlaying = true
                holder.cellPlaybackButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.invoke(audioRecording?.file_path)
            //RecordFragment().onPlay(true)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.file_id ?: 0
    }

}
