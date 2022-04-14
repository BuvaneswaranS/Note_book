package com.example.note_book.displayFolderData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import kotlinx.coroutines.*


class DisplayFolderContentViewModel(val folderRepository: FolderRepository): ViewModel() {

    var isEnabled = MutableLiveData<Boolean>()

    var checkedBoxClicked = MutableLiveData<Boolean>()
    var allItemsSelected = MutableLiveData<Boolean>()

    var selectedList = MutableLiveData<MutableList<String>>()

    var noteIdFolderList: MutableList<String>? = null

    var deletingStarted = MutableLiveData<Boolean>()

    var selectedAllItem = MutableLiveData<Boolean>()

//    var isEnabled: Boolean = false

    var selectedItem = MutableLiveData<Boolean>()

    var defaultId: String = ""

    private val viewModelJob = Job()

    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

     var notesListFolder: LiveData<List<NoteData>>? = null

//    var selectedItemsList = MutableLiveData<MutableList<String>>()

    var size: Int = 0

    var notesListSelected = mutableListOf<NoteData>()

    var filesCame = MutableLiveData<Boolean>()
    var viewModelCreated = MutableLiveData<Boolean>()
    var donedeSelectingall = MutableLiveData<Boolean>()

    init {
        checkedBoxClicked.value = false
        deletingStarted.value = false
        selectedItem.value = false
        isEnabled.value = false
        selectedAllItem.value = false
        allItemsSelected.value = false
//        getFolderListSize()
        viewModelCreated.value = true
        donedeSelectingall.value = false
        filesCame.value = false
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

//    fun getFolderListSize(){
//        scope.launch {
//            return@launch withContext(Dispatchers.IO){
//                folderListSize = folderRepository.getFolderListSize()
//            }
//        }
//    }



    fun getNoteIdList(folderId: String){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                noteIdFolderList = folderRepository.getNoteData(folderId)
            }
        }
    }

    fun selectAndDeSelectAll(value: Boolean){
        deletingStarted.value = true
        for (dat in notesListFolder?.value!!){
            if (value == true){
                var data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value)
                scope.launch {
                    folderRepository.updateNoteData(data)
                }
            }else if (value == false){
                    var data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value)
                    scope.launch {
                        folderRepository.updateNoteData(data)
                    }
            }
        }
    }


//    fun selectAndDeSelectAllLifeCycleChange(value: Boolean){
//        deletingStarted.value = true
//        for (dat in notesListFolder?.value!!){
//            var data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value)
//            scope.launch {
//                folderRepository.updateNoteData(data)
//            }
//        }
//    }


    fun deleteSelectedItem(){
        deletingStarted.value = true
        for(data in selectedList.value!!){
            Log.i("TestingApp","For Loop --> ${data}")
            scope.launch {
                folderRepository.deleteNoteDate(data)
            }
        }
        isEnabled.value = false
    }

    fun getDefaultFolderId(){
        scope.launch {
            return@launch withContext(Dispatchers.Main){
                defaultId = folderRepository.getDefaultFolderId().folderId
//                Log.i("TestingApp","folder id -> ${id}")

            }

        }
    }

//    override fun onCleared() {
//        Log.i("TestingApp","Display Folder Content ViewModel OnClear Called")
//        selectAndDeSelectAll(false)
//        super.onCleared()
//    }

}