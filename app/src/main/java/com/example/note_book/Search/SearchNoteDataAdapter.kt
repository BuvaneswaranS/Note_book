package com.example.note_book.Search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R

class SearchNoteDataAdapter(searchViewModel: SearchViewModel): ListAdapter<NoteData, SearchNoteDataAdapter.ViewHolder>(noteDataCallBack()) {

    val viewModel: SearchViewModel = searchViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchNoteDataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_search_note, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchNoteDataAdapter.ViewHolder, position: Int) {
        val noteData: NoteData = getItem(position)
        holder.bind(noteData)

        holder.itemView.setOnClickListener {view ->
            viewModel.changeFragment = "Changed"
//            findNavController(view).navigate(SearchFragmentDirections.actionSearchFragmentToUpdateNoteFragment(updateNoteId = noteData.noteId, updateNoteFolderId = noteData.folderId, updateNoteNoteTitle = noteData.noteTitle, updateNoteNoteDescription = noteData.noteDescription, updateNoteNoteCreatedTime = noteData.createdTime, favourite = noteData.favourite))
        }
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var folderTitle: TextView
        var folderDescription: TextView
        var selected_button: ImageView

        var head_note_title: TextView
        var head_note_descirption: TextView

        init {
            folderTitle = itemView.findViewById(R.id.folder_content_note_title)
            folderDescription = itemView.findViewById(R.id.folder_content_note_description)
            selected_button = itemView.findViewById(R.id.selected_tick_button)
            head_note_title = itemView.findViewById(R.id.item_layout_note_title_text)
            head_note_descirption = itemView.findViewById(R.id.item_layout_note_description_Text)
        }

        fun bind(noteData: NoteData){
            folderTitle.setText(noteData.noteTitle)
            folderDescription.setText(noteData.noteDescription)
            head_note_title.setText(noteData.noteTitle)
            head_note_descirption.setText(noteData.noteDescription)
            selected_button.visibility = View.INVISIBLE
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

}