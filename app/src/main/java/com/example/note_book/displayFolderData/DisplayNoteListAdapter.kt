package com.example.note_book.displayFolderData

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R

class DisplayNoteListAdapter: ListAdapter<NoteData, DisplayNoteListAdapter.ViewHolder>(noteDataCallBack()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayNoteListAdapter.ViewHolder {

        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_note, parent, false)
        Log.i("TestingApp","Entered successfully")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisplayNoteListAdapter.ViewHolder, position: Int) {
        var noteData: NoteData = getItem(position)
            holder.bind(noteData)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var note_Title: TextView
        var note_description: TextView
        init {
            note_Title = itemView.findViewById(R.id.folder_content_note_title)
            note_description = itemView.findViewById(R.id.folder_content_note_description)
        }

        fun bind(noteData: NoteData){
            note_Title.setText(noteData.noteTitle)
            note_description.setText(noteData.noteDescription)
        }

    }
}

class noteDataCallBack: DiffUtil.ItemCallback<NoteData>(){
    override fun areItemsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}