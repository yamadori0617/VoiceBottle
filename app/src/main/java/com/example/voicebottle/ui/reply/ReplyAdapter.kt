package com.example.voicebottle.ui.reply

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicebottle.AudioRecording
import com.example.voicebottle.ui.record.RecordFragment
import io.realm.OrderedRealmCollection
import io.realm.Realm.init
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
        val created_at: TextView = cell.findViewById(android.R.id.text1)
        val sender_name: TextView = cell.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ReplyAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyAdapter.ViewHolder,
                                  position: Int) {
        val audioRecording: AudioRecording? = getItem(position)
        holder.created_at.text = audioRecording?.created_at
        holder.sender_name.text = audioRecording?.sender_name
        holder.itemView.setOnClickListener {
            listener?.invoke(audioRecording?.file_path)
            //RecordFragment().onPlay(true)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.file_id ?: 0
    }

}
