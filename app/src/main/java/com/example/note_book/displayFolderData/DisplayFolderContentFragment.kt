package com.example.note_book.displayFolderData

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
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
    private lateinit var viewModel: DisplayFolderContentViewModel
    private lateinit var displayNoteListAdapter: DisplayNoteListAdapter

    private lateinit var loadingDialog: AlertDialog

    var folderId: String = ""

    @SuppressLint("SetTextI18n")
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

        viewModel = ViewModelProvider(this, viewModelFactory).get(DisplayFolderContentViewModel::class.java)

        viewModel.getNotesList(arguments.displayFolderId)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(inflater.inflate(R.layout.loading_menu, null))
        builder.setCancelable(true)
        loadingDialog = builder.create()


        if(viewModel.notesListFolder == null){
            val builder = AlertDialog.Builder(this.context)
            builder.setView(inflater.inflate(R.layout.loading_menu, null))
            builder.setCancelable(true)
            loadingDialog = builder.create()
//            loadingDialog.show()
        }else{
//            loadingDialog.dismiss()
        }

        displayNoteListAdapter = DisplayNoteListAdapter(viewModel)
//        binding.displayFolderContentBackArrowButton.setOnClickListener {
//            if(viewModel.isEnabled.value == false){
//
//            }else{
//                requireActivity().onBackPressed()
//            }
//
//        }

        binding.displayFolderContentRecyclerView.adapter = displayNoteListAdapter

        binding.displayFolderContentRecyclerView.layoutManager = GridLayoutManager(this.context, 2)

        binding.folderTitle.setText(arguments.displayFolderName)

        viewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->
            displayNoteListAdapter.submitList(list)
        })


//        viewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->
//            if (viewModel.isEnabled.value == true){
////                binding.folderTitle.setText(" ${displayNoteListAdapter.data.size} selected")
//                binding.folderTitle.setText(" ${list.size} selected")
//            }
//        })


        viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { value ->
            if(value == true){
                binding.selectedBox.visibility = View.VISIBLE
                binding.deleteButton.visibility = View.VISIBLE
                binding.closeButton.visibility = View.VISIBLE
                binding.displayFolderContentBackArrowButton.visibility = View.INVISIBLE

                viewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->
                    if (viewModel.isEnabled.value == true){
//                binding.folderTitle.setText(" ${displayNoteListAdapter.data.size} selected")
                        binding.folderTitle.setText(" ${list.size} selected")
                    }
                })

//                binding.folderTitle.setText(" ${displayNoteListAdapter.data.size + 1} selected")
//                binding.folderTitle.setText("${viewModel.selectedList.value?.size?.plus(1)} selected")
            }else if (value == false){
                binding.selectedBox.visibility = View.INVISIBLE
                binding.deleteButton.visibility = View.INVISIBLE
                binding.closeButton.visibility = View.INVISIBLE
                binding.displayFolderContentBackArrowButton.visibility = View.VISIBLE
                binding.folderTitle.setText(arguments.displayFolderName)
                Log.i("TestingApp","Size of the List ${displayNoteListAdapter.data.size}")
            }
        })

        viewModel.deletingStarted.observe(this.viewLifecycleOwner, Observer {deletingStartedValue ->
            if(deletingStartedValue == false){
                loadingDialog.dismiss()
            }else if(deletingStartedValue == true){
                loadingDialog.show()
            }
        })

        binding.closeButton.setOnClickListener {
            viewModel.isEnabled.value = false
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteSelectedItem()
            var data = mutableListOf<String>()
            displayNoteListAdapter.data = data
            viewModel.selectedList.value = data
        }
        return binding.root
    }
}