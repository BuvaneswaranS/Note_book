package com.example.note_book

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NotebookDatabase


class HomePage : AppCompatActivity() {

//    Shared Preferences Name
    private val shared_pref = "sharedPref"

//    ViewModel declaration
    private lateinit var viewModel: HomePageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_home)

        val application = this.application
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDao, noteDataDao)
        var viewModelFactory = HomePageViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(HomePageViewModel::class.java)

        var sharedPreferences = getSharedPreferences(shared_pref,Context.MODE_PRIVATE)
        var shared = sharedPreferences.getBoolean("STARTED",false)
        viewModel.started.value = shared

        if (viewModel.started.value == false){
            viewModel.started.value = true
            var sharedPreference = getSharedPreferences(shared_pref, Context.MODE_PRIVATE)
            var editor: SharedPreferences.Editor = sharedPreference.edit()
            editor.putBoolean("STARTED",true)
            editor.apply()
            editor.commit()
            viewModel.updateDefaultFolder()
        }
    }
}