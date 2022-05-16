package com.example.note_book.allNotesCard

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class AllNotesCardViewModel(val application: Application): ViewModel() {

//  Declaration of the FolderRepository
    val folderRepository: FolderRepository

    var isEnabled = MutableLiveData<Boolean>()

    var checkedBoxClicked = MutableLiveData<Boolean>()

    var allItemsSelected = MutableLiveData<Boolean>()

    var selectedList = MutableLiveData<MutableList<String>>()

    var allNoteIdList: MutableList<String>? = null

    var deletingStarted = MutableLiveData<Boolean>()

    var selectedAllItem = MutableLiveData<Boolean>()

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

    //  For Detecting the number of the favourite items
    var favourite_item = MutableLiveData<Int>()

    //  For  Detecting the number of the unfavourite items
    var unfavourtie_item = MutableLiveData<Int>()

    //  For Detecting the state of the favourite [Make Favourite / Unfavourite / Nothing will be displayed]
    var favouriteState = MutableLiveData<String>()

    //  -------------------------------------------------------------
    var sortState = MutableLiveData<String>()

    lateinit var sortOrderA1 : LiveData<List<NoteData>>
    lateinit var sortOrderA2 : LiveData<List<NoteData>>
    lateinit var sortOrderA3 : LiveData<List<NoteData>>
    lateinit var sortOrderA4 : LiveData<List<NoteData>>
    lateinit var sortOrderA5 : LiveData<List<NoteData>>
    lateinit var sortOrderA6 : LiveData<List<NoteData>>
//  -----------------------------------------------------------------


    init {
        val allNotesfolderDao =
            NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val allNotesnoteDataDao =
            NotebookDatabase.getDatabaseInstance(application).noteDataDao
        folderRepository = FolderRepository(allNotesfolderDao, allNotesnoteDataDao)

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
        sortState.value = "A1"
//      ------------------------------------------
        getSortOrderA1()
        getSortOrderA2()
        getSortOrderA3()
        getSortOrderA4()
        getSortOrderA5()
        getSortOrderA6()
//     ------------------------------------------

    }

    fun getAllNotesList(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                notesListFolder = folderRepository.getAllNotesCard()
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
//        Log.i("TestingApp","Selected List Size --> ${selectedList.value?.size}")
//        Log.i("TestingApp","Selected List Size --> ${notesListFolder?.value?.size}")
        for(data in selectedList.value!!){
            for (dataNotes in notesListFolder?.value!!){
                if (data == dataNotes.noteId){
                    notesListSelected.add(dataNotes)
                }
            }
        }
    }

    fun selectAndDeSelectAll(value: Boolean){
        deletingStarted.value = true
        for (dat in notesListFolder?.value!!){
            if (value == true){
                val data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value)
                scope.launch {
                    folderRepository.updateNoteData(data)
                }
            }else if (value == false){
                val data = NoteData(noteId = dat.noteId, folderId = dat.folderId, noteTitle = dat.noteTitle, noteDescription = dat.noteDescription, createdTime = dat.createdTime, modifiedTime = dat.modifiedTime, selected = value)
                scope.launch {
                    folderRepository.updateNoteData(data)
                }
            }
        }
    }

    fun deleteSelectedItem(){
        deletingStarted.value = true
        for(data in selectedList.value!!){
//            Log.i("TestingApp","For Loop --> ${data}")
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

    fun makeFavourite(value: Boolean) {
//        Log.i("TestingApp","Selected Note Data Size --> ${notesListSelected.size}")
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

    fun getSortOrderA1(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                sortOrderA1 = folderRepository.getNoteListSortTitleAscending()
            }
        }
    }

    fun getSortOrderA2(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                sortOrderA2 = folderRepository.getNoteListSortTitleDescending()
            }
        }
    }

    fun getSortOrderA3(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                sortOrderA3 = folderRepository.getNoteListSortModifiedTimeAscending()
            }
        }
    }

    fun getSortOrderA4(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                sortOrderA4 = folderRepository.getNoteListSortModifiedTimeDescending()
            }
        }
    }

    fun getSortOrderA5(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                sortOrderA5 = folderRepository.getNoteListSortCreatedTimeAscending()
            }
        }
    }

    fun getSortOrderA6(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                sortOrderA6 = folderRepository.getNoteListSortCreatedTimeDescending()
            }
        }
    }
}