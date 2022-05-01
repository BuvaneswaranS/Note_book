package com.example.note_book.moveNotes

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class MoveFragmentViewModel(val application: Application): ViewModel() {

//  Declaration of folderRepository
    private var folderRepository: FolderRepository

//  Declaration of Coroutine Job
    val job = Job()

//  Declaration of Coroutine Scope
    val scope = CoroutineScope(Dispatchers.IO + job)

//  To Check whether the moving/copying is started (or) Note
    var movingStarted = MutableLiveData<Boolean>()

//  Declaration and initialization of empty mutableList
//  This list will the initialized to the list of the selected items, we got using safeArgs
    var selected = mutableListOf<String>()

//  Folder id of the Folder in which the selected list should move from
    var moveFolderId: String = ""

//  Fodler id of the Folder in which the selected list should move to
    var moveFolderTo: String = ""

//  Declaration and Initialization of FolderData to null that has to be displayed to the user
    var folderList: LiveData<List<FolderData>>? = null

//  getNoteList -->  Have all the NoteData of the selected list of NoteId
    var getNoteList = mutableListOf<NoteData>()

//  Declaration and Initialization of useType
//  useType  --> type whether this fragment is used for [move/copy]
    var useType: String = ""

    init {
//      Declaration and Initialization of FolderDao
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao

//      Declaration and Initialization of NoteDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao

//      Initialization of FolderRepository
        folderRepository = FolderRepository(folderDao, noteDataDao)

//     movingStarted is initialized to false
        movingStarted.value = false
    }

//  Function to launch a coroutine for getting the list of FolderData for moving except the FolderData in which they came
    fun getFolderListMove(folderId: String){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                folderList = folderRepository.getFolderListMove(folderId)
            }
        }
    }

//  Function to launch a coroutine for getting the list of FolderData for Copy Operation
    fun getCopyFolderList(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                folderList = folderRepository.getFolderList()
            }
        }
    }

//  Function to launch a coroutine to Get all the NoteData from the selected list of NoteId
//  and add it to the  getNoteList[mutableList] variable
    fun getNotes(){
        for (data in selected){
            var noteData = NoteData("","","","",0,0,false)
            scope.launch {
                return@launch withContext(Dispatchers.IO){
                    getNoteList.add(folderRepository.getNotesData(data))

                }
            }
        }
    }

//  Function to Move / Copy  all the Note Data to the Folder
    fun moveAllNotes(folderId: String) {
//      For Each NoteData in the getNoteList
        for(data in getNoteList){

//          useType will be checked

//          If the useType is "move"
            if (useType == "move"){
//              Database delete Operation is done for all NoteData
                scope.launch {
                    folderRepository.deleteNoteDate(data.noteId)
                }
            }

//          Declaration and Initialization of NoteData
            val uploadNoteData = NoteData(folderId = folderId, noteTitle = data.noteTitle , noteDescription = data.noteDescription , createdTime = data.createdTime , modifiedTime = System.currentTimeMillis(), selected = false, favourite = data.favourite)

//          Database Insert Operation to insert all the selected NoteData to the database with the specific folderId
            scope.launch {
                folderRepository.insertNoteData(uploadNoteData)
            }
        }
    }
}
