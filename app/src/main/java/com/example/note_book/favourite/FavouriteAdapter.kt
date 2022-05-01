package com.example.note_book.favourite

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import com.example.note_book.displayFolderData.DisplayFolderContentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavouriteAdapter(viewModelAdapter: FavouriteViewModel): ListAdapter<NoteData, FavouriteAdapter.ViewHolder>(noteDataCallBack()) {

    var viewModel: FavouriteViewModel = viewModelAdapter

    var viewModelJob = Job()
    var scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var data = mutableListOf<String>()

    init {
        viewModel.selectedList.value?.let { data.addAll(it) }
        viewModel.selectedList.value = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {

        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_note, parent, false)
//        Log.i("TestingApp","Entered successfully")
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
        var noteData: NoteData = getItem(position)
        holder.bind(noteData)

//        ClickOn Listener to updateNote Fragment
        holder.itemView.setOnClickListener { view ->
            if(viewModel.isEnabled.value == false){
                Navigation.findNavController(view).navigate(favouriteFragmentDirections.actionFavouriteFragmentToUpdateNoteFragment(noteData.noteId, noteData.folderId, noteData.noteTitle, noteData.noteDescription, noteData.createdTime, noteData.favourite))
            }else if(viewModel.isEnabled.value == true){

                if(data.contains(noteData.noteId) == false){

                    viewModel.isEnabled.value = true

                    data.add(noteData.noteId)

                    viewModel.notesListSelected.add(noteData)

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true, favourite = noteData.favourite)

                    scope.launch {

                        viewModel.folderRepository.updateNoteData(noteDataUpdated)

                    }

                    viewModel.selectedList.value = data

                }else if(data.contains(noteData.noteId) == true){

                    viewModel.selectedItem.value = false

                    data.remove(noteData.noteId)

                    viewModel.notesListSelected.remove(noteData)

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false, favourite = noteData.favourite)

                    scope.launch {

                        viewModel.folderRepository.updateNoteData(noteDataUpdated)

                    }

                    viewModel.selectedList.value = data

                }
            }
        }

        holder.itemView.setOnLongClickListener{

            if (viewModel.isEnabled.value == false){

                if(data.contains(noteData.noteId) == false){

                    viewModel.isEnabled.value = true

                    data.add(noteData.noteId)

                    viewModel.notesListSelected.add(noteData)

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true, favourite = noteData.favourite)

                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }

                    viewModel.selectedList.value = data

                }else if(data.contains(noteData.noteId) == true){

                    viewModel.selectedItem.value = false

                    var deleteValue: String = noteData.noteId

                    data.remove(deleteValue)

                    viewModel.notesListSelected.remove(noteData)

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false, favourite = noteData.favourite)

                    scope.launch {

                        viewModel.folderRepository.updateNoteData(noteDataUpdated)

                    }

                    viewModel.selectedList.value = data
                }
            }
            return@setOnLongClickListener true
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var note_Title: TextView
        var note_description: TextView
        var selectedItem: ImageView
        init {
            note_Title = itemView.findViewById(R.id.folder_content_note_title)
            note_description = itemView.findViewById(R.id.folder_content_note_description)
            selectedItem = itemView.findViewById(R.id.selected_tick_button)
        }

        fun bind(noteData: NoteData){
            note_Title.setText(noteData.noteTitle)
            note_description.setText(noteData.noteDescription)
            if (noteData.selected){
                selectedItem.visibility = View.VISIBLE
            }else if (!noteData.selected){
                selectedItem.visibility = View.INVISIBLE
            }

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