package com.example.note_book.HomeFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DisplayFolderListAdapter(displayFolderListAdapterViewModel: HomeFragmentViewModel,val itemClickListener: itemClickListener): ListAdapter<FolderData,DisplayFolderListAdapter.ViewHolder>(folderDataCallBack()) {

    var viewModel: HomeFragmentViewModel = displayFolderListAdapterViewModel

    val LAYOUT_ONE:Int = 0
    val LAYOUT_TWO:Int = 1

    var viewModelJob = Job()

    var scope = CoroutineScope(Dispatchers.IO + viewModelJob)

//  This "data" --> will store the selected list of the note Data Id
    var data = mutableListOf<String>()

    init {
        viewModel.selectedList.value?.let { data.addAll(it) }
        viewModel.selectedList.value = data
    }

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

            view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_default_folder, parent, false)

        }else{
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

//      This is for the entire card is long clicked
        holder.itemView.setOnLongClickListener {
            if(viewModel.isEnabled.value == false){

//              isEnabled MutableLiveData is set to true
                viewModel.isEnabled.value = true

//              If the data list doesn't have the  folder Id
                if(data.contains(folderData.folderId) == false){

//                  Folder id is added to the data mutable List
                    data.add(folderData.folderId)

//                  If the default folder is long pressed selected --> if statement to check
                    if (folderData.folderId == viewModel.data){

//                      The delete button is diabled
                        viewModel.deleteButtonEnabled.value = false

                    }

//                  Folder Data is updated
                    val folderDataupdate = FolderData(folderId = folderData.folderId, folderName = folderData.folderName, createdTime = folderData.createdTime, modifiedTime = folderData.modifiedTime, selected_item = true)

                    scope.launch {
                        viewModel.folderRepository.updateFolderData(folderDataupdate)
                    }

                    viewModel.selectedList.value = data

                }
//              If the data is all
                else if(data.contains(folderData.folderId) == true){

                    data.remove(folderData.folderId)

                    if (folderData.folderId == viewModel.data){

                        viewModel.deleteButtonEnabled.value = true

                    }

                    val folderDataupdate = FolderData(folderId = folderData.folderId, folderName = folderData.folderName, createdTime = folderData.createdTime, modifiedTime = folderData.modifiedTime, selected_item = false)

                    scope.launch {
                        viewModel.folderRepository.updateFolderData(folderDataupdate)
                    }
                    viewModel.selectedList.value = data

                }
            }
            return@setOnLongClickListener true
        }


//       This is for the entire card to clicked
        holder.itemView.setOnClickListener {view ->

            if (viewModel.isEnabled.value == false){

                Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToDisplayFolderContentFragment(folderData.folderId,folderData.folderName, viewModel.data.toString(), viewModel.folder_list.value?.size ?: 0))

            }else if(viewModel.isEnabled.value == true){

                if(data.contains(folderData.folderId) == false){

                    data.add(folderData.folderId)

                    if (folderData.folderId == viewModel.data){

                        viewModel.deleteButtonEnabled.value = false

                    }

                    val folderDataupdate = FolderData(folderId = folderData.folderId, folderName = folderData.folderName, createdTime = folderData.createdTime, modifiedTime = folderData.modifiedTime, selected_item = true)

                    scope.launch {

                        viewModel.folderRepository.updateFolderData(folderDataupdate)

                    }
                    viewModel.selectedList.value = data

                }else if(data.contains(folderData.folderId) == true){

                    data.remove(folderData.folderId)

                    if (folderData.folderId == viewModel.data){

                        viewModel.deleteButtonEnabled.value = true

                    }

                    val folderDataupdate = FolderData(folderId = folderData.folderId, folderName = folderData.folderName, createdTime = folderData.createdTime, modifiedTime = folderData.modifiedTime, selected_item = false)

                    scope.launch {

                        viewModel.folderRepository.updateFolderData(folderDataupdate)

                    }
                    viewModel.selectedList.value = data

                }
            }
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var folder_title: TextView
        var folder_selected: ConstraintLayout

        init {
            folder_title = itemView.findViewById(R.id.folder_content_folder_title)
            folder_selected = itemView.findViewById(R.id.folder_selected)
        }

        fun bind(folderData: FolderData){
            folder_title.setText(folderData.folderName)
            if (folderData.selected_item){
                  folder_selected.visibility = View.VISIBLE
            }else if(!folderData.selected_item){
                folder_selected.visibility = View.GONE
            }

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