package com.example.note_book.moveNotes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import kotlinx.coroutines.*

class MoveFragmentViewModel(val folderRepository: FolderRepository): ViewModel() {

    var noteListFolder = MutableLiveData<MutableList<FolderData>>()

    var movingStarted = MutableLiveData<Boolean>()

//    lateinit var folder_list: LiveData<List<FolderData>>
    var selected = mutableListOf<String>()
    var moveFolderId: String = ""

    val job = Job()
    val scope = CoroutineScope(Dispatchers.IO + job)


    var folder_list: LiveData<List<FolderData>>? = null

    var getNoteList = mutableListOf<NoteData>()

    init {
//        folder_list = folderRepository.folderDataDao.getAllDataByFolderMove()
        movingStarted.value = false
    }

    fun getFolderListMove(folderId: String){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                folder_list = folderRepository.getFolderListMove(folderId)
            }
        }
    }

    fun getNotes(){
        for (data in selected){
            var noteData = NoteData("","","","",0,0,false)
            scope.launch {
                return@launch withContext(Dispatchers.IO){
                    getNoteList.add(folderRepository.getNotesData(data))

                }
            }
            Log.i("TestingApp","Size -> "+ getNoteList.size )
            Log.i("TestingApp","Note Id -> "+ noteData.noteId)
            Log.i("TestingApp","Folder Id -> "+ noteData.folderId)
            Log.i("TestingApp","Note Title -> "+ noteData.noteTitle)
            Log.i("TestingApp","Note Description -> "+ noteData.noteDescription)
        }
    }

    fun moveAllNotes(folderId: String) {
        Log.i("TestingApp","Size of the noteList -> ${getNoteList.size}")
        Log.i("TestingApp","Size -> "+ getNoteList.size )
        Log.i("TestingApp","Note Id -> "+ getNoteList[0].noteId)
        Log.i("TestingApp","Folder Id -> "+ getNoteList[0].folderId)
        Log.i("TestingApp","Note Title -> "+ getNoteList[0].noteTitle)
        Log.i("TestingApp","Note Description -> "+ getNoteList[0].noteDescription)
        for(data in getNoteList){
            scope.launch {
                folderRepository.deleteNoteDate(data.noteId)
            }
            var uploadNoteData = NoteData(
                folderId = folderId,
                noteTitle = data.noteTitle ,
                noteDescription = data.noteDescription ,
                createdTime = data.createdTime ,
                modifiedTime = System.currentTimeMillis(),
                selected = false
            )
            scope.launch {
                folderRepository.insertNoteData(uploadNoteData)
            }
        }
    }
}
