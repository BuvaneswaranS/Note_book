package com.example.note_book.updateNote

import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdateNoteViewModel(val repository: FolderRepository): ViewModel() {

    private val viewModelJob = Job()

    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var folderData: FolderData? = null

    fun updateNoteData(noteData: NoteData){
        scope.launch {
            repository.updateNoteData(noteData)
        }
    }

    fun getFolderData(folderId: String) {
        scope.launch {
            folderData = repository.getFolderData(folderId)
        }
    }

//    fun updateFolderData(){
//        scope.launch {
//            repository.updatedFolderModifiedTime(folderData)
//        }
//    }

}