package com.example.note_book.allNotesCard

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AllNotesCardAdapter(viewModelAdapter: AllNotesCardViewModel): ListAdapter<NoteData, AllNotesCardAdapter.ViewHolder>(noteDataCallBack()) {

    val viewModel: AllNotesCardViewModel = viewModelAdapter

    val viewModelJob = Job()
    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var data = mutableListOf<String>()

    init {
        viewModel.selectedList.value?.let { data.addAll(it) }
        viewModel.selectedList.value = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNotesCardAdapter.ViewHolder {

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_note, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: AllNotesCardAdapter.ViewHolder, position: Int) {
        val noteData: NoteData = getItem(position)
        holder.bind(noteData)

//        ClickOn Listener to updateNote Fragment
        holder.itemView.setOnClickListener { view ->
            if(viewModel.isEnabled.value == false){


                Navigation.findNavController(view).navigate(AllNotesCardFragmentDirections.actionAllNotesCardFragmentToUpdateNoteFragment(noteData.noteId, noteData.folderId, noteData.noteTitle, noteData.noteDescription, noteData.createdTime, noteData.favourite))


            }

//          If Selection is Enabled
            else if(viewModel.isEnabled.value == true){

//              Check Whether the user clicked note is there in the selected List [data]
                if(data.contains(noteData.noteId) == false){

                    viewModel.isEnabled.value = true

                    data.add(noteData.noteId)

                    viewModel.notesListSelected.add(noteData)

//                  Check whether the user selected item is favourite
                    if (noteData.favourite){

//                      Increase the number of the fafcoiri
                        viewModel.favourite_item.value = viewModel.favourite_item.value?.plus(1)

                    }else if (!noteData.favourite){
                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.plus(1)
                    }

                    val noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true)

                    scope.launch {

                        viewModel.folderRepository.updateNoteData(noteDataUpdated)

                    }

                    viewModel.selectedList.value = data

                }else if(data.contains(noteData.noteId) == true){


                    viewModel.selectedItem.value = false

                    data.remove(noteData.noteId)

                    viewModel.notesListSelected.remove(noteData)

                    if (noteData.favourite){

                        viewModel.favourite_item.value = viewModel.favourite_item.value?.minus(1)

                    }else if (!noteData.favourite){
                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.minus(1)
                    }

                    val noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false)

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
//                holder.selectedItem.visibility = View.VISIBLE
//                viewModel.selectedItem.value = true

                    data.add(noteData.noteId)

                    viewModel.notesListSelected.add(noteData)

                    if (noteData.favourite){

                        viewModel.favourite_item.value = viewModel.favourite_item.value?.plus(1)

                    }else if (!noteData.favourite){
                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.plus(1)
                    }

                    val noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = true)

                    scope.launch {
                        viewModel.folderRepository.updateNoteData(noteDataUpdated)
                    }

                    viewModel.selectedList.value = data

                }else if(data.contains(noteData.noteId) == true){


                    viewModel.selectedItem.value = false

                    val deleteValue: String = noteData.noteId

                    data.remove(deleteValue)

                    viewModel.notesListSelected.remove(noteData)

                    if (noteData.favourite){

                        viewModel.favourite_item.value = viewModel.favourite_item.value?.minus(1)

                    }else if (!noteData.favourite){
                        viewModel.unfavourtie_item.value = viewModel.unfavourtie_item.value?.minus(1)
                    }


                    val noteDataUpdated = NoteData(noteId = noteData.noteId, folderId = noteData.folderId, noteTitle = noteData.noteTitle, noteDescription = noteData.noteDescription, createdTime = noteData.createdTime, modifiedTime = noteData.modifiedTime, selected = false)

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