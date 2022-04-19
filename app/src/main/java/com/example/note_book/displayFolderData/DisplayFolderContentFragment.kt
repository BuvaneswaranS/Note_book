package com.example.note_book.displayFolderData

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentDisplayFolderContentBinding

class DisplayFolderContentFragment : Fragment(R.layout.fragment_display_folder_content) {

    private lateinit var binding: FragmentDisplayFolderContentBinding
    private lateinit var viewModel: DisplayFolderContentViewModel
    private lateinit var displayNoteListAdapter: DisplayNoteListAdapter

    private lateinit var loadingDialog: AlertDialog

    var folderId: String = ""

    var folderName: String = ""

    var defaultid: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer {value ->
            if (value){
                binding.diaplayFolderContentToolbar.menu.clear()
                binding.diaplayFolderContentToolbar.inflateMenu(R.menu.menu)
                binding.diaplayFolderContentToolbar.setOnMenuItemClickListener{ menuItem ->
                    when(menuItem.itemId){
                        R.id.delete_button -> {
//                            Toast.makeText(this.context,"Delete button Clicked",Toast.LENGTH_LONG).show()
//                            Log.i("TestingApp","Delete Button Clicked")
                            var builder = AlertDialog.Builder(this.context)
                            builder.setTitle("Delete notes")
                            builder.setMessage("Do you want to delete?")
                            builder.setNegativeButton("No"){DialogInterface, which -> }
                            builder.setPositiveButton("Yes"){DialogInterface, which ->
                                viewModel.isEnabled.value = false
                                viewModel.checkedBoxClicked.value = false
                                viewModel.deleteSelectedItem()
                                viewModel.selectAndDeSelectAll(false)
                                val data = mutableListOf<String>()
                                viewModel.getNoteIdList(folderId)
                                displayNoteListAdapter.data = data
                                viewModel.selectedList.value = data
                                val emptyData = mutableListOf<NoteData>()
                                viewModel.notesListSelected = emptyData
                            }
                            var dialogBox = builder.create()
                            builder.show()
                            builder.setCancelable(false)
                            true
                        }
                        R.id.move_button -> {
                            if (viewModel.size <= 1){
                                var builder = AlertDialog.Builder(this.context)
                                builder.setTitle("Move Notes")
                                builder.setMessage("Don't seem to be another folder to move the note to ")
                                builder.setNegativeButton("Ok"){DialogInterface, which -> }
                                var dialogBox = builder.create()
                                builder.show()
                                builder.setCancelable(false)
                            }else if (viewModel.size > 1){
                                Log.i("TestingApp","${viewModel.defaultId}")
                                Navigation.findNavController(view).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToMoveFragment(folderId,folderName, displayNoteListAdapter.data.toTypedArray(),defaultid,"move"))
                                viewModel.isEnabled.value = false
                                viewModel.checkedBoxClicked.value = false
                                viewModel.selectAndDeSelectAll(false)
                                val data = mutableListOf<String>()
                                viewModel.getNoteIdList(folderId)
                                displayNoteListAdapter.data = data
                                viewModel.selectedList.value = data
                                val emptyData = mutableListOf<NoteData>()
                                viewModel.notesListSelected = emptyData
                            }
//                            Toast.makeText(this.context,"Move button Clicked",Toast.LENGTH_LONG).show()

                            true
                        }

                        R.id.copy_button -> {
//                                Log.i("TestingApp","${viewModel.defaultId}")
                                Navigation.findNavController(view).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToMoveFragment(folderId,folderName, displayNoteListAdapter.data.toTypedArray(),defaultid,"copy"))
                                viewModel.isEnabled.value = false
                                viewModel.checkedBoxClicked.value = false
                                viewModel.selectAndDeSelectAll(false)
                                val data = mutableListOf<String>()
                                viewModel.getNoteIdList(folderId)
                                displayNoteListAdapter.data = data
                                viewModel.selectedList.value = data
                                val emptyData = mutableListOf<NoteData>()
                                viewModel.notesListSelected = emptyData
                            true
                        }

                        else -> {
                            Toast.makeText(this.context,"Else Called",Toast.LENGTH_LONG).show()
                            Log.i("TestingApp","Else Called")
                            true
                        }
                    }
                }
            }else if(!value){
                binding.diaplayFolderContentToolbar.menu.clear()
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDisplayFolderContentBinding.inflate(inflater, container, false)

        binding.displayFolderContentBackArrowButton.setOnClickListener {
                requireActivity().onBackPressed()
        }
         val arguments = DisplayFolderContentFragmentArgs.fromBundle(requireArguments())

        folderId = arguments.displayFolderId
        folderName = arguments.displayFolderName
        Log.i("TestingApp","Arguments Id"+ arguments.defaultFolderId)

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

        viewModel.size = arguments.folderListSize
        Log.i("TestingApp","Folder List Size -> " + viewModel.size)

        defaultid= arguments.defaultFolderId
//        getSelectedToDeSelected()

        viewModel.viewModelCreated.observe(this.viewLifecycleOwner, Observer {created ->
            if(created){
                Log.i("TestingApp","Notes ViewModelCreated")
                viewModel.filesCame.observe(this.viewLifecycleOwner, Observer {filesCame ->
                    if (filesCame){
                        Log.i("TestingApp","Notes Files Came Created")
                        if (viewModel.donedeSelectingall.value == false){
                            Log.i("TestingApp","Notes done deSelecting all ")
                            viewModel.selectAndDeSelectAll(false)
                            viewModel.donedeSelectingall.value = true
                        }
                    }
                })
            }
        })

        val builder = AlertDialog.Builder(this.context)
        builder.setView(inflater.inflate(R.layout.loading_menu_moving, null))
        builder.setCancelable(true)
        loadingDialog = builder.create()


        if(viewModel.notesListFolder == null){
            val builder = AlertDialog.Builder(this.context)
            builder.setView(inflater.inflate(R.layout.loading_menu_moving, null))
            builder.setCancelable(true)
            loadingDialog = builder.create()
        }else {

        }

//        Log.i("TestingApp","All Items Selected - >"+viewModel.allItemsSelected.value.toString())

        viewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer {allSelected ->
//            Log.i("TestingApp","2. All Items Selected -> "+viewModel.allItemsSelected.value.toString())
            if (allSelected){
                binding.selectedBox.isChecked = true
            }else if (!allSelected){
                binding.selectedBox.isChecked = false
            }
        })

        displayNoteListAdapter = DisplayNoteListAdapter(viewModel)
        binding.displayFolderContentRecyclerView.adapter = displayNoteListAdapter
        binding.displayFolderContentRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
        binding.folderTitle.setText(arguments.displayFolderName)
        viewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->
            if(!list.isEmpty()){
                binding.displayNoNotesCard.visibility = View.INVISIBLE
                viewModel.filesCame.value = true
            }else if (list.isEmpty()){
                binding.displayNoNotesCard.visibility = View.VISIBLE
            }
            displayNoteListAdapter.submitList(list)
        })

        viewModel.getNoteIdList(arguments.displayFolderId)

        viewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer {allSelected ->
            if (allSelected){
                binding.selectedBox.isChecked = true
            }else if (!allSelected){
                binding.selectedBox.isChecked = false
            }
        })


