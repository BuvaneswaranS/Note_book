package com.example.note_book.HomeFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.HomePageActivity
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.R
import com.example.note_book.databinding.FragmentHomeBinding

class HomeFragment() : Fragment(R.layout.fragment_home_), itemClickListener {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var displayfolderAdapter: DisplayFolderListAdapter

    val shared_value = "drawer_shared"

    //  For FAB button animation
    private val rotateOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_open_animation
        )
    }
    private val rotateCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_close_animation
        )
    }
    private val fromBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.from_animation
        )
    }
    private val toBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.to_animation
        )
    }

    private var clicked: Boolean = false

    constructor(parcel: Parcel) : this() {
        clicked = parcel.readByte() != 0.toByte()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.show()


        val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = sharedPreference?.edit()
        editor?.putBoolean("IN",true)
        editor?.apply()
        editor?.commit()

        val application = requireNotNull(this.activity).application


        val viewModelFactory = HomeFragmentViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeFragmentViewModel::class.java)

        displayfolderAdapter = viewModel.data?.let { DisplayFolderListAdapter(viewModel, this) }!!

        binding.homeFragmentRecyclerview.layoutManager = GridLayoutManager(this.context, 2)

        binding.homeFragmentRecyclerview.adapter = displayfolderAdapter

        viewModel.folder_list.observe(this.viewLifecycleOwner, Observer { list ->
            if (viewModel.folder_list.value?.isEmpty() == false) {
                viewModel.getDefaultFolderId()
//                binding.dispalyNoNotesCard.visibility = View.INVISIBLE
                viewModel.filesCame.value = true

            }
            displayfolderAdapter.submitList(list)
        })

        viewModel.viewModelCreated.observe(this.viewLifecycleOwner, Observer { created ->
            if (created) {

                viewModel.filesCame.observe(this.viewLifecycleOwner, Observer { filesCame ->
                    if (filesCame) {

                        if (viewModel.donedeSelectingall.value == false) {

                            viewModel.folder_SelectAll_And_DeSelectAll(false)
                            viewModel.donedeSelectingall.value = true
                        }
                    }
                })
            }
        })


        binding.addBtn.setOnClickListener {
            startAnimation()
//            if(viewModel.folderList.value?.isEmpty() == true){
////                Log.i("TestingApp","Ëntered")
//                viewModel.updateDefaultFolder()
//                viewModel.getDefaultFolderId()
//                binding.dispalyNoNotesCard.visibility = View.GONE
//            }

        }


        binding.addNoteBtn.setOnClickListener { view ->
            Navigation.findNavController(view)
                .navigate(HomeFragmentDirections.actionHomeFragmentToCreateNewNoteFragment(viewModel.data.toString()))
        }

        binding.addFolder.setOnClickListener { view ->
            val data = FolderData(folderName = "Untitled", createdTime = System.currentTimeMillis())
            viewModel.insertUserDefinedFolder(data)
        }

        val builder = AlertDialog.Builder(this.context)
        builder.setView(inflater.inflate(R.layout.loading_menu_moving, null))
        val dialogBox = builder.create()
        viewModel.deletedStarted.observe(this.viewLifecycleOwner, Observer { started ->

            if (started) {
                dialogBox.show()
            } else if (!started) {
                dialogBox.dismiss()
            }
        })


//      ---------------------------------------------------------------------------------------------------------------------------------------
//        Notebooks
//      ---------------------------------------------------------------------------------------------------------------------------------------
//        Selected List
        viewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->
            var selectedListSize: Int = list.size
            var entireListSize: Int? = viewModel.folder_list.value?.size
            if (selectedListSize == entireListSize){
                viewModel.allItemsSelected.value = true
                viewModel.checkBoxSelected.value = true
            }else{
                viewModel.allItemsSelected.value = false
                viewModel.checkBoxSelected.value = false
            }
        })


