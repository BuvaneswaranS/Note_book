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
        viewModel.isEnabled.value = false
        viewModel.selectedList.value = data
        viewModel.selectedList.value = data
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
            Navigation.findNavController(view).navigate(
                DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToUpdateNoteFragment(
                    noteData.noteId,
                    noteData.folderId,
                    noteData.noteTitle,
                    noteData.noteDescription,
                    noteData.createdTime
                )
            )
        }
//        if (viewModel.selectedAllItem.value == true){
//            holder.selectedItem.visibility = View.VISIBLE
//        }else{
//            holder.selectedItem.visibility = View.INVISIBLE
//        }


        holder.itemView.setOnLongClickListener{
            if(data.contains(noteData.noteId) == false){
                viewModel.isEnabled.value = true
//                holder.selectedItem.visibility = View.VISIBLE
//                viewModel.selectedItem.value = true

                data.add(noteData.noteId)

                var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true)

                scope.launch {
                    viewModel.folderRepository.updateNoteData(noteDataUpdated)
                }

//                if(data.add(noteData.noteId) == true){
//                    Log.i("TestingApp"," Mutable List of Adding Process Added Successfully")
//                }else{
//                    Log.i("TestingApp"," Mutable List of Adding Process Failed Successfully")
//
//                }

                viewModel.selectedList.value = data

//                if(viewModel.selectedList.value?.add(noteData.noteId) == true){
//                    Log.i("TestingApp"," Live Data Adding Process Added Successfully")
//                }else{
//                    Log.i("TestingApp"," Live Data Adding Process Failed Successfully")
//
//                }
//                Log.i("TestingApp", noteData.noteId)
//                Log.i("TestingApp2","Data Added")
//                Log.i("TestingApp2","Size after Adding ${viewModel.selectedList.value?.size}")

            }else if(data.contains(noteData.noteId) == true){
                Log.i("TestingApp","${noteData.noteTitle}")
//                holder.selectedItem.visibility = View.INVISIBLE
                viewModel.selectedItem.value = false

                data.remove(noteData.noteId)
                var noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false)

                scope.launch {
                    viewModel.folderRepository.updateNoteData(noteDataUpdated)
                }

//                if(data.remove(noteData.noteId)){
//                    Log.i("TestingApp"," Mutable List of Remove Process Added Successfully")
//                }else{
//                    Log.i("TestingApp"," Mutable List of Remove Process Failed Successfully")
//
//                }
                viewModel.selectedList.value = data
//                if(viewModel.selectedList.value?.add(noteData.noteId) == true){
//                    Log.i("TestingApp"," Live Data Removing Process Removed Successfully")
//                }else{
//                    Log.i("TestingApp"," Live Data Removing Process Failed Successfully")
//
//                }
//                Log.i("TestingApp2","Size after removing ${viewModel.selectedList.value?.size}")
//                Log.i("TestingApp", noteData.noteId)
//                Log.i("TestingApp2","Data Removed")
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