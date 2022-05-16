package com.example.note_book.Search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class SearchViewModel(val application: Application): ViewModel() {

    //  Coroutine Job Declaration
    private val coroutineJob = Job()

    //  Coroutine Scope Declaration
    private val coroutineScope = CoroutineScope(Dispatchers.IO + coroutineJob)

    //  Folder Repository Declaration
    private var folderRepository: FolderRepository

    //  All Note Data
    var listNoteData: LiveData<List<NoteData>>?= null

    var changeFragment: String = "NotChanged"

    //  All Folder Data
    lateinit var listFolderData: LiveData<List<FolderData>>

    //  Declaration and Initialization of defaultFolderId to null
    var defaultFolderId: String? = null

    var searchText = MutableLiveData<String>()

    var setSearchViewToEmpty = MutableLiveData<Boolean>()

    init {
//      Declaration and Initialization of folderDao
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao

//      Declaration and Initialization of noteDao
        val noteDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao

//      Initialization of folderRepository
        folderRepository = FolderRepository(folderDao, noteDao)

//        Get defaultFolderId
        getDefaultFolderId()

//      Initialization of the list of NoteData
        getAllNoteData()

//      Initialization of the list of FolderData
        getAllSearchFolderData()

        searchText.value = ""

        setSearchViewToEmpty.value = false

    }

    var searchNoteData = folderRepository.getSearchNoteData().asLiveData()

    var searchFolderData = folderRepository.getSearchData().asLiveData()

    //  Function to get Default Folder Id
    fun getDefaultFolderId() {
        coroutineScope.launch {
            return@launch withContext(Dispatchers.IO) {
                defaultFolderId = folderRepository.getDefaultFolderId().folderId
            }
        }
    }


//      Function to get all the noteData
    fun getAllNoteData() {
        coroutineScope.launch {
            return@launch withContext(Dispatchers.IO) {
                listNoteData = folderRepository.getAllNotesCard()

            }
        }
    }
        //  Function to get all the folderData
        fun getAllSearchFolderData() {
            coroutineScope.launch {
                return@launch withContext(Dispatchers.IO) {
                    listFolderData = folderRepository.getFolderList()
                }
            }
        }

    fun searchNoteDatabase(searchText: String): LiveData<List<NoteData>>{
        return folderRepository.getSearchNoteData(searchText).asLiveData()
    }

    fun searchFolderDatabase(searchText: String): LiveData<List<FolderData>>{
        return folderRepository.getSearchFolderData(searchText)
    }

}