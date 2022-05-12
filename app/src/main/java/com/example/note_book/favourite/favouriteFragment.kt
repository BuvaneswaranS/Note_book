package com.example.note_book.favourite

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
import com.example.note_book.HomePageActivity
import com.example.note_book.NotebookDatabase.NoteData

import com.example.note_book.R
import com.example.note_book.databinding.FragmentFavouriteBinding

class favouriteFragment : Fragment(R.layout.fragment_favourite) {

    private lateinit var binding: FragmentFavouriteBinding

    private lateinit var favouriteViewModel: FavouriteViewModel

    private lateinit var favouriteAdapter: FavouriteAdapter

    private val selected = "sharedSelect"

    val shared_value = "drawer_shared"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("IN",true)
        editor?.apply()
        editor?.commit()

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        val application = requireNotNull(this.activity).application

        val viewModelFactory = FavouriteViewModelFactory(application)
        
        favouriteViewModel = ViewModelProvider(this,viewModelFactory).get(FavouriteViewModel::class.java)

        favouriteViewModel.getAllNotesList()

        binding.favouritesRecyclerview.layoutManager = GridLayoutManager(this.context,2)

        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("Favourites")

        favouriteAdapter =   FavouriteAdapter(favouriteViewModel)

        binding.favouritesRecyclerview.adapter = favouriteAdapter


        favouriteViewModel.favouriteList?.observe(this.viewLifecycleOwner, Observer {list ->
            favouriteAdapter.submitList(list)
        })

