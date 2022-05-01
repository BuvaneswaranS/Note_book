package com.example.note_book.createNewNote

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class CreateNewNoteViewModel(application: Application): ViewModel() {

//  Declaration of FolderRepository
    private var folderRepository: FolderRepository

//  Coroutine Job Declaration
    private val viewModelJob = Job()

//  Coroutine Scope Declaration
    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {

//      Declaration and Initialization of folderDataDao
        val folderDataDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao

//      Declaration and Initialization of noteDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao

//      Initialization of folderRepository
        folderRepository = FolderRepository(folderDataDao,noteDataDao)

    }

//  Function for inserting the NoteData to the Database
    fun insertNoteData(noteData: NoteData){

//      This below operation scope.launch --> launch an database insert operation
        scope.launch {

            folderRepository.insertNoteData(noteData)

        }

    }

}