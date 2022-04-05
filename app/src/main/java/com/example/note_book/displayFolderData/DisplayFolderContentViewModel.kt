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

    var noteIdFolderList: MutableList<String>? = null

    var deletingStarted = MutableLiveData<Boolean>()

    var selectedAllItem = MutableLiveData<Boolean>()

//    var isEnabled: Boolean = false

    var selectedItem = MutableLiveData<Boolean>()

    private val viewModelJob = Job()

    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

     var notesListFolder: LiveData<List<NoteData>>? = null

    var selectedItemsList = mutableListOf<String>()

    init {
        deletingStarted.value = false
        selectedItem.value = false
        isEnabled.value = false
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
            var data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value)
            scope.launch {
                folderRepository.updateNoteData(data)
            }
        }
        deletingStarted.value = false
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