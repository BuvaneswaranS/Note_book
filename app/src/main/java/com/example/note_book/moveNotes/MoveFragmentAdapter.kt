package com.example.note_book.moveNotes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.R

class MoveFragmentAdapter(moveFragmentViewModel: MoveFragmentViewModel, defaultFolderId: String, val moveToFolderClickListener: moveItemClickListener): ListAdapter<FolderData, MoveFragmentAdapter.ViewHolder>(folderDataChangeCallBack()) {

    var viewModel: MoveFragmentViewModel = moveFragmentViewModel

    var defaultFolderIdAdapter = defaultFolderId

    val LAYOUT_ONE: Int = 0
    val LAYOUT_TWO: Int = 1

    init {
        Log.i("TestingApp","Default Folder id -> ${defaultFolderIdAdapter}")
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("TestingApp","Position ${position}")
        Log.i("TestingApp","Default Id from the Adapter -> ${viewModel.noteListFolder.value?.get(position)?.folderId.toString()}")
        if (defaultFolderIdAdapter == viewModel.folder_list?.value?.get(position)?.folderId.toString()){
            Log.i("TestingApp","Default Id from the Adapter -> ${viewModel.noteListFolder.value?.get(position)?.folderId.toString()}")
            return LAYOUT_ONE
        }else{
            return LAYOUT_TWO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveFragmentAdapter.ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_user_created_folder, parent, false)
        if(viewType == LAYOUT_ONE){
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_default_folder, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_user_created_folder, parent, false)
        }
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: MoveFragmentAdapter.ViewHolder, position: Int) {
        var folderData: FolderData = getItem(position)
        holder.bind(folderData)
        holder.itemView.setOnClickListener {
//            Log.i("TestingApp","MoveFolderItemClicked")
            moveToFolderClickListener.moveFiles(folderData)
            viewModel.movingStarted.value = true
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var folder_title: TextView

        init {
            folder_title = itemView.findViewById(R.id.folder_content_folder_title)
        }

        fun bind(folderData: FolderData){
            folder_title.setText(folderData.folderName)
        }
    }
}

interface moveItemClickListener{
    fun moveFiles(folderData: FolderData)
}

class folderDataChangeCallBack: DiffUtil.ItemCallback<FolderData>(){
    override fun areItemsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}