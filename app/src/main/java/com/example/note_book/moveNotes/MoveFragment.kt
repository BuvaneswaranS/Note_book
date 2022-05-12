package com.example.note_book.moveNotes

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.HomeFragment.HomeFragmentDirections
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentMoveBinding
import java.util.*

class MoveFragment : Fragment(), moveItemClickListener {

//    Declaring the ViewBinding
    private lateinit var binding: FragmentMoveBinding

//    Declaring the ViewModel
    private lateinit var viewModel: MoveFragmentViewModel

//    Declaring the Adapter for the recyclerView
    private lateinit var adapter: MoveFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        Initializing the View Binding
        binding = FragmentMoveBinding.inflate(inflater, container, false)

//        Get the Arguments
        val arguments = MoveFragmentArgs.fromBundle(requireArguments())
        Log.i("TestingApp","Move Fragment -> ${arguments.defaultFolderId}")

//      set the supportActionBar to hide
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

//      Declaration and Initialization of Application
        val application = requireNotNull(this.activity).application

//      Declaring the ViewModel Factory
        val viewModelFactory = MoveFragmentViewModelFactory(application)

//      Initliazing the ViewModel
        viewModel = ViewModelProvider(this,viewModelFactory).get(MoveFragmentViewModel::class.java)

//      Get the fragmentType[move/copy] and initialize to useType variable in the viewModel
        viewModel.useType = arguments.fragmentType

//      Set the Title of the Move Fragment
//      If the fragmentType is "Move"
        if (arguments.fragmentType == "move"){
            if (arguments.folderName == "allNoteCards"){
                binding.fragmentMoveTitle.setText("Move from All Note Cards")
                viewModel.getCopyFolderList()
            }else if (arguments.folderName != "allNoteCards"){
                binding.fragmentMoveTitle.setText("Move from "+ arguments.folderName)
                viewModel.getFolderListMove(arguments.folderId)
            }


        }
//      If the fragmentType is "copy"
        else if (arguments.fragmentType == "copy"){
            if (arguments.folderName == "allNoteCards"){
                binding.fragmentMoveTitle.setText("Copy from All Note Cards")
            }else if (arguments.folderName != "allNoteCards"){
                binding.fragmentMoveTitle.setText("Copy from "+ arguments.folderName)
            }
            viewModel.getCopyFolderList()
        }

        viewModel.moveFolderId = arguments.folderId


//      ViewModel of selected List
        viewModel.selected.addAll(arguments.selectedList)
//        Log.i("TestingApp","Selected Size${viewModel.selected.size}")
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
//        var player= MediaPlayer.create(this.context, R.raw.sound)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogBoxAnimation
        viewModel.movingStarted.observe(this.viewLifecycleOwner, Observer {value ->
            if (value){
//                player.start()
                dialog.show()
                val timer = Timer()
                timer.schedule(object : TimerTask(){
                    override fun run() {
                        dialog.dismiss()
                    }
                }, 1300)
            }
        })


//      Setting the Adapter
        adapter = MoveFragmentAdapter(viewModel,arguments.defaultFolderId,this)

//      Setting the type of the layoutMananger of the RecyclerView
        binding.fragmentMoveRecyclerview.layoutManager = GridLayoutManager(this.context, 2)

//      Setting the adapter to the recyclerView
        binding.fragmentMoveRecyclerview.adapter = adapter

//      Folder List has been inserted to the recyclerView Adapter
        viewModel.folderList?.observe(this.viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.move_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.move_search_button){
            view?.let { Navigation.findNavController(it).navigate(MoveFragmentDirections.actionMoveFragmentToSearchFragment()) }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun moveFiles(folderData: FolderData) {

//      Function call in the viewModel to move/copy all the notes to the specific folder
        viewModel.moveAllNotes(folderData.folderId)

//      Set the supportActionBar to show to the user
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        requireActivity().onBackPressed()
    }
}