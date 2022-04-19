package com.example.note_book.moveNotes


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentMoveBinding
import java.util.*


class MoveFragment : Fragment(), moveItemClickListener {

//    Declaring the ViewBinding
    private lateinit var binding: FragmentMoveBinding

//    Declaring the ViewModel
    private lateinit var viewModel: MoveFragmentViewModel

//    Declaring the Adapter
    private lateinit var adapter: MoveFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        Initializing the View Binding
        binding = FragmentMoveBinding.inflate(inflater, container, false)

//        Get the Arguments
        val arguments = MoveFragmentArgs.fromBundle(requireArguments())
        Log.i("TestingApp","Move Fragment -> ${arguments.defaultFolderId}")

//        Log.i("TestingApp","Move Fragment Send List Size -> ${arguments.notesList.size}")


        val application = requireNotNull(this.activity).application

        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDao, noteDataDao)

//      Declaring the ViewModel Factory
        val viewModelFactory = MoveFragmentViewModelFactory(repository)

//      Initliazing the ViewModel
        viewModel = ViewModelProvider(this,viewModelFactory).get(MoveFragmentViewModel::class.java)

        viewModel.useType = arguments.fragmentType

        //      Set the Title of the Move Fragment
        if (arguments.fragmentType == "move"){
            binding.fragmentMoveTitle.setText("Move from "+ arguments.folderName)
            viewModel.getFolderListMove(arguments.folderId)

        }else if (arguments.fragmentType == "copy"){
            binding.fragmentMoveTitle.setText("Copy from "+ arguments.folderName)
            viewModel.getCopyFolderList()
        }

        viewModel.moveFolderId = arguments.folderId


//      ViewModel of selected List
        viewModel.selected.addAll(arguments.selectedList)
        Log.i("TestingApp","Selected Size${viewModel.selected.size}")
        viewModel.getNotes()


//      Back Button set onClickListener
        binding.fragmentMoveBackArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val builder = AlertDialog.Builder(this.context)
        if(arguments.fragmentType == "move"){
            builder.setView(inflater.inflate(R.layout.loading_menu_moving, null))
        }else if(arguments.fragmentType == "copy"){
            builder.setView(inflater.inflate(R.layout.layout_menu_copying, null))
        }

        val dialog = builder.create()

        viewModel.movingStarted.observe(this.viewLifecycleOwner, Observer {value ->
            if (value){
                dialog.show()
                val timer = Timer()
                timer.schedule(object : TimerTask(){
                    override fun run() {
                        dialog.dismiss()
                    }
                }, 1200)
            }
        })


//      Setting the Adapter
        adapter = MoveFragmentAdapter(viewModel,arguments.defaultFolderId,this)
        binding.fragmentMoveRecyclerview.layoutManager = GridLayoutManager(this.context, 2)
        binding.fragmentMoveRecyclerview.adapter = adapter

//      Folder List
        viewModel.folder_list?.observe(this.viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

        return binding.root
    }

    override fun moveFiles(folderData: FolderData) {
        viewModel.moveAllNotes(folderData.folderId)
        requireActivity().onBackPressed()
    }
}