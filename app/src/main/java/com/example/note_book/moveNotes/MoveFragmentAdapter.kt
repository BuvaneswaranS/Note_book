package com.example.note_book.moveNotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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


    override fun getItemViewType(position: Int): Int {

        if (defaultFolderIdAdapter == viewModel.folderList?.value?.get(position)?.folderId.toString()){
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
        val folderData: FolderData = getItem(position)

        holder.bind(folderData)

        holder.itemView.setOnClickListener {

            viewModel.moveFolderTo = folderData.folderName

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