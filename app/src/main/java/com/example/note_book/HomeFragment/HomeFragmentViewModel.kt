package com.example.note_book.HomeFragment


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import kotlinx.coroutines.*

class HomeFragmentViewModel(val folderRepository: FolderRepository): ViewModel() {


    var isEnabled = MutableLiveData<Boolean>()


    lateinit var folder_list: LiveData<List<FolderData>>

    val viewModelJob = Job()

    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
        folder_list = folderRepository.folderDataDao.getAllDataByFolder()
    }

    var data: String? =  ""

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

    fun getDefaultFolderId(){
        scope.launch {
            return@launch withContext(Dispatchers.Main){
                data = folderRepository.getDefaultFolderId().folderId
//                Log.i("TestingApp","folder id -> ${id}")

            }

        }
    }

}