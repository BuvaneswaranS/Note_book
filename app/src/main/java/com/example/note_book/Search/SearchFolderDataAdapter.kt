package com.example.note_book.Search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.R

class SearchFolderDataAdapter(searchViewModel: SearchViewModel, defaultFolderId: String): ListAdapter<FolderData, SearchFolderDataAdapter.ViewHolder>(folderDataChangeCallBack()) {

    var viewModel: SearchViewModel = searchViewModel

    var defaultFolderIdAdapter = defaultFolderId

    val LAYOUT_ONE: Int = 0
    val LAYOUT_TWO: Int = 1

    override fun getItemViewType(position: Int): Int {

        if ((position == 0) && (viewModel.searchFolderData.value?.get(position)?.folderId == defaultFolderIdAdapter )){
            return LAYOUT_ONE
        }else {
            return LAYOUT_TWO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFolderDataAdapter.ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_default_folder_display, parent, false)
        if (viewType == LAYOUT_ONE){
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_default_folder_display, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_folder_search_display, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchFolderDataAdapter.ViewHolder, position: Int) {
        val folderData: FolderData = getItem(position)

        holder.bind(folderData)

        if (defaultFolderIdAdapter == folderData.folderId){

        }

        holder.itemView.setOnClickListener {view ->
            viewModel.changeFragment = "Changed"
//            findNavController(view).navigate(SearchFragmentDirections.actionSearchFragmentToDisplayFolderContentFragment(displayFolderId = folderData.folderId , displayFolderName = folderData.folderName , defaultFolderId = defaultFolderIdAdapter, folderListSize = 0))
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var folder_title: TextView
        var defaultImage: ImageView

        init {
            folder_title = itemView.findViewById(R.id.search_item_folder_title)
            defaultImage = itemView.findViewById(R.id.search_item_layout_user_Definied_folder_icon)
        }

        fun bind(folderData: FolderData){
            if (defaultFolderIdAdapter == folderData.folderId){
                defaultImage.setImageResource(R.drawable.ic_notebook)
            }
            folder_title.setText(folderData.folderName)
        }
    }

    class folderDataChangeCallBack: DiffUtil.ItemCallback<FolderData>(){
        override fun areItemsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

}