package com.example.note_book.favourite

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.Appendable
import java.lang.IllegalArgumentException

class FavouriteViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)){
            return FavouriteViewModel(application) as T
        }
        throw IllegalArgumentException("unknown ViewModel Class")
    }
}