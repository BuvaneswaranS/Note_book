package com.example.note_book.displayFolderData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class displayFolderContentViewModelFactory(private val folderRepository: FolderRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(displayFolderContentViewModel::class.java)){
            return displayFolderContentViewModel(folderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}