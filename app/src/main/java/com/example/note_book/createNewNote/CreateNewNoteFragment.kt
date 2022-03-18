package com.example.note_book.createNewNote

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentCreateNewNoteBinding


class CreateNewNoteFragment : Fragment() {

    private lateinit var binding: FragmentCreateNewNoteBinding

    private lateinit var viewModel: CreateNewNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_new_note, container, false)

        val application = requireNotNull(this.activity).application
        val folderDataDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDataDao,noteDataDao)
        val viewModelFactory = CreateNewNoteViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(CreateNewNoteViewModel::class.java)

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.doneButton.setOnClickListener {
            val title: String = binding.noteTitle.text.toString()
            val description: String = binding.noteDescription.text.toString()
            val folderId: String = viewModel.getDefaultFolderId()

            val data = NoteData(folderId = folderId,noteTitle = title, noteDescription = description,createdTime = System.currentTimeMillis(), modifiedTime = System.currentTimeMillis())
            viewModel.insertNoteData(data)
            Log.i("TestingApp","Inserted Successfully")
            requireActivity().onBackPressed()
        }

        return binding.root
    }


}