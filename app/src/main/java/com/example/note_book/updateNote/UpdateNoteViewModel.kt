package com.example.note_book.updateNote

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdateNoteViewModel(application: Application): ViewModel() {

//  Declaration of FolderRespository
    private var folderRepository: FolderRepository

//  Coroutine Job and Scope is Declared
    private val viewModelJob = Job()
    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

//  Declaration and Initialization of FolderData to null
    var folderData: FolderData? = null

    init {
//      Getting the instance of the folderDao
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao

//      Getting the instance of the noteDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao

//      Initializing of folderRepository
        folderRepository = FolderRepository(folderDao, noteDataDao)

    }

//  Function to Update the Note in the DATABASE
    fun updateNoteData(noteData: NoteData){
        scope.launch {
            folderRepository.updateNoteData(noteData)
        }
    }

//  Function to get the Folder Data from the DATABASE
    fun getFolderData(folderId: String) {
        scope.launch {
            folderData = folderRepository.getFolderData(folderId)
        }
    }

}