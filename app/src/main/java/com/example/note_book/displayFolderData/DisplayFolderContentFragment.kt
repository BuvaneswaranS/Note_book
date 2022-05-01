package com.example.note_book.displayFolderData

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.HomePageActivity
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentDisplayFolderContentBinding

class DisplayFolderContentFragment : Fragment(R.layout.fragment_display_folder_content) {

//  ViewBinding Declaration
    private lateinit var binding: FragmentDisplayFolderContentBinding

//  ViewModel Declaration
    private lateinit var viewModel: DisplayFolderContentViewModel

//  Adapter Declaration
    private lateinit var displayNoteListAdapter: DisplayNoteListAdapter

    private lateinit var loadingDialog: AlertDialog

//  Declaration and Initialzation  of the SharedPreference
    private val selected = "sharedSelect"

//  Initializing the FolderId to a Variable "folderId"
    var folderId: String = ""

//  Initializing the Folder Name to a Variable "folderName"
    var folderName: String = ""

//  Initialiing the Default Folder Id to a variable "defaultid"
    var defaultid: String = ""

    val shared_value = "drawer_shared"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//      For Setting the Options Menu
        setHasOptionsMenu(true)
    }



    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//      Initializing the ViewBinding
        binding = FragmentDisplayFolderContentBinding.inflate(inflater, container, false)

        val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("IN",false)
        editor?.apply()
        editor?.commit()

        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        (activity as HomePageActivity).drawerLocked()

//  -------------------------------------------------------------------------------------------------------------------

//      Safe Args Declaration
        val arguments = DisplayFolderContentFragmentArgs.fromBundle(requireArguments())

//      folderId in the arguments[Safe Args] is initialized to the "folderId" variable
        folderId = arguments.displayFolderId

//      folderName in the arguments[Safe Args] is initialized to the "folderName" variable
        folderName = arguments.displayFolderName

//      defaultFolderId in the arguments[Safe Args] is initialized to the "defaultid" variable
        defaultid= arguments.defaultFolderId

//      Setting the Title of the Toolbar from the safeArgs arguments



//      Add Note OnClickListener
        binding.addNoteFolderButton.setOnClickListener {view ->
            Navigation.findNavController(view).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToCreateNewNoteFragment(arguments.displayFolderId))
        }

        val application = requireNotNull(this.activity).application

//     Declaration and Initialization of ViewModel Factory
        val viewModelFactory = displayFolderContentViewModelFactory(application)

//      viewModel is initialized
        viewModel = ViewModelProvider(this, viewModelFactory).get(DisplayFolderContentViewModel::class.java)

//
        viewModel.getNotesList(arguments.displayFolderId)

//
        viewModel.size = arguments.folderListSize

//      Initializing the Adapter[displayNoteListAdapter]
        displayNoteListAdapter = DisplayNoteListAdapter(viewModel)

//      Setting the RecyclerView's Adapter as the Adapter[displayNoteListAdapter]
        binding.displayFolderContentRecyclerView.adapter = displayNoteListAdapter

