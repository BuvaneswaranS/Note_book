package com.example.note_book.moveNotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class MoveFragmentViewModelFactory(private val repository: FolderRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MoveFragmentViewModel::class.java)){
            return MoveFragmentViewModel(repository) as T

        }
        throw IllegalArgumentException("Unknown Model Class")
    }
}