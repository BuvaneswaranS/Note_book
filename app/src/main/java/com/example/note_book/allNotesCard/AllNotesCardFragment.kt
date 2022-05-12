package com.example.note_book.allNotesCard

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.HomeFragment.HomeFragmentDirections
import com.example.note_book.HomePageActivity
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentAllNotesCardBinding


class AllNotesCardFragment : Fragment(R.layout.fragment_all_notes_card) {

//  ViewBinding Declaration
    private lateinit var binding: FragmentAllNotesCardBinding

//  Declaration of the ViewModel
    private lateinit var allNoteCardViewModel: AllNotesCardViewModel

//  Declaration of the Adapter used in the recyclerView
    private lateinit var allNoteCardAdapter: AllNotesCardAdapter

//  SharedPreference Name for the selection enabled
    private val selected = "sharedSelect"

//  SharedPreference Name for the Drawer
    val shared_value = "drawer_shared"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        binding = FragmentAllNotesCardBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.show()

//      Shared Preference --> to make sure the drawer will open the when the navigation drawer is clicked
        val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("IN",true)
        editor?.apply()
        editor?.commit()

//      Declaration of hte application
        val allNotesapplication = requireNotNull(this.activity).application

//      Set the supportActionBar Title
        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("All Notes Card")

//     Declaration and initialization of the ViewModelFactory
        val allNoteCardViewModelFactory = AllNotesCardViewModelFactory(allNotesapplication)

//     Initialization of the ViewModel
        allNoteCardViewModel = ViewModelProvider(this, allNoteCardViewModelFactory).get(AllNotesCardViewModel::class.java)

//      Function Call in the viewModel to get all the Notes List
        allNoteCardViewModel.getAllNotesList()

//      Function Call in the ViewModel to get all the Notes Id list
        allNoteCardViewModel.getAllNoteIdList()

//      Initialization of the Adapter
        allNoteCardAdapter = AllNotesCardAdapter(allNoteCardViewModel)

//      Declaration and Initialization of the type if the layoutManager of the recyclerView
        binding.allNotesRecyclerview.layoutManager = GridLayoutManager(this.context, 2)

//      Declaration of the Adapter for the recyclerView
        binding.allNotesRecyclerview.adapter = allNoteCardAdapter

        allNoteCardViewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer {list ->
            allNoteCardAdapter.submitList(list)
        })

//      Click Listener for the Add Note
        binding.addNoteAllNotesRecyclerview.setOnClickListener { view ->

            Navigation.findNavController(view).navigate(AllNotesCardFragmentDirections.actionAllNotesCardFragmentToCreateNewNoteFragment(allNoteCardViewModel.defaultId))

        }

