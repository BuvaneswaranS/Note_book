package com.example.note_book.Sort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SortViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SortViewModel::class.java)){
            return SortViewModel() as T
        }
        throw IllegalArgumentException("Unknwon viewModel class")
    }
}