package com.example.note_book.displayFolderData

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class DisplayFolderContentViewModel(val application: Application): ViewModel() {

//  Declaration of the folder Repository
    val folderRepository: FolderRepository

//  For Detecting the Selection Enabled (or) Not
    var isEnabled = MutableLiveData<Boolean>()

//  For Detecting the Select all menu item is clicked (or) not
    var checkedBoxClicked = MutableLiveData<Boolean>()

//  For detecting whether all the items in the list is selected (or) not
    var allItemsSelected = MutableLiveData<Boolean>()

//  A LiveData of a List of selected NoteData NoteId of String
    var selectedList = MutableLiveData<MutableList<String>>()

//  A List of Selected Notedata
    var selectedListNoteData = MutableLiveData<MutableList<NoteData>>()

//  List of all NoteData NoteId of a separate folder
    var noteIdFolderList: MutableList<String>? = null


    var allNoteIdList: MutableList<String>? = null

    var deletingStarted = MutableLiveData<Boolean>()

    var selectedAllItem = MutableLiveData<Boolean>()

//    var isEnabled: Boolean = false

    var selectedItem = MutableLiveData<Boolean>()

    var defaultId: String = ""

//  Coroutine Job
    private val viewModelJob = Job()

//  Coroutine Scope
    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

     var notesListFolder: LiveData<List<NoteData>>? = null

//    var selectedItemsList = MutableLiveData<MutableList<String>>()

    var size: Int = 0

    var notesListSelected = mutableListOf<NoteData>()

    var noteListSelectedFav = mutableListOf<NoteData>()

    var filesCame = MutableLiveData<Boolean>()

    var viewModelCreated = MutableLiveData<Boolean>()

    var donedeSelectingall = MutableLiveData<Boolean>()

//  For Detecting the number of the favourite items
    var favourite_item = MutableLiveData<Int>()

//  For  Detecting the number of the unfavourite items
    var unfavourtie_item = MutableLiveData<Int>()

//  For Detecting the state of the favourite [Make Favourite / Unfavourite / Nothing will be displayed]
    var favouriteState = MutableLiveData<String>()

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
        favourite_item.value = 0
        unfavourtie_item.value = 0
        favouriteState.value = "makeFavourite"
        noteListSelectedFav.add(NoteData("","","","",0,0,false,false))

    }

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

    fun getAllNoteIdList(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                allNoteIdList = folderRepository.getAllNoteId()
            }
        }
    }

    fun getNotesList(){
        for(data in selectedList.value!!){
            for (dataNotes in notesListFolder?.value!!){
                if (data == dataNotes.noteId){
                    notesListSelected.add(dataNotes)
                }
            }
        }
    }


//  For Selecting and  Deselecting all the items in the list of recyclerview Items
    fun selectAndDeSelectAll(value: Boolean){
        deletingStarted.value = true
        for (dat in notesListFolder?.value!!){
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

    fun makeFavourite(value: Boolean) {
        for (dat in notesListSelected) {
            if (value){
                val data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = false, favourite = true)
                scope.launch {
                    folderRepository.updateFavouriteNoteData(data)
                }
            }else if (!value){
                val data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = false, favourite = false)

                scope.launch {
                    folderRepository.updateUnFavouriteNoteData(data)
                }
            }
        }
    }

}