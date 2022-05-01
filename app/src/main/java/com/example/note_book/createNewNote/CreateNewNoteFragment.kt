package com.example.note_book.createNewNote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentCreateNewNoteBinding

class CreateNewNoteFragment : Fragment(R.layout.fragment_create_new_note) {

//  ViewBinding Declaration
    private lateinit var binding: FragmentCreateNewNoteBinding

//  ViewModel Declaration
    private lateinit var viewModel: CreateNewNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//      Initializing of the ViewBinding
        binding = FragmentCreateNewNoteBinding.inflate(inflater, container, false)

//      Declaration of arguments that has been got using Safe Args in Navigation
        val arguments = CreateNewNoteFragmentArgs.fromBundle(requireArguments())

//      Declaration and Initialization of application
        val application = requireNotNull(this.activity).application

//      Declaration and Initialization of ViewModelFactory
        val viewModelFactory = CreateNewNoteViewModelFactory(application)

//      Initialization of ViewModel
        viewModel = ViewModelProvider(this,viewModelFactory).get(CreateNewNoteViewModel::class.java)

//      Set the supportActionBar to Hide
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

//      OnClickListener for the Back Arrow Button
        binding.createNoteBackArrowButton.setOnClickListener {

//          When the back Arrow button is clicked supportActionBar is set to show to the user
            (requireActivity() as AppCompatActivity).supportActionBar?.show()

            requireActivity().onBackPressed()

        }

//      Declaration of a Empty description
        var description = ""

//      For every time, the text is initialize to the description
        binding.noteDescription.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }

//      onClickListener for the Done Button
        binding.createNoteDoneButton.setOnClickListener {

//          Title is declared and initialized from the data of EditText Input
            var title: String = binding.createNoteTitleInput.text.toString()

//          If both the title and the description is Empty --> Toast message to display that Title and the Description is Empty
            if(title.isEmpty() && description.isEmpty()){

                Toast.makeText(this.context,"Title and Description is Empty",Toast.LENGTH_LONG).show()

            }

//          If Title is Empty AND Description is not Empty --> "Default- Title" is set as a Title during inserting
            else if((title.isEmpty() == true) && (description.isEmpty() == false)){

//              if the title is not set by the user, "DefaultTitle" will be set as the title of the note
                title = "Default Title"

                val folderId: String = arguments.noteFolderId

                val data = NoteData(folderId = folderId,noteTitle = title, noteDescription = description,createdTime = System.currentTimeMillis(), modifiedTime = System.currentTimeMillis())

                viewModel.insertNoteData(data)

                (requireActivity() as AppCompatActivity).supportActionBar?.hide()

                requireActivity().onBackPressed()

            }

//          If Both the Title and Description is Not Empty --> Insert the Note Data to the Database
            else{

                val folderId: String = arguments.noteFolderId

                val data = NoteData(folderId = folderId,noteTitle = title, noteDescription = description,createdTime = System.currentTimeMillis(), modifiedTime = System.currentTimeMillis())

                viewModel.insertNoteData(data)
                (requireActivity() as AppCompatActivity).supportActionBar?.hide()
                    requireActivity().onBackPressed()
            }

        }

        return binding.root
    }

}