//        Selection Enabled
        viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { Enabled ->
            if (Enabled) {
                val selected = "sharedSelect"
                val sharedPreferences = activity?.getSharedPreferences(selected, Context.MODE_PRIVATE)
                var editor: SharedPreferences.Editor? = sharedPreferences?.edit()
                editor?.putBoolean("SELECTED",true)
                editor?.apply()
                editor?.commit()


                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button)

                (activity as HomePageActivity).drawerLocked()

                requireActivity().invalidateOptionsMenu()

                viewModel.getFolderIdList()
                viewModel.getDefaultFolderId()

                binding.addBtn.visibility = View.GONE

                viewModel.selectedList.observe(this.viewLifecycleOwner, Observer { list ->
                    (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("${list.size} selected")
                    if (list.size == 0){
                        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("NoteBook")
                    }
                })

            } else if (!Enabled) {
                (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("Notebook")
                val sharedPreference = activity?.getSharedPreferences(shared_value, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = sharedPreference?.edit()
                editor?.putBoolean("IN",true)
                editor?.apply()
                editor?.commit()
                binding.addBtn.visibility = View.VISIBLE
                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
                (requireActivity() as AppCompatActivity).supportActionBar?.setTitle("NoteBook")
                (activity as HomePageActivity).drawerUnLocked()
//                requireActivity().invalidateOptionsMenu()
            }
        })



//      ---------------------------------------------------------------------------------------------------------------------------

        viewModel.deleteButtonEnabled.observe(this.viewLifecycleOwner, Observer { enabled ->
            if (viewModel.drawerState.value == "notebooks") {
                if (enabled == true) {
                    requireActivity().invalidateOptionsMenu()
//                        binding.displayFolderContentDeleteButton.isEnabled = true
                    if (viewModel.isEnabled.value == true) {
//                            binding.displayFolderContentDeleteButton.visibility = View.VISIBLE
                    }

                } else if (enabled == false) {
                    requireActivity().invalidateOptionsMenu()
//                binding.displayFolderContentDeleteButton.isEnabled = false
//                Log.i("TestingApp","Entered into !Enabled ${enabled}")
//                binding.displayFolderContentDeleteButton.visibility = View.INVISIBLE
                }
            }
            })
        return binding.root
    }

    private fun startAnimation() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.addNoteBtn.visibility = View.VISIBLE
            binding.addFolder.visibility = View.VISIBLE
        } else {
            binding.addNoteBtn.visibility = View.GONE
            binding.addFolder.visibility = View.GONE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.addNoteBtn.startAnimation(fromBottomAnimation)
            binding.addFolder.startAnimation(fromBottomAnimation)
            binding.addBtn.startAnimation(rotateOpenAnimation)
        } else {
            binding.addNoteBtn.startAnimation(toBottomAnimation)
            binding.addFolder.startAnimation(toBottomAnimation)
            binding.addBtn.startAnimation(rotateCloseAnimation)
        }
    }

    override fun changeFolderName(folderData: FolderData) {
//        This place is for showing dialog

        var dialogBox = UpdateFolderTitleFragment(viewModel, folderData)

        if (viewModel.isEnabled.value == false) {
            dialogBox.show(childFragmentManager, "dialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            if (viewModel.isEnabled.value == true){
                (requireActivity() as AppCompatActivity).supportActionBar?.title = "Notebook"
                viewModel.isEnabled.value = false
                viewModel.folder_SelectAll_And_DeSelectAll(false)
                val empty_list = mutableListOf<String>()
                displayfolderAdapter.data = empty_list
                viewModel.selectedList.value = empty_list
                viewModel.deleteButtonEnabled.value = true
                viewModel.deletedStarted.value = false

            }
        }

         else if (item.itemId == R.id.delete_button) {

                if (viewModel.drawerState.value == "notebooks"){

                    if (viewModel.selectedList.value?.isEmpty() == true) {

                    } else {
                        var builder = AlertDialog.Builder(this.context)
                        builder.setTitle("Delete folder")
                        builder.setMessage("Do you want to delete?")
                        builder.setNegativeButton("No") { DialogInterface, which -> }
                        builder.setPositiveButton("Yes") { DialogInterface, which ->
                            viewModel.isEnabled.value = false
                            viewModel.folder_SelectAll_And_DeSelectAll(false)
                            viewModel.deleteFolderData()
                            val empty_list = mutableListOf<String>()
                            displayfolderAdapter.data = empty_list
                            viewModel.selectedList.value = empty_list
                            viewModel.deleteButtonEnabled.value = true
                            viewModel.deletedStarted.value = false
//                            binding.homeFragmentToolbar.setTitle("Notebook")
//                            binding.homeFragmentToolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
//                                binding.displayFolderContentCheckedBox.isChecked = false
                        }
                        var dialogBox = builder.create()
                        builder.show()
                        builder.setCancelable(false)
                    }

                }


        }else if(item.itemId == R.id.select_button){
                if (viewModel.checkBoxSelected.value == false){
                    viewModel.checkBoxSelected.value = true
                }else if(viewModel.checkBoxSelected.value == true){
                    viewModel.checkBoxSelected.value = false
                }

                if (viewModel.checkBoxSelected.value == true){
                    if (viewModel.allItemsSelected.value == false){
                        viewModel.folder_SelectAll_And_DeSelectAll(true)
                        var full_list = mutableListOf<String>()
                        viewModel.all_folder_id_list?.let { full_list.addAll(it) }
                        displayfolderAdapter.data = full_list
                        viewModel.selectedList.value = full_list
                        viewModel.deleteButtonEnabled.value = false
                        viewModel.allItemsSelected.value = true
                        viewModel.deleteButtonEnabled.value = false
                        requireActivity().invalidateOptionsMenu()
                    }
                }else if (viewModel.checkBoxSelected.value == false){
                    if (viewModel.allItemsSelected.value == true) {
                        viewModel.folder_SelectAll_And_DeSelectAll(false)
                        val empty_list = mutableListOf<String>()
                        displayfolderAdapter.data = empty_list
                        viewModel.selectedList.value = empty_list
                        viewModel.deleteButtonEnabled.value = true
                        viewModel.allItemsSelected.value = false
                        requireActivity().invalidateOptionsMenu()
                    }
                }
            }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        viewModel.drawerState.observe(this.viewLifecycleOwner, Observer { value ->
                if (value == "notebooks") {
                    viewModel.isEnabled.observe(this.viewLifecycleOwner, Observer { value ->
                        if (value) {

                            val moveItem = menu.findItem(R.id.move_button)
                            moveItem.setVisible(false)


                            val copyItem = menu.findItem(R.id.copy_button)
                            copyItem.setVisible(false)


                            val favourite = menu.findItem(R.id.favourite_button)
                            favourite.setVisible(false)

                            if (viewModel.checkBoxSelected.value == true){
                                if (viewModel.allItemsSelected.value == false){
                                    val selectButton = menu.findItem(R.id.select_button)
                                    selectButton.setIcon(R.drawable.ic_baseline_select_all_24)
                                }
                                if (viewModel.allItemsSelected.value == true){
                                    val selectButton = menu.findItem(R.id.select_button)
                                    selectButton.setIcon(R.drawable.ic_select_all_on)
                                }
                            }else if (viewModel.checkBoxSelected.value == false){
                                if (viewModel.allItemsSelected.value == true) {
                                    val selectButton = menu.findItem(R.id.select_button)
                                    selectButton.setIcon(R.drawable.ic_select_all_on)
                                } else if (viewModel.allItemsSelected.value == false) {
                                    val selectButton = menu.findItem(R.id.select_button)
                                    selectButton.setIcon(R.drawable.ic_baseline_select_all_24)
                                }
                            }

                            viewModel.allItemsSelected.observe(this.viewLifecycleOwner, Observer {value ->
                                if (value){
                                    val selectButton = menu.findItem(R.id.select_button)
                                    selectButton.setIcon(R.drawable.ic_select_all_on)
                                }else if(!value){
                                    val selectButton = menu.findItem(R.id.select_button)
                                    selectButton.setIcon(R.drawable.ic_baseline_select_all_24)
                                }
                            })

                            if (viewModel.deleteButtonEnabled.value == true){
                                val deleteItem = menu.findItem(R.id.delete_button)
                                deleteItem.setVisible(true)
                                deleteItem.setShowAsAction(2)
                            }else if (viewModel.deleteButtonEnabled.value == false){
                                val deleteItem = menu.findItem(R.id.delete_button)
                                deleteItem.setVisible(false)
                                deleteItem.setShowAsAction(2)
                            }
                            val searchItem = menu.findItem(R.id.search_button)
                            searchItem.setVisible(false)

                            val checkBox = menu.findItem(R.id.select_button)
                            checkBox.setVisible(true)

                        } else if (!value) {

                            val searchItem = menu.findItem(R.id.search_button)
                            searchItem.setVisible(true)

                            val moveItem = menu.findItem(R.id.move_button)
                            moveItem.setVisible(false)


                            val copyItem = menu.findItem(R.id.copy_button)
                            copyItem.setVisible(false)


                            val favourite = menu.findItem(R.id.favourite_button)
                            favourite.setVisible(false)


                            val deleteItem = menu.findItem(R.id.delete_button)
                            deleteItem.setVisible(false)
                            deleteItem.setShowAsAction(0)


                            val checkBox = menu.findItem(R.id.select_button)
                            checkBox.setVisible(false)

                        }
                    })
                }
            })
        super.onPrepareOptionsMenu(menu)
    }

}