//      To Check whether the Selection is Enabled (or) Not
        allNoteCardViewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { enabled ->

//          If Enabled
            if (enabled) {

                binding.addNoteAllNotesRecyclerview.visibility = View.GONE

//              The below lines 117 - 121 to enabled the SELECTED shared Preferences
                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",true)
                editor?.apply()
                editor?.commit()

//              The below lines 110 - 114 to make sure the navigation drawer will not open when the user clicked on the navigationd drawer icon
                val sharedPreferences = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
                val editors: SharedPreferences.Editor? = sharedPreferences?.edit()
                editors?.putBoolean("IN",false)
                editors?.apply()
                editors?.commit()

//              Set the supportActionBar as the Back Arrow Button
                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button)

//              Call the function the MainAcivity to Lock the drawer
                (activity as HomePageActivity).drawerLocked()

//              Call  the invalidateOptionsMenu() to the invalidate the current optionMenu
                requireActivity().invalidateOptionsMenu()

//              Line 131 - 137  --> is used to display the size of the selected list
                allNoteCardViewModel.selectedList.observe(this.viewLifecycleOwner, Observer { list ->

                        if (allNoteCardViewModel.isEnabled.value == true) {

                            (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("${list.size} selected")

                        }
//              Line 139 - 148 --> is used to set whether all the items are selected
                        val selectedListSize: Int = list.size

                        allNoteCardViewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->

                            if (selectedListSize == list.size) {
                                    allNoteCardViewModel.allItemsSelected.value = true
                                } else {
                                    allNoteCardViewModel.allItemsSelected.value = false
                                }
                            })
                    })
            } else if (!enabled) {
//
                    (activity as HomePageActivity).drawerClosed()

                    binding.addNoteAllNotesRecyclerview.visibility = View.VISIBLE

                    requireActivity().invalidateOptionsMenu()

                    (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("All Note Cards")
                    (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

                    binding.addNoteAllNotesRecyclerview.visibility = View.VISIBLE

                    val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                    editor?.putBoolean("IN",true)
                    editor?.apply()
                    editor?.commit()

            }

                (activity as HomePageActivity).drawerUnLocked()

        })




        allNoteCardViewModel.favourite_item.observe(this.viewLifecycleOwner, Observer { favouriteItems ->
            allNoteCardViewModel.unfavourtie_item.observe(this.viewLifecycleOwner, Observer {unFavourite ->

                if ((favouriteItems == 0) && (unFavourite == 0)){
                    allNoteCardViewModel.favouriteState.value = "makeFavourite"
                }

                else if ((favouriteItems > 0) && (unFavourite == 0)){
                    allNoteCardViewModel.favouriteState.value = "unFavourite"
                }

                else if ((favouriteItems == 0) && (unFavourite > 0)){
                    allNoteCardViewModel.favouriteState.value = "makeFavourite"
                }

                else if ((favouriteItems > 0) && (unFavourite > 0)){
                    allNoteCardViewModel.favouriteState.value = "Nothing"
                }

            })
        })

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (allNoteCardViewModel.isEnabled.value == true){
                    allNoteCardViewModel.isEnabled.value = false
                    allNoteCardViewModel.checkedBoxClicked.value = false
                    allNoteCardViewModel.selectAndDeSelectAll(false)
                    val data = mutableListOf<String>()
                        allNoteCardAdapter.data = data
                    allNoteCardViewModel.selectedList.value = data
                    val emptyData = mutableListOf<NoteData>()
                    allNoteCardViewModel.notesListSelected = emptyData
                    allNoteCardViewModel.favourite_item.value = 0
                    allNoteCardViewModel.unfavourtie_item.value = 0

                }
                (activity as HomePageActivity).onSupportNavigateUp()
                Log.i("TestingApp","All Note Card Fragment --> On Back Pressed ")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)


        allNoteCardViewModel.favouriteState.observe(this.viewLifecycleOwner, Observer { value ->

            if (allNoteCardViewModel.isEnabled.value == true){

                if (value == "makeFavourite"){
                    requireActivity().invalidateOptionsMenu()
                }

                else if (value == "unFavourite"){
                    requireActivity().invalidateOptionsMenu()
                }

                else if (value == "Nothing"){
                    requireActivity().invalidateOptionsMenu()
                }

            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            if (allNoteCardViewModel.isEnabled.value == true){
                requireActivity().onBackPressed()
            }else{
                onDestroy()
            }
        }else if (item.itemId == R.id.move_button) {

            if (allNoteCardViewModel.size <= 1) {

                val builder = AlertDialog.Builder(this.context)
                builder.setTitle("Move Notes")
                builder.setMessage("Don't seem to be another folder to move the note to ")
                builder.setNegativeButton("Ok") { DialogInterface, which -> }
                val dialogBox = builder.create()
                dialogBox.show()
                builder.setCancelable(false)

            } else if (allNoteCardViewModel.size > 1) {

                view?.let { Navigation.findNavController(it).navigate(HomeFragmentDirections.actionHomeFragmentToMoveFragment(allNoteCardViewModel.defaultId, "allNoteCards", allNoteCardAdapter.data.toTypedArray(), allNoteCardViewModel.defaultId, "move")) }

                allNoteCardViewModel.isEnabled.value = false

                allNoteCardViewModel.checkedBoxClicked.value = false

                allNoteCardViewModel.selectAndDeSelectAll(false)

                val data = mutableListOf<String>()

                allNoteCardAdapter.data = data

                allNoteCardViewModel.selectedList.value = data

                val emptyData = mutableListOf<NoteData>()

                allNoteCardViewModel.notesListSelected = emptyData

                allNoteCardViewModel.favourite_item.value = 0

                allNoteCardViewModel.unfavourtie_item.value = 0

                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",false)
                editor?.apply()
                editor?.commit()

            }

        } else if (item.itemId == R.id.copy_button) {

            view?.let { Navigation.findNavController(it).navigate(HomeFragmentDirections.actionHomeFragmentToMoveFragment("", "allNoteCards", allNoteCardAdapter.data.toTypedArray(), allNoteCardViewModel.defaultId, "copy")) }

            allNoteCardViewModel.isEnabled.value = false

            allNoteCardViewModel.checkedBoxClicked.value = false

            allNoteCardViewModel.selectAndDeSelectAll(false)

            val data = mutableListOf<String>()

            allNoteCardAdapter.data = data

            allNoteCardViewModel.selectedList.value = data

            val emptyData = mutableListOf<NoteData>()

            allNoteCardViewModel.notesListSelected = emptyData

            allNoteCardViewModel.favourite_item.value = 0

            allNoteCardViewModel.unfavourtie_item.value = 0

            val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = sharedPreference?.edit()
            editor?.putBoolean("SELECTED",false)
            editor?.apply()
            editor?.commit()

        } else if (item.itemId == R.id.delete_button) {

            if (allNoteCardViewModel.selectedList.value?.isEmpty() == true) {

            } else {

                val builder = AlertDialog.Builder(this.context)
                builder.setTitle("Delete notes")
                builder.setMessage("Do you want to delete?")
                builder.setNegativeButton("No") { DialogInterface, which -> }
                builder.setPositiveButton("Yes") { DialogInterface, which ->

                    allNoteCardViewModel.isEnabled.value = false

                    allNoteCardViewModel.deleteSelectedItem()

                    allNoteCardViewModel.selectAndDeSelectAll(false)

                    val data = mutableListOf<String>()

                    allNoteCardAdapter.data = data

                    allNoteCardViewModel.selectedList.value = data

                    val emptyData = mutableListOf<NoteData>()

                    allNoteCardViewModel.notesListSelected = emptyData

                    allNoteCardViewModel.favourite_item.value = 0

                    allNoteCardViewModel.unfavourtie_item.value = 0

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
        }

        else if (item.itemId == R.id.search_button){
            view?.let { Navigation.findNavController(it).navigate(AllNotesCardFragmentDirections.actionAllNotesCardFragmentToSearchFragment()) }
        }

    else if(item.itemId == R.id.select_button) {

        if (allNoteCardViewModel.checkedBoxClicked.value == false) {

            allNoteCardViewModel.checkedBoxClicked.value = true

        } else if (allNoteCardViewModel.checkedBoxClicked.value == true) {

            allNoteCardViewModel.checkedBoxClicked.value = false

        }

        if (allNoteCardViewModel.checkedBoxClicked.value == true) {

            if (allNoteCardViewModel.allItemsSelected.value == false) {

                allNoteCardViewModel.selectedAllItem.value = true

                allNoteCardViewModel.selectAndDeSelectAll(true)

                var data = mutableListOf<String>()

                allNoteCardViewModel.allNoteIdList?.let { data.addAll(it) }

                allNoteCardAdapter.data = data

                allNoteCardViewModel.selectedList.value = data

                allNoteCardViewModel.allItemsSelected.value = true

                requireActivity().invalidateOptionsMenu()

            }
        } else if (allNoteCardViewModel.checkedBoxClicked.value == false) {
            if (allNoteCardViewModel.allItemsSelected.value == true) {

                allNoteCardViewModel.selectAndDeSelectAll(false)
                val data = mutableListOf<String>()
                allNoteCardAdapter.data = data
                allNoteCardViewModel.selectedList.value = data
                allNoteCardViewModel.allItemsSelected.value = false
                requireActivity().invalidateOptionsMenu()

            }
        }

    }
        else if (item.itemId == R.id.favourite_button){

            allNoteCardViewModel.getNotesList()

            if (allNoteCardViewModel.favouriteState.value == "makeFavourite"){

                allNoteCardViewModel.isEnabled.value = false

                allNoteCardViewModel.checkedBoxClicked.value = false

                val data = mutableListOf<String>()

                allNoteCardViewModel.makeFavourite(true)

                allNoteCardAdapter.data = data

                allNoteCardViewModel.selectedList.value = data

                val emptyData = mutableListOf<NoteData>()

                allNoteCardViewModel.notesListSelected = emptyData

                allNoteCardViewModel.favourite_item.value = 0

                allNoteCardViewModel.unfavourtie_item.value = 0

                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",false)
                editor?.apply()
                editor?.commit()

            }else if (allNoteCardViewModel.favouriteState.value == "unFavourite"){

                allNoteCardViewModel.isEnabled.value = false

                allNoteCardViewModel.checkedBoxClicked.value = false

                allNoteCardViewModel.makeFavourite(false)

                val data = mutableListOf<String>()

                allNoteCardAdapter.data = data

                allNoteCardViewModel.selectedList.value = data

                val emptyData = mutableListOf<NoteData>()

                allNoteCardViewModel.notesListSelected = emptyData

                allNoteCardViewModel.favourite_item.value = 0

                allNoteCardViewModel.unfavourtie_item.value = 0

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

        allNoteCardViewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { value ->

            if (value) {

                if (allNoteCardViewModel.favouriteState.value == "makeFavourite"){

                    if (allNoteCardViewModel.checkedBoxClicked.value == true){

                        if (allNoteCardViewModel.allItemsSelected.value == false){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                        else if (allNoteCardViewModel.allItemsSelected.value == true){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                    }

                    else if (allNoteCardViewModel.checkedBoxClicked.value == false) {

                        if (allNoteCardViewModel.allItemsSelected.value == true) {

                            val selectButton = menu.findItem(R.id.select_button)

                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                        else if (allNoteCardViewModel.allItemsSelected.value == false) {

                            val selectButton = menu.findItem(R.id.select_button)

                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                    }

                    allNoteCardViewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

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
                    favourite.setTitle("Make Favourite")
                    favourite.setVisible(true)

                }

                else if (allNoteCardViewModel.favouriteState.value == "unFavourite"){
                    if (allNoteCardViewModel.checkedBoxClicked.value == true){

                        if (allNoteCardViewModel.allItemsSelected.value == false){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                        else if (allNoteCardViewModel.allItemsSelected.value == true){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                    }

                    else if (allNoteCardViewModel.checkedBoxClicked.value == false) {

                        if (allNoteCardViewModel.allItemsSelected.value == true) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                        else if (allNoteCardViewModel.allItemsSelected.value == false) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                    }

                    allNoteCardViewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

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
                    favourite.setTitle("Unfavourite")
                    favourite.setVisible(true)

                }

                else if (allNoteCardViewModel.favouriteState.value == "Nothing"){
                    if (allNoteCardViewModel.checkedBoxClicked.value == true){

                        if (allNoteCardViewModel.allItemsSelected.value == false){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                        else if (allNoteCardViewModel.allItemsSelected.value == true){

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                    }

                    else if (allNoteCardViewModel.checkedBoxClicked.value == false) {

                        if (allNoteCardViewModel.allItemsSelected.value == true) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_select_all_on)

                        }

                        else if (allNoteCardViewModel.allItemsSelected.value == false) {

                            val selectButton = menu.findItem(R.id.select_button)
                            selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                        }

                    }

                    allNoteCardViewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

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
            } else if (!value) {

                val searchItem = menu.findItem(R.id.search_button)
                searchItem.setVisible(true)

                Log.i("TestingApp", "Entered into False")
                val moveItem = menu.findItem(R.id.move_button)
                moveItem.setVisible(false)
                Log.i("TestingApp", "Move Item = " + moveItem.isVisible.toString())

                val copyItem = menu.findItem(R.id.copy_button)
                copyItem.setVisible(false)
                Log.i("TestingApp", "Copy Item = " + copyItem.isVisible.toString())

                val deleteItem = menu.findItem(R.id.delete_button)
                deleteItem.setVisible(false)
                Log.i("TestingApp", "Delete Item = " + deleteItem.isVisible.toString())

                val favourite = menu.findItem(R.id.favourite_button)
                favourite.setVisible(false)

                val checkBox = menu.findItem(R.id.select_button)
                checkBox.setVisible(false)
                Log.i("TestingApp", "Delete Item = " + deleteItem.isVisible.toString())
            }
        })
        super.onPrepareOptionsMenu(menu)
    }

    fun normalOperation(){
        val data = mutableListOf<String>()

        allNoteCardAdapter.data = data

        allNoteCardViewModel.selectedList.value = data

        val emptyData = mutableListOf<NoteData>()

        allNoteCardViewModel.notesListSelected = emptyData

        allNoteCardViewModel.favourite_item.value = 0

        allNoteCardViewModel.unfavourtie_item.value = 0

        val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("SELECTED",false)
        editor?.apply()
        editor?.commit()

    }

}