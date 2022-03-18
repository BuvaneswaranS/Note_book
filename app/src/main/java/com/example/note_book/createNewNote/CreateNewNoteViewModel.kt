package com.example.note_book.createNewNote


import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateNewNoteViewModel(val folderRepository: FolderRepository): ViewModel() {

    private val viewModelJob = Job()

    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun insertNoteData(noteData: NoteData){
        scope.launch {
            folderRepository.insertNoteData(noteData)
        }
    }

    fun getDefaultFolderId(): String{
        var data: String = ""
        scope.launch {
            data = folderRepository.folderDataDao.getDefaultFolderId()
        }
        return data
    }

}