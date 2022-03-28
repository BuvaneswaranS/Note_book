package com.example.note_book.updateNote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class UpdateNoteViewModelFactory(private val folderRepository: FolderRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateNoteViewModel::class.java)){
            return UpdateNoteViewModel(folderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}