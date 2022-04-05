package com.example.note_book.createNewNote

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentCreateNewNoteBinding


class CreateNewNoteFragment : Fragment(R.layout.fragment_create_new_note) {

    private lateinit var binding: FragmentCreateNewNoteBinding

    private lateinit var viewModel: CreateNewNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_new_note, container, false)

        binding = FragmentCreateNewNoteBinding.inflate(inflater, container, false)

        var arguments = CreateNewNoteFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val folderDataDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDataDao,noteDataDao)
        val viewModelFactory = CreateNewNoteViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(CreateNewNoteViewModel::class.java)

        binding.createNoteBackArrowButton.setOnClickListener {
//            Log.i("TestingApp","Back Arrow Button Clicked")
            requireActivity().onBackPressed()
        }
//        viewModel.getDefaultFolderId()

        var description: String = ""

        binding.noteDescription.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }


        binding.createNoteDoneButton.setOnClickListener {
            var title: String = binding.createNoteTitleInput.text.toString()
//            val description: String = binding.noteDescription.text.toString()


            if(title.isEmpty() && description.isEmpty()){
                Toast.makeText(this.context,"Title and Description is Empty",Toast.LENGTH_LONG).show()
//                Log.i("TestingApp","Message is Empty")

            }else if((title.isEmpty() == true) && (description.isEmpty() == false)){
                title = "Default Title"

                val folderId: String = arguments.noteFolderId



//                Log.i("TestingApp","----------------------------")
//                Log.i("TestingApp","NoteTitle -> ${title}")
//                Log.i("TestingApp","NoteDescription -> ${description}")
//                Log.i("TestingApp","Inserted Successfully")
                val data = NoteData(folderId = folderId,noteTitle = title, noteDescription = description,createdTime = System.currentTimeMillis(), modifiedTime = System.currentTimeMillis())
                viewModel.insertNoteData(data)
                requireActivity().onBackPressed()
            } else{
                val folderId: String = arguments.noteFolderId
//                Log.i("TestingApp","----------------------------")
//                Log.i("TestingApp","NoteTitle -> ${title}")
//                Log.i("TestingApp","NoteDescription -> ${description}")
//                Log.i("TestingApp","Inserted Successfully")
                val data = NoteData(folderId = folderId,noteTitle = title, noteDescription = description,createdTime = System.currentTimeMillis(), modifiedTime = System.currentTimeMillis())
                viewModel.insertNoteData(data)

                requireActivity().onBackPressed()
            }

        }
        return binding.root
    }


}