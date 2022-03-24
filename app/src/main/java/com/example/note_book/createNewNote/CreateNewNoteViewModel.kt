package com.example.note_book.createNewNote


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import kotlinx.coroutines.*

class CreateNewNoteViewModel(val folderRepository: FolderRepository): ViewModel() {


    private val viewModelJob = Job()

    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

//    var data: String? = ""

    init {
//        getDefaultFolderId()
    }


    fun insertNoteData(noteData: NoteData){
        scope.launch {
            folderRepository.insertNoteData(noteData)
        }
    }

//    fun getDefaultFolderId(){
//        scope.launch {
//            return@launch withContext(Dispatchers.Main){
//                data = folderRepository.getDefaultFolderId().folderId
////                Log.i("TestingApp","folder id -> ${id}")
//
//            }
//
//        }
//    }
}