//      Setting the Recyclerview --> LayoutManager
        binding.displayFolderContentRecyclerView.layoutManager = GridLayoutManager(this.context, 2)


        viewModel.viewModelCreated.observe(this.viewLifecycleOwner, Observer {created ->

            if(created){

                viewModel.filesCame.observe(this.viewLifecycleOwner, Observer {filesCame ->

                    if (filesCame){

                        if (viewModel.donedeSelectingall.value == false){

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



        viewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->

            if(!list.isEmpty()){

                binding.displayNoNotesCard.visibility = View.INVISIBLE

                viewModel.filesCame.value = true

            }else if (list.isEmpty()){

                binding.displayNoNotesCard.visibility = View.VISIBLE

            }

            displayNoteListAdapter.submitList(list)

        })

        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(arguments.displayFolderName)

        val drawerLayout = view?.findViewById<DrawerLayout>(R.id.drawerLayoutHome)

        drawerLayout?.isEnabled = false

        viewModel.getNoteIdList(arguments.displayFolderId)

        viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer {value ->

            if (value){

                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",true)
                editor?.apply()
                editor?.commit()


                viewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->

                    if (viewModel.isEnabled.value == true){

//                        binding.diaplayFolderContentToolbar.setTitle(" ${list.size} selected")
                        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("${list.size} selected")

                    }

                    val selectedListSize: Int = list.size
                    val entireListSize: Int? = viewModel.notesListFolder?.value?.size
                    viewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->

                        if (selectedListSize == list.size){

                            viewModel.allItemsSelected.value = true

                        }

                        else{

                            viewModel.allItemsSelected.value = false

                        }

                    })

                })

            }

            else if(!value){

                (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(arguments.displayFolderName)

            }

        })

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (viewModel.isEnabled.value == true){
                    viewModel.isEnabled.value = false
                    viewModel.checkedBoxClicked.value = false
                    viewModel.selectAndDeSelectAll(false)
                    val data = mutableListOf<String>()
                    displayNoteListAdapter.data = data
                    viewModel.selectedList.value = data
                    val emptyData = mutableListOf<NoteData>()
                    viewModel.notesListSelected = emptyData
                    goBack()

                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)


        viewModel.favourite_item.observe(this.viewLifecycleOwner, Observer { favouriteItems ->
            viewModel.unfavourtie_item.observe(this.viewLifecycleOwner, Observer {unFavourite ->
                if ((favouriteItems == 0) && (unFavourite == 0)){

                    viewModel.favouriteState.value = "makeFavourite"

                }
                else if ((favouriteItems > 0) && (unFavourite == 0)){

                    viewModel.favouriteState.value = "unFavourite"

                }else if ((favouriteItems == 0) && (unFavourite > 0)){

                    viewModel.favouriteState.value = "makeFavourite"

                }else if ((favouriteItems > 0) && (unFavourite > 0)){
                    viewModel.favouriteState.value = "Nothing"

                }
            })
        })


        viewModel.favouriteState.observe(this.viewLifecycleOwner, Observer { value ->
            if (viewModel.isEnabled.value == true){
                if (value == "makeFavourite"){

                    requireActivity().invalidateOptionsMenu()

                }else if (value == "unFavourite"){

                    requireActivity().invalidateOptionsMenu()

                }else if (value == "Nothing"){

                    requireActivity().invalidateOptionsMenu()

                }
            }
        })

//  -----------------------------------------------------------------------------------------------------------------------------------

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            if (viewModel.isEnabled.value == true){
                requireActivity().onBackPressed()
            }else{
                onDestroy()
            }
        }

        if (item.itemId == R.id.move_button){

            if (viewModel.size <= 1){

                val builder = AlertDialog.Builder(this.context)
                builder.setTitle("Move Notes")
                builder.setMessage("Don't seem to be another folder to move the note to ")
                builder.setNegativeButton("Ok"){DialogInterface, which -> }
                val dialogBox = builder.create()
                dialogBox.show()
                builder.setCancelable(false)
            }else if (viewModel.size > 1){
                Log.i("TestingApp","${viewModel.defaultId}")
                view?.let { Navigation.findNavController(it).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToMoveFragment(folderId,folderName, displayNoteListAdapter.data.toTypedArray(),defaultid,"move")) }
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
            val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = sharedPreference?.edit()
            editor?.putBoolean("SELECTED",false)
            editor?.apply()
            editor?.commit()


        }

        else if (item.itemId == R.id.copy_button){

            view?.let { Navigation.findNavController(it).navigate(DisplayFolderContentFragmentDirections.actionDisplayFolderContentFragmentToMoveFragment(folderId,folderName, displayNoteListAdapter.data.toTypedArray(),defaultid,"copy")) }
            viewModel.isEnabled.value = false
            viewModel.checkedBoxClicked.value = false
            viewModel.selectAndDeSelectAll(false)
            val data = mutableListOf<String>()
            viewModel.getNoteIdList(folderId)
            displayNoteListAdapter.data = data
            viewModel.selectedList.value = data
            val emptyData = mutableListOf<NoteData>()
            viewModel.notesListSelected = emptyData

            val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = sharedPreference?.edit()
            editor?.putBoolean("SELECTED",false)
            editor?.apply()
            editor?.commit()


        }

        else if (item.itemId == R.id.delete_button) {
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Delete notes")
            builder.setMessage("Do you want to delete?")
            builder.setNegativeButton("No") { DialogInterface, which -> }
            builder.setPositiveButton("Yes") { DialogInterface, which ->

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
            val dialogBox = builder.create()
            dialogBox.show()
            builder.setCancelable(false)
            val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = sharedPreference?.edit()
            editor?.putBoolean("SELECTED",false)
            editor?.apply()
            editor?.commit()

        }

        else if(item.itemId == R.id.select_button){

            if (viewModel.checkedBoxClicked.value == false){

                viewModel.checkedBoxClicked.value = true

            }else if(viewModel.checkedBoxClicked.value == true){

                viewModel.checkedBoxClicked.value = false

            }

            if (viewModel.checkedBoxClicked.value == true){

                if (viewModel.allItemsSelected.value == false){

                    viewModel.selectedAllItem.value = true

                    viewModel.selectAndDeSelectAll(true)

                    var data = mutableListOf<String>()

                    viewModel.noteIdFolderList?.let { data.addAll(it) }

                    displayNoteListAdapter.data = data

                    viewModel.selectedList.value = data

                    viewModel.allItemsSelected.value = true

                    requireActivity().invalidateOptionsMenu()

                }

            }

            else if(viewModel.checkedBoxClicked.value == false){

                if (viewModel.allItemsSelected.value == true){

                    viewModel.selectAndDeSelectAll(false)

                    val data = mutableListOf<String>()

                    displayNoteListAdapter.data = data

                    viewModel.selectedList.value = data

                    viewModel.allItemsSelected.value = false

                    requireActivity().invalidateOptionsMenu()

                }
            }
        }

        else if (item.itemId == R.id.favourite_button){
            viewModel.getNotesList()

            if (viewModel.favouriteState.value == "makeFavourite"){

                viewModel.isEnabled.value = false

                viewModel.checkedBoxClicked.value = false

                val data = mutableListOf<String>()
                viewModel.makeFavourite(true)
                displayNoteListAdapter.data = data
                viewModel.selectedList.value = data
                val emptyData = mutableListOf<NoteData>()
                viewModel.notesListSelected = emptyData
                displayNoteListAdapter.noteDataList = emptyData
                viewModel.selectedListNoteData.value = emptyData
                viewModel.noteListSelectedFav = emptyData
                viewModel.noteListSelectedFav.add(NoteData("","","","",0,0,false,false))
                viewModel.favourite_item.value = 0
                viewModel.unfavourtie_item.value = 0
                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",false)
                editor?.apply()
                editor?.commit()

            }else if (viewModel.favouriteState.value == "unFavourite"){

                viewModel.makeFavourite(false)

                viewModel.isEnabled.value = false

                viewModel.checkedBoxClicked.value = false


                val data = mutableListOf<String>()

                displayNoteListAdapter.data = data

                viewModel.selectedList.value = data

                val emptyData = mutableListOf<NoteData>()

                viewModel.notesListSelected = emptyData

                displayNoteListAdapter.noteDataList = emptyData

                viewModel.selectedListNoteData.value = emptyData

                viewModel.noteListSelectedFav = emptyData

                viewModel.noteListSelectedFav.add(NoteData("","","","",0,0,false,false))

                viewModel.favourite_item.value = 0

                viewModel.unfavourtie_item.value = 0

                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",false)
                editor?.apply()
                editor?.commit()

            }
        }

        return super.onOptionsItemSelected(item)

    }



    override fun onPrepareOptionsMenu(menu: Menu) {

        viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer {value->

            if (value){
                if (viewModel.favouriteState.value == "makeFavourite"){

                    if (viewModel.checkedBoxClicked.value == true){

                        if (viewModel.allItemsSelected.value == false){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                        else if (viewModel.allItemsSelected.value == true){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                    }

                    else if (viewModel.checkedBoxClicked.value == false) {

                        if (viewModel.allItemsSelected.value == true) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                        else if (viewModel.allItemsSelected.value == false) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                    }

                    viewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

                        if (value){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }else if(!value){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }
                    })


                    val moveItem = menu.findItem(R.id.move_button)
                    moveItem.setVisible(true)

                    val copyItem = menu.findItem(R.id.copy_button)
                    copyItem.setVisible(true)

                    val deleteItem = menu.findItem(R.id.delete_button)
                    deleteItem.setVisible(true)

                    val checkBox = menu.findItem(R.id.select_button)
                    checkBox.setVisible(true)

                    val favourite = menu.findItem(R.id.favourite_button)
                    favourite.setTitle("Make Favourite")
                    favourite.setVisible(true)

                    val searchItem = menu.findItem(R.id.search_button)
                    searchItem.setVisible(false)

                }

                else if (viewModel.favouriteState.value == "unFavourite"){
                    if (viewModel.checkedBoxClicked.value == true){

                        if (viewModel.allItemsSelected.value == false){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                        else if (viewModel.allItemsSelected.value == true){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                    }

                    else if (viewModel.checkedBoxClicked.value == false) {

                        if (viewModel.allItemsSelected.value == true) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                        else if (viewModel.allItemsSelected.value == false) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                    }

                    viewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

                        if (value){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }else if(!value){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }
                    })


                    val moveItem = menu.findItem(R.id.move_button)
                    moveItem.setVisible(true)

                    val copyItem = menu.findItem(R.id.copy_button)
                    copyItem.setVisible(true)

                    val deleteItem = menu.findItem(R.id.delete_button)
                    deleteItem.setVisible(true)

                    val checkBox = menu.findItem(R.id.select_button)
                    checkBox.setVisible(true)

                    val favourite = menu.findItem(R.id.favourite_button)
                    favourite.setTitle("Unfavourite")
                    favourite.setVisible(true)

                    val searchItem = menu.findItem(R.id.search_button)
                    searchItem.setVisible(false)

                }

                else if (viewModel.favouriteState.value == "Nothing"){
                    if (viewModel.checkedBoxClicked.value == true){

                        if (viewModel.allItemsSelected.value == false){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                        else if (viewModel.allItemsSelected.value == true){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                    }

                    else if (viewModel.checkedBoxClicked.value == false) {

                        if (viewModel.allItemsSelected.value == true) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                        else if (viewModel.allItemsSelected.value == false) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                    }

                    viewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

                        if (value){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }else if(!value){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }
                    })

                    val searchItem = menu.findItem(R.id.search_button)
                    searchItem.setVisible(false)

                    val moveItem = menu.findItem(R.id.move_button)
                    moveItem.setVisible(true)

                    val copyItem = menu.findItem(R.id.copy_button)
                    copyItem.setVisible(true)

                    val deleteItem = menu.findItem(R.id.delete_button)
                    deleteItem.setVisible(true)

                    val checkBox = menu.findItem(R.id.select_button)
                    checkBox.setVisible(true)

                    val favourite = menu.findItem(R.id.favourite_button)
                    favourite.setVisible(false)

                }
            }

            else if (!value){

                val searchItem = menu.findItem(R.id.search_button)
                searchItem.setVisible(true)

                val moveItem = menu.findItem(R.id.move_button)
                moveItem.setVisible(false)

                val copyItem = menu.findItem(R.id.copy_button)
                copyItem.setVisible(false)

                val deleteItem = menu.findItem(R.id.delete_button)
                deleteItem.setVisible(false)

                val checkBox = menu.findItem(R.id.select_button)
                checkBox.setVisible(false)

                val favourite = menu.findItem(R.id.favourite_button)
                favourite.setVisible(false)

            }

        })

        super.onPrepareOptionsMenu(menu)

    }

    fun goBack(){
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        (activity as HomePageActivity).drawerUnLocked()
        super.onDestroyView()
    }

}