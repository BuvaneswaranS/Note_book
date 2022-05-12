package com.example.note_book.updateNote

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.HomePageActivity
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentUpdateNoteBinding

class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

//  ViewBinding Declaration
    private lateinit var binding: FragmentUpdateNoteBinding

//  ViewModel Declaration
    private lateinit var viewModel: UpdateNoteViewModel

    //  Declaration and Initialzation  of the SharedPreference
    private val selected = "sharedSelect"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//      ViewBinding Initializition
        binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)

//        Back Arrow Button OnClickListener to request to the fragment to move back
        binding.updateNoteBackArrowButton.setOnClickListener {

//          To make the supportActionBar to display to the user
            (requireActivity() as AppCompatActivity).supportActionBar?.show()

//           onBackPressed is called to move back
//            requireActivity().onBackPressed()
            (activity as HomePageActivity).onSupportNavigateUp()
            (activity as HomePageActivity).drawerUnLocked()
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        }

        val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("SELECTED",false)
        editor?.apply()
        editor?.commit()

//      Declaration and Initializing of SafeArgs Arguments
        val arguments = UpdateNoteFragmentArgs.fromBundle(requireArguments())

//      Declaration and Initialization of Application
        val application = requireNotNull(this.activity).application

//      Declaration of Initialization of ViewModelFactory
        val viewModelFactory = UpdateNoteViewModelFactory(application)

//      ViewModel Initializing
        viewModel = ViewModelProvider(this,viewModelFactory).get(UpdateNoteViewModel::class.java)

//      Get the Folder Data
        viewModel.getFolderData(arguments.updateNoteFolderId)

//        Set the Note Title
        binding.updateNoteTitle.setText(arguments.updateNoteNoteTitle)

//        Set the Note Description
        binding.updateNoteDescription.setText(arguments.updateNoteNoteDescription)

//      Description is initialized as Empty
        var description = ""

//      Set the Navigation drawer in Lock State
        (activity as HomePageActivity).drawerLocked()

//      set the supportActionBar to Hide
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

//      "startChanging" a Boolean is declared and initialzed to False at initial State --> TO CHECK WHETHER THE DESCRIPTION EDITTEXT GOT CHANGED
        var startChanging = false

//      Getting the Text for the Description
        binding.updateNoteDescription.doOnTextChanged { text, start, before, count ->

//          "startChanging" is initialized to true
            startChanging = true

//          Everytime the input is changed, It was initialize to the description
            description = text.toString()

        }

//      Done Button to add the updated note to the DATABASE
        binding.updateNoteButton.setOnClickListener {

//          If the description is not changed. the description is initialized as the description which came through safe args
            if (!startChanging){

                description = arguments.updateNoteNoteDescription

            }

            val updateNoteData = NoteData(noteId = arguments.updateNoteId, folderId = arguments.updateNoteFolderId, noteTitle = binding.updateNoteTitle.text.toString(), noteDescription = description, createdTime = arguments.updateNoteNoteCreatedTime, modifiedTime = System.currentTimeMillis(), favourite = arguments.favourite)

//          Function call in the viewModel to update the Note
            viewModel.updateNoteData(updateNoteData)

//          Set the supportActionBar  to show it to the user
            (requireActivity() as AppCompatActivity).supportActionBar?.show()

//            requireActivity().onBackPressed()

            (activity as HomePageActivity).onSupportNavigateUp()
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()

//          onDestroyView is called
//            onDestroyView()
            (activity as HomePageActivity).drawerUnLocked()
        }

        return binding.root
    }

//  onDestroyView
    override fun onDestroyView() {
//      To unLock the navigation drawer when the updateNoteFragment is destroyed
        (activity as HomePageActivity).drawerUnLocked()
        super.onDestroyView()
    }

}