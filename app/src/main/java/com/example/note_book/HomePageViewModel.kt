package com.example.note_book

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomePageViewModel(val folderRepository: FolderRepository): ViewModel() {

    val started = MutableLiveData<Boolean>()

//    Coroutine Job and Scope is declared
    val viewModelJob = Job()
    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

//    INIT Block is declared
    init{
        started.value = false
    }

//    Insert the Default folder to the database
    fun updateDefaultFolder(){
        scope.launch {
            folderRepository.insertDefaultFolder()
        }
    }



}