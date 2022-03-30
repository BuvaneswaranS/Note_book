package com.example.note_book.HomeFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.print.PrintAttributes
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.R
import com.example.note_book.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home_), itemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeFragmentViewModel

    private lateinit var displayfolderAdapter: DisplayFolderListAdapter

    //  For FAB button animation
    private val rotateOpenAnimation: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open_animation) }
    private val rotateCloseAnimation: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close_animation) }
    private val fromBottomAnimation: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_animation) }
    private val toBottomAnimation: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.to_animation) }

    private var clicked: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_, container, false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        val application = requireNotNull(this.activity).application
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        val repository = FolderRepository(folderDao, noteDataDao)
        val viewModelFactory = HomeFragmentViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(HomeFragmentViewModel::class.java)
//        viewModel.getDefaultFolderId()

        if(viewModel.folder_list.value?.isEmpty() == false){
//            Log.i("TestingApp","Ëntered")
            viewModel.getDefaultFolderId()
        }


        displayfolderAdapter = DisplayFolderListAdapter(this)

        binding.recyclerView.layoutManager = GridLayoutManager(this.context,2)

        binding.recyclerView.adapter  = displayfolderAdapter

        viewModel.folder_list.observe(this.viewLifecycleOwner, Observer {list ->
          displayfolderAdapter.submitList(list)
        })


        binding.addBtn.setOnClickListener{
            startAnimation()
            if(viewModel.folder_list.value?.isEmpty() == true){
//                Log.i("TestingApp","Ëntered")
                viewModel.updateDefaultFolder()
                viewModel.getDefaultFolderId()
            }

        }

        binding.addNoteBtn.setOnClickListener{view ->
            Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToCreateNewNoteFragment(viewModel.data.toString()))
        }

        binding.addFolder.setOnClickListener{view ->
            val data = FolderData(folderName = "Untitled", createdTime = System.currentTimeMillis())
            viewModel.insertUserDefinedFolder(data)
        }



        return binding.root
    }

    private fun startAnimation(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean){
        if(!clicked){
            binding.addNoteBtn.visibility = View.VISIBLE
            binding.addFolder.visibility = View.VISIBLE
        }else{
            binding.addNoteBtn.visibility = View.GONE
            binding.addFolder.visibility = View.GONE
        }
    }

    private fun setAnimation(clicked: Boolean){
        if (!clicked){
            binding.addNoteBtn.startAnimation(fromBottomAnimation)
            binding.addFolder.startAnimation(fromBottomAnimation)
            binding.addBtn.startAnimation(rotateOpenAnimation)
        }else{
            binding.addNoteBtn.startAnimation(toBottomAnimation)
            binding.addFolder.startAnimation(toBottomAnimation)
            binding.addBtn.startAnimation(rotateCloseAnimation)
        }
    }

    override fun changeFolderName(folderData: FolderData) {
//        This place is for showing dialog

        var dialogBox = UpdateFolderTitleFragment(viewModel, folderData)

        dialogBox.show(childFragmentManager,"dialog")



//        Initializing the builder
//        var builder = AlertDialog.Builder(this.context)
//        builder.setView(layoutInflater.inflate(R.layout.item_layout_update_folder_title, null))
////
//        val folder_change_update_name: EditText? =  view?.findViewById(R.id.update_folder_name_input)
//        Log.i("TestingApp","${folderData.folderName}")
//        folder_change_update_name?.setText("${folderData.folderName}")
//        builder.setTitle("Change Folder Name")
//        builder.setIcon(R.drawable.ic_create_dialog_box)

//        Initiliaze the Edit Text for the Dialog
//        var folder_change_update_name = EditText(this.context)
//        folder_change_update_name.setText("${folderData.folderName}")
//        folder_change_update_name.setHint("Enter your folder name")
//        folder_change_update_name.maxLines = 1
//        folder_change_update_name.inputType = InputType.TYPE_CLASS_TEXT
//
//        builder.setView(folder_change_update_name)

//        Log.i("Testing1App","${folder_change_update_name?.text}")
//        dialogBox.alertDialog.setPositiveButton("OK") { dialogInterface: DialogInterface, i ->
//            val data= FolderData(folderId = folderData.folderId,folderName = folder_change_update_name?.text.toString(), createdTime = folderData.createdTime, modifiedTime = System.currentTimeMillis())
////
//          Log.i("Testing1App","FolderNAME -> ${data.folderName}")
//
//            viewModel.updateFolderName(data)
//        }
//        builder.setNegativeButton("CANCEL",{dialogInterface: DialogInterface, i -> Int })
//
//        val alertDialog = builder.create()
//        alertDialog.show()

    }

}