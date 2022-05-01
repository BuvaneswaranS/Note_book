package com.example.note_book

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomePageViewModel(application: Application): ViewModel() {

//    Declaration of the folderRepository
    private val folderRepository: FolderRepository

//    Coroutine Job and Scope is declared
    val viewModelJob = Job()
    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
//      Declaration of Initialization of FolderDao
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao

//      Declaration and Initialization of NoteDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao

//      Initialization of FolderRepository
        folderRepository = FolderRepository(folderDao, noteDataDao)
    }

//    Insert the Default folder to the database
    fun updateDefaultFolder(){
        scope.launch {
            folderRepository.insertDefaultFolder()
        }
    }

}