//      Selection Enabled (OR) Not
        viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { value ->
            if(value == true){
//                Log.i("TestingApp","Checked Box - > " + binding.selectedBox.isChecked.toString())
                binding.selectedBox.visibility = View.VISIBLE
//                binding.deleteButton.visibility = View.VISIBLE
                binding.closeButton.visibility = View.VISIBLE
                binding.displayFolderContentBackArrowButton.visibility = View.INVISIBLE

                viewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->
                    if (viewModel.isEnabled.value == true){
//                binding.folderTitle.setText(" ${displayNoteListAdapter.data.size} selected")
                        binding.folderTitle.setText(" ${list.size} selected")
//                        Log.i("TestingApp","${list.size}")
                    }
                    val selectedListSize: Int = list.size
                    val entireListSize: Int? = viewModel.notesListFolder?.value?.size
                    viewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->
                        if (selectedListSize == list.size){
                            viewModel.allItemsSelected.value = true
                            binding.selectedBox.isChecked = true
                        }else{
                            viewModel.allItemsSelected.value = false
                            binding.selectedBox.isChecked = false
                        }
                    })
//                    if (selectedListSize == entireListSize){
//
//                    }else{
//
//                    }
//                    Log.i("TestingApp","Place 2 -> Checked Box - > " + binding.selectedBox.isChecked.toString())

                })


                binding.addNoteFolderButton.visibility = View.INVISIBLE

