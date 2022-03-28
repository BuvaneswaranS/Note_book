package com.example.note_book.displayFolderData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import kotlinx.coroutines.*


class DisplayFolderContentViewModel(val folderRepository: FolderRepository): ViewModel() {

    var isEnabled = MutableLiveData<Boolean>()

    var selectedList = MutableLiveData<MutableList<String>>()

    var deletingStarted = MutableLiveData<Boolean>()

    var selectedAllItem = MutableLiveData<Boolean>()

//    var isEnabled: Boolean = false

    var selectedItem = MutableLiveData<Boolean>()

    private val viewModelJob = Job()

    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

     var notesListFolder: LiveData<List<NoteData>>? = null

    init {
        deletingStarted.value = false
        selectedItem.value = false
        selectedAllItem.value = false
    }

//    fun insertingData(){
//        selectedList.value?.add("0")
//        Log.i("TestingApp","${selectedList.value?.size}")
//    }

    fun getNotesList(folderId: String){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                notesListFolder = folderRepository.getNotesListFolder(folderId)
            }
        }
    }

    fun deleteSelectedItem(){
        deletingStarted.value = true
        for(data in selectedList.value!!){
            Log.i("TestingApp","For Loop --> ${data}")
            scope.launch {
                folderRepository.deleteNoteDate(data)
            }
        }
        deletingStarted.value = false
        isEnabled.value = false
    }
}