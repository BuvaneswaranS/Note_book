package com.example.note_book.updateNote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentUpdateNoteBinding

class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

//    ViewBinding Declaration
    private lateinit var binding: FragmentUpdateNoteBinding

//    ViewModel Declaration
    private lateinit var viewModel: UpdateNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)

//        Back Arrow Button OnClickListener to request to the fragment to move back
        binding.updateNoteBackArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        var arguments = UpdateNoteFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDao, noteDataDao)
        val viewModelFactory = UpdateNoteViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(UpdateNoteViewModel::class.java)

//      Get the Folder Data
        viewModel.getFolderData(arguments.updateNoteFolderId)

//        Set the Note Title
        binding.updateNoteTitle.setText(arguments.updateNoteNoteTitle.toString())

//        Set the Note Description
        binding.updateNoteDescription.setText(arguments.updateNoteNoteDescription.toString())

        var description: String = ""

        binding.updateNoteDescription.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }

//      Done Button to add the updated note to the DATABASE
        binding.updateNoteButton.setOnClickListener {view ->
            val updateNoteData = NoteData(noteId = arguments.updateNoteId, folderId = arguments.updateNoteFolderId, noteTitle = binding.updateNoteTitle.text.toString(), noteDescription = description, createdTime = arguments.updateNoteNoteCreatedTime, modifiedTime = System.currentTimeMillis())
            viewModel.updateNoteData(updateNoteData)
//            Navigation.findNavController(view).navigate(R.id.action_displayFolderContentFragment_to_updateNoteFragment)
            requireActivity().onBackPressed()
        }

        return binding.root
    }
}