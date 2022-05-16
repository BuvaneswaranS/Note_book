package com.example.note_book.Sort

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SortViewModel(): ViewModel() {

    var sortOrder = MutableLiveData<String>()
    var sortOrderTemp = MutableLiveData<String>()

    init {
        sortOrder.value = "A1"
        sortOrderTemp.value = "A1"
    }
}