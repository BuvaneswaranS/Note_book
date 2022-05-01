package com.example.note_book.favourite

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class FavouriteViewModel(val application: Application): ViewModel() {

//    Declaration of the folderRespository
    val folderRepository: FolderRepository

    var isEnabled = MutableLiveData<Boolean>()

    var checkedBoxClicked = MutableLiveData<Boolean>()
    var allItemsSelected = MutableLiveData<Boolean>()

    var selectedList = MutableLiveData<MutableList<String>>()

    var noteIdFolderList: MutableList<String>? = null

    var allNoteIdList: MutableList<String>? = null

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

     var favouriteList: LiveData<List<NoteData>>?= null

    init {
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        folderRepository = FolderRepository(folderDao, noteDataDao)
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
        getFavouriteItem()
    }

    fun getAllNotesList(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                notesListFolder = folderRepository.getAllNotesCard()
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

//    fun getAllNoteIdList(){
//        scope.launch {
//            return@launch withContext(Dispatchers.IO){
//                allNoteIdList = folderRepository.getAllNoteId()
//            }
//        }
//    }

    fun selectAndDeSelectAll(value: Boolean){
        deletingStarted.value = true
        for (dat in favouriteList?.value!!){
            if (value == true){
                var data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value, favourite = dat.favourite)
                scope.launch {
                    folderRepository.updateNoteData(data)
                }
            }else if (value == false){
                var data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value, favourite = dat.favourite)
                scope.launch {
                    folderRepository.updateNoteData(data)
                }
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
        isEnabled.value = false
    }

//    fun getDefaultFolderId(){
//        scope.launch {
//            return@launch withContext(Dispatchers.Main){
//                defaultId = folderRepository.getDefaultFolderId().folderId
////                Log.i("TestingApp","folder id -> ${id}")
//
//            }
//
//        }
//    }

    fun getFavouriteItem(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                favouriteList = folderRepository.getFavouriteNotes(true)
//                Log.i("TestingApp","folder id -> ${id}")

            }
        }
    }

    fun getNotesList() {
        for (data in selectedList.value!!) {
            for (dataNotes in notesListFolder?.value!!) {
                if (data == dataNotes.noteId) {
                    notesListSelected.add(dataNotes)
                }
            }
        }
    }

        fun makeUnFavourite(){
        Log.i("TestingApp","Selected Note Data Size --> ${notesListSelected.size}")
        for (dat in notesListSelected) {

            scope.launch {
                folderRepository.deleteNoteDate(dat.noteId)
            }

            var data = NoteData(
                folderId = dat.folderId,
                noteTitle = dat.noteTitle,
                noteDescription = dat.noteDescription,
                createdTime = dat.createdTime,
                modifiedTime = dat.modifiedTime,
                selected = false,
                favourite = false
            )
            scope.launch {
                folderRepository.insertNoteData(data)
            }
        }
    }
}