//                binding.folderTitle.setText(" ${displayNoteListAdapter.data.size + 1} selected")
//                binding.folderTitle.setText("${viewModel.selectedList.value?.size?.plus(1)} selected")
            }else if (value == false){

                binding.selectedBox.visibility = View.INVISIBLE
//                binding.deleteButton.visibility = View.INVISIBLE
                binding.closeButton.visibility = View.INVISIBLE
                binding.displayFolderContentBackArrowButton.visibility = View.VISIBLE
                binding.folderTitle.setText(arguments.displayFolderName)
                Log.i("TestingApp","Size of the List ${displayNoteListAdapter.data.size}")
                binding.addNoteFolderButton.visibility = View.VISIBLE
            }
        })

        viewModel.deletingStarted.observe(this.viewLifecycleOwner, Observer {deletingStartedValue ->

            if(deletingStartedValue == false){
                loadingDialog.dismiss()
            }else if(deletingStartedValue == true){
                val thread = Thread( Runnable {
                    loadingDialog.show()
                    Thread.sleep(5000)
                    loadingDialog.dismiss()
                })

            }
        })

//        viewModel.selectedAllItem.observe(this.viewLifecycleOwner, Observer { selected ->
//            if(selected){
//
//            }else if(!selected){
//                viewModel.deSelectAll()
//            }
//        })



//        viewModel.checkedBoxClicked.observe(this.viewLifecycleOwner, Observer { checkBox ->
//            if (checkBox == true){
//                binding.selectedBox.isChecked = true
//            }else if(checkBox == false){
//                binding.selectedBox.isChecked = false
//            }
//        })



//        CheckBox
        binding.selectedBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked){
                if(viewModel.allItemsSelected.value == false){
//                    Log.i("TestingApp","Entered true")
                    viewModel.selectedAllItem.value = true
                    viewModel.selectAndDeSelectAll(true)
                    var data = mutableListOf<String>()
                    viewModel.noteIdFolderList?.let { data.addAll(it) }

                    displayNoteListAdapter.data = data
                    viewModel.selectedList.value = data
                    viewModel.allItemsSelected.value = true
                }

            }else if (!isChecked){
                if (viewModel.allItemsSelected.value == true){
                    Log.i("TestingApp","Entered False")
                    viewModel.selectAndDeSelectAll(false)
                    val data = mutableListOf<String>()
                    displayNoteListAdapter.data = data
                    viewModel.selectedList.value = data
                    viewModel.allItemsSelected.value = false
                }
            }
        }



//     Close Button
        binding.closeButton.setOnClickListener {
            viewModel.isEnabled.value = false
            binding.selectedBox.isChecked = false
            viewModel.selectAndDeSelectAll(false)
            val data = mutableListOf<String>()
            val emptyData = mutableListOf<NoteData>()
            displayNoteListAdapter.data = data
            viewModel.selectedList.value = data
            viewModel.notesListSelected = emptyData
        }



//        Delete Button
//        binding.deleteButton.setOnClickListener {
//            viewModel.checkedBoxClicked.value = false
//            viewModel.deleteSelectedItem()
//            viewModel.selectAndDeSelectAll(false)
//            val data = mutableListOf<String>()
//            viewModel.getNoteIdList(arguments.displayFolderId)
//            displayNoteListAdapter.data = data
//            viewModel.selectedList.value = data
//        }


        return binding.root
    }

//    fun getSelectedToDeSelected(){
//        if (viewModel.isEnabled.value == false){
//            viewModel.selectAndDeSelectAll(false)
//            var data = mutableListOf<String>()
//            displayNoteListAdapter.data = data
//            viewModel.selectedList.value = data
//            viewModel.selectedItemsList = data
//            viewModel.checkedBoxClicked.value = false
//        }
//
//    }

//    override fun onDestroyView() {
//        if (viewModel.isEnabled.value == true){
//            viewModel.selectAndDeSelectAllLifeCycleChange(false)
//            var data = mutableListOf<String>()
//            displayNoteListAdapter.data = data
//            viewModel.selectedList.value = data
//            viewModel.selectedItemsList = data
//            viewModel.checkedBoxClicked.value = false
//        }
//        super.onDestroyView()
//    }

}

