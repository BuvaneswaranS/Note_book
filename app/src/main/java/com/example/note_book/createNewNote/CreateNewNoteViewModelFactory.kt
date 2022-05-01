package com.example.note_book.createNewNote

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class CreateNewNoteViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CreateNewNoteViewModel::class.java)){
            return CreateNewNoteViewModel(application) as T
        }

        throw IllegalArgumentException("UnKnown ViewModel Class")
    }
}