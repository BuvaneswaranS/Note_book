package com.example.note_book.search

import android.app.SearchManager
import android.content.Context
import android.content.Context.*
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.HomePageActivity
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentSearchBinding

class SearchFragment : Fragment(R.layout.fragment_search) {

//  Viewbinding Declaration
    private lateinit var binding: FragmentSearchBinding

//  ViewModel Declaration
    private lateinit var viewModel: SearchViewModel

//  NoteData Adapter Declaration
    private lateinit var searchNoteDataAdpater: SearchNoteDataAdapter

//  FolderData Adapter Declaration
    private lateinit var searchFolderDataAdapter: SearchFolderDataAdapter

    val shared_value = "drawer_shared"

    //  Declaration and Initialzation  of the SharedPreference
    private val selected = "sharedSelect"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//      ViewBinding Initialization
        binding = FragmentSearchBinding.inflate(inflater, container, false)

//      Declaration and Initialization of the Application
        val application  = requireNotNull(this.activity).application

//      Declaration and Initialization of ViewModel Factory
        val searchViewModelFactory = SearchViewModelFactory(application)

//      Initialization of the ViewModel
        viewModel = ViewModelProvider(this, searchViewModelFactory).get(SearchViewModel::class.java)

        val sharedPreference = activity?.getSharedPreferences(shared_value, MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("IN",false)
        editor?.apply()
        editor?.commit()

        Log.i("TestingApp","Arrow enabled for go Back ")
        val sharedPreferencesSelected = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
        val editorSelected: SharedPreferences.Editor? = sharedPreferencesSelected?.edit()
        editorSelected?.putBoolean("SELECTED",false)
        editorSelected?.apply()
        editorSelected?.commit()


//      Lock the drawer
        (activity as HomePageActivity).drawerLocked()

        binding.notesRecyclerViewLayout.visibility = View.GONE

        (requireActivity() as AppCompatActivity).supportActionBar?.show()

//      Up button to go back

//      Initialization of the Note Data Adapter
        searchNoteDataAdpater = SearchNoteDataAdapter(viewModel)

//      Initialization of the Folder Data Adapter
        searchFolderDataAdapter = SearchFolderDataAdapter(viewModel,viewModel.defaultFolderId.toString())

//      Setting the Layout Manager
        binding.notesRecyclerView.layoutManager = GridLayoutManager(this.context, 1)

        binding.folderRecyclerView.layoutManager = GridLayoutManager(this.context, 1)


//      Setting the Adapter

        binding.notesRecyclerView.adapter = searchNoteDataAdpater

        binding.folderRecyclerView.adapter = searchFolderDataAdapter

//      -----------------------------------------------------
//        viewModel.searchText?.observe(this.viewLifecycleOwner, Observer {searchText ->
//            Log.i("TestingApp","Search Text Area --> ${searchText}")
//            viewModel.getNoteDataBasedTitleAndDescription(searchText)
//        })


        viewModel.searchNoteData.observe(this.viewLifecycleOwner, Observer { list ->
            Log.i("TestingApp","Changed")
            if (list.isEmpty()){
                binding.notesRecyclerViewLayout.visibility = View.GONE
            }else{
                if (viewModel.searchFolderData.value?.isEmpty() == true){
                    binding.folderRecyclerViewLayout.visibility = View.GONE
                }else{

                }

            }

        })

        viewModel.searchText.observe(this.viewLifecycleOwner, Observer {text ->

            if (text == ""){
                viewModel.setSearchViewToEmpty.value = false

            }else if (text != ""){
//                Log.i("TestingApp","Arrow enabled for clear the data in the search view")
//                val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
//                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
//                editor?.putBoolean("SELECTED",true)
//                editor?.apply()
//                editor?.commit()

            }

        })

        viewModel.setSearchViewToEmpty.observe(this.viewLifecycleOwner, Observer {value ->
            if (value){
                Log.i("TestingApp","Changed Go Back Called --> ${viewModel.changeFragment}")
                if (viewModel.changeFragment == "Changed"){
                    Log.i("TestingApp","Changed Go Back Called")
                    viewModel.changeFragment = "ChangedGoBack"
                }
                requireActivity().invalidateOptionsMenu()
            }
        })

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.i("TestingApp","Arrow enabled for go Back ")
                val sharedPreferencesSelected = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                val editorSelected: SharedPreferences.Editor? = sharedPreferencesSelected?.edit()
                editorSelected?.putBoolean("SELECTED",false)
                editorSelected?.apply()
                editorSelected?.commit()
                Log.i("TestingApp","OnSupport Navigate Up cAlled ")
                if (viewModel.searchText.value?.isEmpty() == true){
                        Log.i("TestingApp","Search Fragment if part onBackPressed Called")
                    (activity as HomePageActivity).onSupportNavigateUp()
                }
                            else if (viewModel.searchText.value?.isNotEmpty() == true){
                    Log.i("TestingApp","Search Fragment else part onBackPressed Called")
                        (activity as HomePageActivity).onSupportNavigateUp()

                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        var menuItem = menu.findItem(R.id.search_menu_id)
        menuItem.icon = null
        val searchManager =  activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = menuItem.actionView  as androidx.appcompat.widget.SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

        searchView.isSubmitButtonEnabled = true


        searchView.setIconifiedByDefault(false)

        searchView.queryHint = "Search"
        searchView.setQuery("${viewModel.searchText.value}", false)
        searchView.maxWidth= Int.MAX_VALUE



        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val data = p0
                viewModel.searchText.value = data
                Log.i("TestingApp"," Submit Search Text in ViewModel --> ${viewModel.searchText.value}")
                if (data == ""){
                    binding.notesRecyclerViewLayout.visibility = View.GONE
                    binding.folderRecyclerViewLayout.visibility = View.GONE
                }else if (data != null) {
                    searchDatabase(data)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val data = p0
                viewModel.searchText.value = data
                Log.i("TestingApp","Change Search Text in ViewModel --> ${viewModel.searchText.value}")
                if (data == ""){
                    binding.notesRecyclerViewLayout.visibility = View.GONE
                    binding.folderRecyclerViewLayout.visibility = View.GONE
                }else if (data != null) {
                    searchDatabase(data)
                }
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            (activity as HomePageActivity).onSupportNavigateUp()
            (activity as HomePageActivity).drawerLocked()
            (activity as HomePageActivity).drawerUnLocked()

//            if (viewModel.searchText.value?.isEmpty() == true){
//                Log.i("TestingApp","Search Fragment if part onBackPressed Called")
//                (activity as HomePageActivity).onSupportNavigateUp()
//                (activity as HomePageActivity).drawerLocked()
//                (activity as HomePageActivity).drawerUnLocked()
//            }else if (viewModel.searchText.value?.isNotEmpty() == true){
//                Log.i("TestingApp","Search Fragment else part onBackPressed Called")
////                        (activity as HomePageActivity).onSupportNavigateUp()
//                viewModel.setSearchViewToEmpty.value = true
//            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.i("TestingApp","onPrepareOptionsMenu called")
        if (viewModel.changeFragment == "NotChanged"){
            val menuItem = menu.findItem(R.id.search_menu_id)
            val searchManager =  activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

            val searchView = menuItem.actionView  as androidx.appcompat.widget.SearchView

            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

            searchView.isSubmitButtonEnabled = true
            searchView.setIconifiedByDefault(false)
            searchView.queryHint = "Search"
            searchView.setQuery("", false)
            searchView.maxWidth= Int.MAX_VALUE

            searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    val data = p0
                    viewModel.searchText.value = data
                    Log.i("TestingApp"," Submit Search Text in ViewModel --> ${viewModel.searchText.value}")
                    if (data == ""){
                        binding.notesRecyclerViewLayout.visibility = View.GONE
                        binding.folderRecyclerViewLayout.visibility = View.GONE
                    }else if (data != null) {
                        searchDatabase(data)
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    val data = p0
                    viewModel.searchText.value = data
                    Log.i("TestingApp","Change Search Text in ViewModel --> ${viewModel.searchText.value}")
                    if (data == ""){
                        binding.notesRecyclerViewLayout.visibility = View.GONE
                        binding.folderRecyclerViewLayout.visibility = View.GONE
                    }else if (data != null) {
                        searchDatabase(data)
                    }
                    return true
                }

            })
        }else if(viewModel.changeFragment == "Changed"){
            val menuItem = menu.findItem(R.id.search_menu_id)
            val searchManager =  activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager


            val searchView = menuItem.actionView  as androidx.appcompat.widget.SearchView

            (activity as HomePageActivity).openKeyboard(searchView)

            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

            (activity as HomePageActivity).openKeyboard(menuItem.actionView)

            searchView.isQueryRefinementEnabled = true

            searchView.setIconifiedByDefault(false)
            searchView.queryHint = "Search"

            searchView.maxWidth= Int.MAX_VALUE
            searchView.isSubmitButtonEnabled = true
            viewModel.searchText.value?.let { searchDatabase(it) }
            searchView.imeOptions = 6
            searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    val data = p0
                    viewModel.searchText.value = data
                    Log.i("TestingApp"," Submit Search Text in ViewModel --> ${viewModel.searchText.value}")
                    if (data == ""){
                        binding.notesRecyclerViewLayout.visibility = View.GONE
                        binding.folderRecyclerViewLayout.visibility = View.GONE
                    }else if (data != null) {
                        searchDatabase(data)
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    val data = p0
                    viewModel.searchText.value = data
                    Log.i("TestingApp","Change Search Text in ViewModel --> ${viewModel.searchText.value}")
                    if (data == ""){
                        binding.notesRecyclerViewLayout.visibility = View.GONE
                        binding.folderRecyclerViewLayout.visibility = View.GONE
                    }else if (data != null) {
                        searchDatabase(data)
                    }
                    return true
                }

            })
//            searchView.setQuery(viewModel.searchText.value, true)


        }else if (viewModel.changeFragment == "ChangedGoBack"){

            val menuItem = menu.findItem(R.id.search_menu_id)
        val searchManager =  activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = menuItem.actionView  as androidx.appcompat.widget.SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

        searchView.isSubmitButtonEnabled = true
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = "Search"
        searchView.setQuery("", false)
        searchView.maxWidth= Int.MAX_VALUE

        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val data = p0
                viewModel.searchText.value = data
                Log.i("TestingApp"," Submit Search Text in ViewModel --> ${viewModel.searchText.value}")
                if (data == ""){
                    binding.notesRecyclerViewLayout.visibility = View.GONE
                    binding.folderRecyclerViewLayout.visibility = View.GONE
                }else if (data != null) {
                    searchDatabase(data)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val data = p0
                viewModel.searchText.value = data
                Log.i("TestingApp","Change Search Text in ViewModel --> ${viewModel.searchText.value}")
                if (data == ""){
                    binding.notesRecyclerViewLayout.visibility = View.GONE
                    binding.folderRecyclerViewLayout.visibility = View.GONE
                }else if (data != null) {
                    searchDatabase(data)
                }
                return true
            }

        })
    }

        super.onPrepareOptionsMenu(menu)
    }

    private fun searchDatabase(searchText: String){

        var searchTextu = searchText

        searchTextu = "%$searchText%"

        viewModel.searchNoteDatabase(searchTextu).observe(this.viewLifecycleOwner, Observer {list ->

            list?.let {
                if (list.isEmpty()){
                    binding.notesRecyclerViewLayout.visibility = View.GONE
                }else{
                    binding.notesRecyclerViewLayout.visibility = View.VISIBLE
                }
                searchNoteDataAdpater.submitList(it)
            }

        })

        viewModel.searchFolderDatabase(searchTextu).observe(this.viewLifecycleOwner, Observer {list ->

            list?.let{
                if (list.isEmpty()){
                    binding.folderRecyclerViewLayout.visibility = View.GONE
                }else{
                    binding.folderRecyclerViewLayout.visibility = View.VISIBLE
                }
                searchFolderDataAdapter.submitList(it)
            }

        })
    }

    override fun onStart() {
        Log.i("TestingApp","Arrow enabled for go Back ")
        Log.i("TestingApp","onStart Called")
        if (viewModel.changeFragment != "NotChanged"){
            viewModel.changeFragment = "changed"
            viewModel.searchText.value?.let { searchDatabase(it) }
            val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = sharedPreference?.edit()
            editor?.putBoolean("SELECTED",false)
            editor?.apply()
            editor?.commit()

        }
        val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("SELECTED",true)
        editor?.apply()
        editor?.commit()
        requireActivity().invalidateOptionsMenu()
        super.onStart()
    }

    override fun onStop() {
        Log.i("TestingApp","Arrow enabled for go Back ")
        val sharedPreference = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("SELECTED",false)
        editor?.apply()
        editor?.commit()
        (activity as HomePageActivity).hideKeyboard()
        super.onStop()
    }

    override fun onDestroy() {
        (activity as HomePageActivity).hideKeyboard()
        super.onDestroy()
    }

}