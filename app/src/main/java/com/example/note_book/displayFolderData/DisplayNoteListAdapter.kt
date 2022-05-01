package com.example.note_book.displayFolderData

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DisplayNoteListAdapter(viewModelAdapter: DisplayFolderContentViewModel): ListAdapter<NoteData, DisplayNoteListAdapter.ViewHolder>(noteDataCallBack()) {

//  ViewModel
    var viewModel: DisplayFolderContentViewModel = viewModelAdapter

//  Coroutine Job
    var viewModelJob = Job()

//  Coroutine Scope
    var scope = CoroutineScope(Dispatchers.IO + viewModelJob)

//  Mutable List of String containing all the selected item's noteId
    var data = mutableListOf<String>()

//  Mutable List of NoteData containing all the selected item's noteData
    var noteDataList = mutableListOf<NoteData>()

    init {

        viewModel.selectedList.value?.let { data.addAll(it) }
        viewModel.selectedList.value = data

        viewModel.selectedListNoteData.value?.let { noteDataList.addAll(it) }
        viewModel.selectedListNoteData.value = noteDataList

    }

//  OnCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayNoteListAdapter.ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_note, parent, false)
        return ViewHolder(view)
    }


//  onBindViewHolder
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: DisplayNoteListAdapter.ViewHolder, position: Int) {
        var noteData: NoteData = getItem(position)

        holder.bind(noteData)

//        ClickOn Listener to updateNote Fragment
        holder.itemView.setOnClickListener { view ->

//          To Check whether the selection is enabled when the recyclerview item is clicked
//          if Selection is not enabled
            if(viewModel.isEnabled.value == false){

//              Navigate to UpdateNoteFragment
                Navigation.findNavController(view).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToUpdateNoteFragment(noteData.noteId, noteData.folderId, noteData.noteTitle, noteData.noteDescription, noteData.createdTime, noteData.favourite))

            }
//          If selection is enabled
            else if(viewModel.isEnabled.value == true){

//
                if(data.contains(noteData.noteId) == false){

                    viewModel.isEnabled.value = true

                    data.add(noteData.noteId)

                    noteDataList.add(noteData)
                    Log.i("TestingApp","noteDataList Size after Adding --> ${noteDataList.size}")

                    viewModel.noteListSelectedFav.add(noteData)

                    Log.i("TestingApp","NoteList Selected Size after Adding --> ${viewModel.notesListSelected.size}")

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true, favourite = noteData.favourite)

                    scope.launch {

                        viewModel.folderRepository.updateNoteData(noteDataUpdated)

                    }

                    if (noteData.favourite){
                        Log.i("TestingApp","Favourite --> ${noteData.favourite.toString()}")
                        viewModel.favourite_item.value = viewModel.favourite_item.value?.plus(1)

                    }else if (!noteData.favourite){
                        Log.i("TestingApp","Favourite --> ${noteData.favourite.toString()}")
                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.plus(1)


                    }

                    viewModel.selectedList.value = data

                    viewModel.selectedListNoteData.value = noteDataList

                }else if(data.contains(noteData.noteId) == true){

                    viewModel.selectedItem.value = false

                    data.remove(noteData.noteId)

                    viewModel.noteListSelectedFav.remove(noteData)

                    if (noteData.favourite){
                        Log.i("TestingApp","Favourite --> ${noteData.favourite.toString()}")
                        viewModel.favourite_item.value = viewModel.favourite_item.value?.minus(1)
                        Log.i("TestingApp","Favourite Size -> ${viewModel.favourite_item.value}")

                    }else if (!noteData.favourite){
                        Log.i("TestingApp","Favourite --> ${noteData.favourite.toString()}")
                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.minus(1)
                        Log.i("TestingApp","UnFavourite Size -> ${viewModel.unfavourtie_item.value}")
                    }

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false, favourite = noteData.favourite)

                    scope.launch {

                        viewModel.folderRepository.updateNoteData(noteDataUpdated)

                    }

                    viewModel.selectedList.value = data

//                    viewModel.selectedListNoteData.value = noteDataList
                }
            }

        }

        holder.itemView.setOnLongClickListener{

            if (viewModel.isEnabled.value == false){

                if(data.contains(noteData.noteId) == false){

                    viewModel.isEnabled.value = true

                    data.add(noteData.noteId)

                    viewModel.noteListSelectedFav.add(noteData)

                    if (noteData.favourite){

                        Log.i("TestingApp","Favourite --> ${noteData.favourite.toString()}")

                        viewModel.favourite_item.value = viewModel.favourite_item.value?.plus(1)

                        Log.i("TestingApp","Favourite Size -> ${viewModel.favourite_item.value}")

                    }else if (!noteData.favourite){

                        Log.i("TestingApp","Favourite --> ${noteData.favourite.toString()}")

                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.plus(1)

                        Log.i("TestingApp","UnFavourite Size -> ${viewModel.unfavourtie_item.value}")
                    }

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true, favourite = noteData.favourite)

                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }

                    viewModel.selectedList.value = data

                }else if(data.contains(noteData.noteId) == true){

                    viewModel.selectedItem.value = false

                    var deleteValue: String = noteData.noteId

                    data.remove(deleteValue)

                    viewModel.noteListSelectedFav.remove(noteData)

                    if (noteData.favourite){

                        viewModel.favourite_item.value = viewModel.favourite_item.value?.minus(1)
                        Log.i("TestingApp","Favourite Size -> ${viewModel.favourite_item.value}")

                    }else if (!noteData.favourite){

                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.minus(1)
                        Log.i("TestingApp","UnFavourite Size -> ${viewModel.unfavourtie_item.value}")
                    }

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