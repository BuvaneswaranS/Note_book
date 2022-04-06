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

    var viewModel: DisplayFolderContentViewModel = viewModelAdapter

    var viewModelJob = Job()
    var scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var data = mutableListOf<String>()


    init {
        viewModel.selectedList.value?.let { data.addAll(it) }
        viewModel.selectedList.value = data

//        for (i in data){
//            Log.i("Testing App","${i}")
//        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayNoteListAdapter.ViewHolder {

        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_note, parent, false)
//        Log.i("TestingApp","Entered successfully")
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: DisplayNoteListAdapter.ViewHolder, position: Int) {
        var noteData: NoteData = getItem(position)
        holder.bind(noteData)

//        ClickOn Listener to updateNote Fragment
        holder.itemView.setOnClickListener { view ->
            if(viewModel.isEnabled.value == false){
                Navigation.findNavController(view).navigate(
                    DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToUpdateNoteFragment(
                        noteData.noteId,
                        noteData.folderId,
                        noteData.noteTitle,
                        noteData.noteDescription,
                        noteData.createdTime
                    )
                )
            }else if(viewModel.isEnabled.value == true){

                if(data.contains(noteData.noteId) == false){
                    viewModel.isEnabled.value = true
//                holder.selectedItem.visibility = View.VISIBLE
//                viewModel.selectedItem.value = true
                    data.add(noteData.noteId)

                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true)
                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }

                    viewModel.selectedList.value = data
//                    viewModel.selectedItemsList.value?.clear()
//                    viewModel.selectedItemsList.value?.addAll(data)

//                    Log.i("TestingApp","-----------------------------------")
//                    for (i in data){
//                        Log.i("Testing App","${i}")
//                    }
//                    viewModel.selectedList.value = data
//                    viewModel.selectedList.value?.let { viewModel.selectedItemsList.addAll(it) }

                }else if(data.contains(noteData.noteId) == true){

                    Log.i("TestingApp","${noteData.noteTitle}")
//                holder.selectedItem.visibility = View.INVISIBLE
                    viewModel.selectedItem.value = false

                    data.remove(noteData.noteId)
                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false)

                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }
//                    Log.i("TestingApp","-----------------------------------")
//                    for (i in data){
//                        Log.i("Testing App","${i}")
//                    }
                    viewModel.selectedList.value = data
//                    viewModel.selectedItemsList.value?.clear()
//                    viewModel.selectedItemsList.value?.addAll(data)
//                    viewModel.selectedList.value = data
//                    viewModel.selectedList.value?.let { viewModel.selectedItemsList.addAll(it) }
                }
            }
        }

        holder.itemView.setOnLongClickListener{
            if (viewModel.isEnabled.value == false){
                if(data.contains(noteData.noteId) == false){
                    viewModel.isEnabled.value = true
//                holder.selectedItem.visibility = View.VISIBLE
//                viewModel.selectedItem.value = true

                    data.add(noteData.noteId)
                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true)

                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }
//                    Log.i("TestingApp","-----------------------------------")
//                    for (i in data){
//                        Log.i("Testing App","${i}")
//                    }
                    viewModel.selectedList.value = data
//                    viewModel.selectedItemsList.value?.clear()
//                    viewModel.selectedItemsList.value?.addAll(data)

//                    viewModel.selectedList.value = data


                }else if(data.contains(noteData.noteId) == true){

                    Log.i("TestingApp","${noteData.noteTitle}")
//                holder.selectedItem.visibility = View.INVISIBLE
                    viewModel.selectedItem.value = false

                    var deleteValue: String = noteData.noteId

                    data.remove(deleteValue)

                    Log.i("TestingApp","${viewModel.checkedBoxClicked.value}")

//                    if (viewModel.checkedBoxClicked.value == true){
//                        viewModel.checkedBoxClicked.value = false
//                    }
                    Log.i("TestingApp","${viewModel.checkedBoxClicked.value}")

//                    viewModel.selectedItemsList.addAll(data)
//                    viewModel.selectedList.value = data
                    var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false)

                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }

                    viewModel.selectedList.value = data
//                    viewModel.selectedItemsList.value?.clear()
//                    viewModel.selectedItemsList.value?.addAll(data)

//                    viewModel.selectedList.value?.let { viewModel.selectedItemsList.addAll(it) }

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