/**
 * //        CloseButton
//        binding.folderListCloseButton.setOnClickListener {
//            viewModel.isEnabled.value = false
//            binding.displayFolderContentCheckedBox.isChecked = false
//            viewModel.deleteButtonEnabled.value = true
//            if (viewModel.selectedList.value?.isEmpty() == false){
//                viewModel.folder_SelectAll_And_DeSelectAll(false)
//            }
//            val empty_list = mutableListOf<String>()
//            displayfolderAdapter.data = empty_list
//            viewModel.selectedList.value = empty_list
//        }

//        viewModel.deleteButtonEnabled.observe(this.viewLifecycleOwner, Observer { enabled ->
//            if (enabled == true){
//                binding.displayFolderContentDeleteButton.isEnabled = true
//                if(viewModel.isEnabled.value == true){
//                    binding.displayFolderContentDeleteButton.visibility = View.VISIBLE
//                }
////                Log.i("TestingApp","Entered into Enabled ${enabled}")
////              Delete Button
//                binding.displayFolderContentDeleteButton.setOnClickListener {
//                    if(viewModel.selectedList.value?.isEmpty() == true){
//
//                    }else{
//                        var builder = AlertDialog.Builder(this.context)
//                        builder.setTitle("Delete folder")
//                        builder.setMessage("Do you want to delete?")
//                        builder.setNegativeButton("No"){DialogInterface, which -> }
//                        builder.setPositiveButton("Yes"){DialogInterface, which ->
//                            viewModel.isEnabled.value = false
////                            viewModel.folder_SelectAll_And_DeSelectAll(false)
//                            viewModel.deleteFolderData()
//                            val empty_list = mutableListOf<String>()
//                            displayfolderAdapter.data = empty_list
//                            viewModel.selectedList.value = empty_list
//                            viewModel.deleteButtonEnabled.value = true
//                            viewModel.deletedStarted.value = false
//                            binding.displayFolderContentCheckedBox.isChecked = false
//                        }
//                        var dialogBox = builder.create()
//                        builder.show()
//                        builder.setCancelable(false)
//                    }
//                }
//
//            }else if(enabled == false){
//                binding.displayFolderContentDeleteButton.isEnabled = false
////                Log.i("TestingApp","Entered into !Enabled ${enabled}")
//                binding.displayFolderContentDeleteButton.visibility = View.INVISIBLE
//            }
//        })

//        Delete Button
//        binding.displayFolderContentDeleteButton.setOnClickListener {
//                viewModel.isEnabled.value = false
//                binding.displayFolderContentCheckedBox.isChecked = false
//                viewModel.folder_SelectAll_And_DeSelectAll(false)
//                viewModel.deleteFolderData()
//                val empty_list = mutableListOf<String>()
//                displayfolderAdapter.data = empty_list
//                viewModel.selectedList.value = empty_list
//        }


//        CheckBox
//        binding.displayFolderContentCheckedBox.setOnCheckedChangeListener{ buttonView, isChecked ->
//            if (isChecked){
//                if (viewModel.allItemsSelected.value == false){
//                    viewModel.folder_SelectAll_And_DeSelectAll(true)
//                    var full_list = mutableListOf<String>()
//                    viewModel.all_folder_id_list?.let { full_list.addAll(it) }
//                    displayfolderAdapter.data = full_list
//                    viewModel.selectedList.value = full_list
//                    viewModel.deleteButtonEnabled.value = false
//                    viewModel.allItemsSelected.value = true
//                }
//            }else if (!isChecked){
//                if (viewModel.allItemsSelected.value == true) {
//                    viewModel.folder_SelectAll_And_DeSelectAll(false)
//                    val empty_list = mutableListOf<String>()
//                    displayfolderAdapter.data = empty_list
//                    viewModel.selectedList.value = empty_list
//                    viewModel.deleteButtonEnabled.value = true
//                    viewModel.allItemsSelected.value = false
//                }
//            }
//        }
}


//      ---------------------------------------------------------------------------------------------------------------------------


//      ---------------------------------------------------------------------------------------------------------------------------
//      All Notes Card
//      ---------------------------------------------------------------------------------------------------------------------------

//        binding.addNoteAllNotesRecyclerview.setOnClickListener{view ->
//            Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToCreateNewNoteFragment(viewModel.data.toString()))
//        }

//        allNoteCardViewModel.isEnabled.observe(this.viewLifecycleOwner, Observer {enabled ->
//            if (enabled){
////                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
////                binding.homeFragmentToolbar.setNavigationIcon(R.drawable.ic_back_button)
//                requireActivity().invalidateOptionsMenu()
//                allNoteCardViewModel.selectedList.observe(this.viewLifecycleOwner, Observer {list ->
//                    if (allNoteCardViewModel.isEnabled.value == true){
////                        binding.homeFragmentToolbar.setTitle(" ${list.size} selected")
//                    }
//                    val selectedListSize: Int = list.size
//                    allNoteCardViewModel.notesListFolder?.observe(this.viewLifecycleOwner, Observer { list ->
//                        if (selectedListSize == list.size){
//                            Log.i("TestingApp","List is Full")
//                            allNoteCardViewModel.allItemsSelected.value = true
//                        }else{
//                            Log.i("TestingApp","List is not Full")
//                            allNoteCardViewModel.allItemsSelected.value = false
//                        }
//                    })
//                })
//            }else if (!enabled){
////                binding.addNoteAllNotesRecyclerview.visibility = View.VISIBLE
////                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                requireActivity().invalidateOptionsMenu()
//                if(viewModel.drawerState.value == "allNoteCard"){
////                    binding.homeFragmentToolbar.setTitle("All Note Cards")
//                    binding.addNoteAllNotesRecyclerview.visibility = View.VISIBLE
//                }
//
//            }
//        })


////      Close Button
//        binding.allNotesRelatedCloseButton.setOnClickListener {
//
//            allNoteCardViewModel.isEnabled.value = false
//            binding.allNotesRelatedCheckedBox.isChecked = false
////            allNoteCardViewModel.selectAndDeSelectAll(false)
//            val data = mutableListOf<String>()
//            val emptyData = mutableListOf<NoteData>()
//            allNoteCardAdapter.data = data
//            allNoteCardViewModel.selectedList.value = data
//            allNoteCardViewModel.notesListSelected = emptyData
//        }
//
////      CheckBox
//        binding.allNotesRelatedCheckedBox.setOnCheckedChangeListener{ buttonView, isChecked ->
//            if (isChecked){
//                if(allNoteCardViewModel.allItemsSelected.value == false){
////                    Log.i("TestingApp","Entered true")
//                    allNoteCardViewModel.selectedAllItem.value = true
////                    allNoteCardViewModel.selectAndDeSelectAll(true)
//                    var data = mutableListOf<String>()
//                    allNoteCardViewModel.allNoteIdList?.let { data.addAll(it) }
//
//                    allNoteCardAdapter.data = data
//                    allNoteCardViewModel.selectedList.value = data
//                    allNoteCardViewModel.allItemsSelected.value = true
//                }
//
//            }else if (!isChecked){
//                if (allNoteCardViewModel.allItemsSelected.value == true){
//                    Log.i("TestingApp","Entered False")
////                    allNoteCardViewModel.selectAndDeSelectAll(false)
//                    val data = mutableListOf<String>()
//                    allNoteCardAdapter.data = data
//                    allNoteCardViewModel.selectedList.value = data
//                    allNoteCardViewModel.allItemsSelected.value = false
//                }
//            }
//        }

 * **/