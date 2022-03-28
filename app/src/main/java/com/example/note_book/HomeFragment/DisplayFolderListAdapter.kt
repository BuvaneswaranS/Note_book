package com.example.note_book.HomeFragment

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


class DisplayFolderListAdapter(val itemClickListener: itemClickListener): ListAdapter<FolderData,DisplayFolderListAdapter.ViewHolder>(folderDataCallBack()) {

    val LAYOUT_ONE:Int = 0
    val LAYOUT_TWO:Int = 1

    override fun getItemViewType(position: Int): Int {
        if(position == 0){
            return LAYOUT_ONE
        }else{
            return LAYOUT_TWO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayFolderListAdapter.ViewHolder {

            var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_user_created_folder, parent, false)
        if(viewType == LAYOUT_ONE){
//                Log.i("TestingApp1","Finally Entered")
                 view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_default_folder, parent, false)

            }else{
//                Log.i("TestingApp1","Entered")
                 view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_user_created_folder, parent, false)
            }
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: DisplayFolderListAdapter.ViewHolder, position: Int) {
        var folderData: FolderData = getItem(position)
        holder.bind(folderData)
//        This is for the title textView Clicking
        holder.folder_title.setOnClickListener {
            itemClickListener.changeFolderName(getItem(position))
        }

//       This is for the entire card to clicked
        holder.itemView.setOnClickListener {view ->
            Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToDisplayFolderContentFragment(folderData.folderId,folderData.folderName))
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var folder_title: TextView

        init {
            folder_title = itemView.findViewById(R.id.folder_content_folder_title)
        }

        fun bind(folderData: FolderData){
            folder_title.setText(folderData.folderName)
//            Log.i("TestingApp","${folderData.folderName}")
        }
    }
}

interface itemClickListener {
    fun changeFolderName(folderData: FolderData)
}

class folderDataCallBack: DiffUtil.ItemCallback<FolderData>(){
    override fun areItemsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
        return areItemsTheSame(oldItem,newItem)
    }
}