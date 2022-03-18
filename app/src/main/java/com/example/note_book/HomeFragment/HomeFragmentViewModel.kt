package com.example.note_book.HomeFragment


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragmentViewModel(val folderRepository: FolderRepository): ViewModel() {

    lateinit var folder_list: LiveData<List<FolderData>>

    val viewModelJob = Job()

    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
        folder_list = folderRepository.folderDataDao.getAllDataByFolder()
    }

    fun updateDefaultFolder(){
        scope.launch {
            folderRepository.insertDefaultFolder()
        }
    }

    fun insertUserDefinedFolder(folderData: FolderData){
        scope.launch {
            folderRepository.insert(folderData)
        }
    }

    fun updateFolderName(folderData: FolderData){
        scope.launch {
            folderRepository.updatefolderName(folderData)
        }
    }

}