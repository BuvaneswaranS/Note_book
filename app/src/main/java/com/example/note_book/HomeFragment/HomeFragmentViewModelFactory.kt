package com.example.note_book.HomeFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import java.lang.IllegalArgumentException

class HomeFragmentViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)){
            return HomeFragmentViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown Model Class")
    }
}