package com.example.note_book.allNotesCard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class AllNotesCardViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllNotesCardViewModel::class.java)){
            return AllNotesCardViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}