        favouriteViewModel.isEnabled.observe(this.viewLifecycleOwner, Observer {value ->

            if (value){

                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                var editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("SELECTED",true)
                editor?.apply()
                editor?.commit()


                val sharedPreferences = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
                var editors: SharedPreferences.Editor? = sharedPreferences?.edit()
                editors?.putBoolean("IN",false)
                editors?.apply()
                editors?.commit()

                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button)

                (activity as HomePageActivity).drawerLocked()

                favouriteViewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->

                    if (favouriteViewModel.isEnabled.value == true){
                        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("${list.size} selected")
                    }

                    val selectedListSize: Int = list.size
                    val entireListSize: Int? = favouriteViewModel.notesListFolder?.value?.size
                    favouriteViewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->

                        if (selectedListSize == list.size){

                            favouriteViewModel.allItemsSelected.value = true
                            requireActivity().invalidateOptionsMenu()

                        }

                        else{

                            favouriteViewModel.allItemsSelected.value = false
                            requireActivity().invalidateOptionsMenu()
                        }

                    })

                })

            }

            else if(!value){

//                binding.favouritesToolbar.setTitle("Favourites")
                (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("Favourites")
                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
                val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
                var editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("IN",true)
                editor?.apply()
                editor?.commit()
                (activity as HomePageActivity).drawerUnLocked()
            }

        })

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (favouriteViewModel.isEnabled.value == true){
                    favouriteViewModel.isEnabled.value = false
                    favouriteViewModel.checkedBoxClicked.value = false
                    favouriteViewModel.selectAndDeSelectAll(false)
                    val data = mutableListOf<String>()
                    favouriteAdapter.data = data
                    favouriteViewModel.selectedList.value = data
                    val emptyData = mutableListOf<NoteData>()
                    favouriteViewModel.notesListSelected = emptyData
                }
                Log.i("TestingApp","Favourite Fragment --> On Back Pressed ")
                (activity as HomePageActivity).onSupportNavigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favourite_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            if (favouriteViewModel.isEnabled.value == true){
                requireActivity().onBackPressed()
            }else{
                onDestroy()
            }
        }

        if (item.itemId == R.id.favourite_delete_button) {

            var builder = AlertDialog.Builder(this.context)
            builder.setTitle("Delete notes")
            builder.setMessage("Do you want to delete?")
            builder.setNegativeButton("No") { DialogInterface, which -> }
            builder.setPositiveButton("Yes") { DialogInterface, which ->
                favouriteViewModel.isEnabled.value = false
                favouriteViewModel.checkedBoxClicked.value = false
                favouriteViewModel.deleteSelectedItem()
                favouriteViewModel.selectAndDeSelectAll(false)
                val data = mutableListOf<String>()
//                favouriteViewModel.getNoteIdList(folderId)
                favouriteAdapter.data = data
                favouriteViewModel.selectedList.value = data
                val emptyData = mutableListOf<NoteData>()
                favouriteViewModel.notesListSelected = emptyData
            }
            var dialogBox = builder.create()
            dialogBox.show()
            builder.setCancelable(false)

        }

        else if(item.itemId == R.id.select_deselect_all_button){
            Log.i("Tester","Select button clicked")
            if (favouriteViewModel.checkedBoxClicked.value == false){

                favouriteViewModel.checkedBoxClicked.value = true

            }else if(favouriteViewModel.checkedBoxClicked.value == true){

                favouriteViewModel.checkedBoxClicked.value = false

            }

            if (favouriteViewModel.checkedBoxClicked.value == true){

                if (favouriteViewModel.allItemsSelected.value == false){

                    favouriteViewModel.selectedAllItem.value = true

                    favouriteViewModel.selectAndDeSelectAll(true)

                    var data = mutableListOf<String>()

                    favouriteViewModel.noteIdFolderList?.let { data.addAll(it) }

                    favouriteAdapter.data = data

                    favouriteViewModel.selectedList.value = data

                    favouriteViewModel.allItemsSelected.value = true

                    requireActivity().invalidateOptionsMenu()
                }

            }

            else if(favouriteViewModel.checkedBoxClicked.value == false){

                if (favouriteViewModel.allItemsSelected.value == true){

                    favouriteViewModel.selectAndDeSelectAll(false)

                    val data = mutableListOf<String>()

                    favouriteAdapter.data = data

                    favouriteViewModel.selectedList.value = data

                    favouriteViewModel.allItemsSelected.value = false

                    requireActivity().invalidateOptionsMenu()

                }
            }
        }

        else if (item.itemId == R.id.favourite_search_button){
            view?.let { Navigation.findNavController(it).navigate(favouriteFragmentDirections.actionFavouriteFragmentToSearchFragment()) }
        }

        else if (item.itemId == R.id.unfavourite_button){
            favouriteViewModel.getNotesList()
            favouriteViewModel.isEnabled.value = false

            favouriteViewModel.checkedBoxClicked.value = false

            favouriteViewModel.makeUnFavourite()

            favouriteViewModel.selectAndDeSelectAll(false)

            val data = mutableListOf<String>()
//
//                favouriteViewModel.getNoteIdList(folderId)

            favouriteAdapter.data = data

            favouriteViewModel.selectedList.value = data

            val emptyData = mutableListOf<NoteData>()

            favouriteViewModel.notesListSelected = emptyData
            val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
            var editor: SharedPreferences.Editor? = sharedPreference?.edit()
            editor?.putBoolean("SELECTED",false)
            editor?.apply()
            editor?.commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        favouriteViewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { value ->

            if (value) {
                if (favouriteViewModel.checkedBoxClicked.value == true){

                    if (favouriteViewModel.allItemsSelected.value == false){

                        val selectButton = menu.findItem(R.id.select_deselect_all_button)

                        selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                    }

                    else if (favouriteViewModel.allItemsSelected.value == true){

                        val selectButton = menu.findItem(R.id.select_deselect_all_button)
                        selectButton.setIcon(R.drawable.ic_select_all_on)

                    }

                }

                else if (favouriteViewModel.checkedBoxClicked.value == false) {

                    if (favouriteViewModel.allItemsSelected.value == true) {

                        val selectButton = menu.findItem(R.id.select_deselect_all_button)
                        selectButton.setIcon(R.drawable.ic_select_all_on)

                    }

                    else if (favouriteViewModel.allItemsSelected.value == false) {

                        val selectButton = menu.findItem(R.id.select_deselect_all_button)
                        selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                    }

                }

                favouriteViewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer { value ->

                    if (value){

                        val selectButton = menu.findItem(R.id.select_deselect_all_button)
                        selectButton.setIcon(R.drawable.ic_select_all_on)

                    }else if(!value){

                        val selectButton = menu.findItem(R.id.select_deselect_all_button)
                        selectButton.setIcon(R.drawable.ic_baseline_select_all_24)

                    }
                })

                
                val deleteItem = menu.findItem(R.id.favourite_delete_button)
                deleteItem.setVisible(true)

                val checkBox = menu.findItem(R.id.select_deselect_all_button)
                checkBox.setVisible(true)

                val favourite = menu.findItem(R.id.unfavourite_button)
                favourite.setVisible(true)

                val searchItem = menu.findItem(R.id.favourite_search_button)
                searchItem.setVisible(false)

            } else if (!value) {

                val searchItem = menu.findItem(R.id.favourite_search_button)
                searchItem.setVisible(true)

                val deleteItem = menu.findItem(R.id.favourite_delete_button)
                deleteItem.setVisible(false)

                val checkBox = menu.findItem(R.id.select_deselect_all_button)
                checkBox.setVisible(false)

                val favourite = menu.findItem(R.id.unfavourite_button)
                favourite.setVisible(false)

            }
        })
        super.onPrepareOptionsMenu(menu)
    }
}