package com.example.note_book.displayFolderData

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentDisplayFolderContentBinding

class DisplayFolderContentFragment : Fragment(R.layout.fragment_display_folder_content) {

    private lateinit var binding: FragmentDisplayFolderContentBinding
    private lateinit var viewModel: displayFolderContentViewModel
    private lateinit var displayNoteListAdapter: DisplayNoteListAdapter

    private lateinit var loadingDialog: AlertDialog

    var folderId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDisplayFolderContentBinding.inflate(inflater, container, false)

        binding.displayFolderContentBackArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        var arguments = DisplayFolderContentFragmentArgs.fromBundle(requireArguments())

        folderId = arguments.displayFolderId

        binding.addNoteFolderButton.setOnClickListener {view ->
            Navigation.findNavController(view).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToCreateNewNoteFragment(arguments.displayFolderId))
        }

        val application = requireNotNull(this.activity).application
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDao, noteDataDao)
        val viewModelFactory = displayFolderContentViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(displayFolderContentViewModel::class.java)

        viewModel.getNotesList(arguments.displayFolderId)

        if(viewModel.notesListFolder == null){
            val builder = AlertDialog.Builder(this.context)
            builder.setView(inflater.inflate(R.layout.loading_menu, null))
            builder.setCancelable(true)
            loadingDialog = builder.create()
//            loadingDialog.show()
        }else{
            loadingDialog.dismiss()
        }

        displayNoteListAdapter = DisplayNoteListAdapter()

        binding.displayFolderContentRecyclerView.adapter = displayNoteListAdapter

        binding.displayFolderContentRecyclerView.layoutManager = GridLayoutManager(this.context, 2)

        binding.folderTitle.setText(arguments.displayFolderName)

        viewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->
            displayNoteListAdapter.submitList(list)
        })

        return binding.root
    }

    fun stopLoading(){
        loadingDialog.dismiss()
    }

//    override fun onResume() {
//        viewModel.getNotesList(folderId)
//        super.onResume()
//    }
}