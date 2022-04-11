package com.example.note_book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class HomePageViewModelFactory(private val folderRepository: FolderRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)){
            return HomePageViewModel(folderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}