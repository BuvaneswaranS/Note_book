package com.example.note_book.HomeFragment


import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NoteData
import com.example.note_book.NotebookDatabase.NotebookDatabase
import kotlinx.coroutines.*

class HomeFragmentViewModel(val application: Application): ViewModel() {

//   Declaration of FolderRepository
     val folderRepository: FolderRepository

    var isEnabled = MutableLiveData<Boolean>()

    var allItemsSelected = MutableLiveData<Boolean>()

    var checkBoxSelected = MutableLiveData<Boolean>()

    var filesCame = MutableLiveData<Boolean>()
    var viewModelCreated = MutableLiveData<Boolean>()
    var donedeSelectingall = MutableLiveData<Boolean>()

//    var viewModelCreated = MutableLiveData<Boolean>()

    var selectedList = MutableLiveData<MutableList<String>>()

    var drawerState = MutableLiveData<String>()

    var deleteButtonEnabled = MutableLiveData<Boolean>()

    var displayFolderDataDeleted = MutableLiveData<Boolean>()

    var deletedStarted = MutableLiveData<Boolean>()

    lateinit var folder_list: LiveData<List<FolderData>>

    var all_folder_id_list: MutableList<String>? = null

    var allNotesCard: LiveData<List<NoteData>>? = null

    val viewModelJob = Job()

    val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
        val folderDao = NotebookDatabase.getDatabaseInstance(application).folderDataDao
        val noteDataDao = NotebookDatabase.getDatabaseInstance(application).noteDataDao
        folderRepository = FolderRepository(folderDao, noteDataDao)
        folder_list = folderRepository.folderDataDao.getAllDataByFolder()
        isEnabled.value = false
        deleteButtonEnabled.value = true
        displayFolderDataDeleted.value = false
        deletedStarted.value = false
        allItemsSelected.value = false
        viewModelCreated.value = true
        donedeSelectingall.value = false
        filesCame.value = false
        getNotesCard()
        drawerState.value = "notebooks"
        checkBoxSelected.value = false
    }

    var data: String? =  ""

    override fun onCleared() {
        folder_SelectAll_And_DeSelectAll(false)
        super.onCleared()
    }

    fun updateDefaultFolder(){
        scope.launch {
            folderRepository.insertDefaultFolder()
        }
    }

    fun insertUserDefinedFolder(folderData: FolderData){
        scope.launch {
            folderRepository.insert(folderData)
        }
    }

    fun updateFolderName(folderData: FolderData){
        scope.launch {
            folderRepository.updatefolderName(folderData)
        }
    }

    fun getDefaultFolderId(){
        scope.launch {
            return@launch withContext(Dispatchers.Main){
                data = folderRepository.getDefaultFolderId().folderId
//                Log.i("TestingApp","folder id -> ${id}")

            }

        }
    }

    fun folder_SelectAll_And_DeSelectAll(value: Boolean){
        for (folder_data in folder_list?.value!!){
            if (value == true){
                    var update_Folder_Data = FolderData(folderId =  folder_data.folderId, folderName = folder_data.folderName , createdTime = folder_data.createdTime , modifiedTime = folder_data.modifiedTime , selected_item = value)
                    scope.launch{
                        folderRepository.updateFolderData(update_Folder_Data)
                    }
            }else if(value == false){
                        var update_Folder_Data = FolderData(folderId =  folder_data.folderId, folderName = folder_data.folderName , createdTime = folder_data.createdTime , modifiedTime = folder_data.modifiedTime , selected_item = value)
                        scope.launch{
                            folderRepository.updateFolderData(update_Folder_Data)
                        }
            }
        }
    }

    fun getFolderIdList(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                all_folder_id_list = folderRepository.get_folder_id_list()
            }

        }
    }


    fun deleteFolderData(){
        deletedStarted.value = true
        for (folderId in selectedList?.value!!){

            scope.launch {
                folderRepository.deleteFolderData(folderId)
            }

            scope.launch {
                folderRepository.delete_Note_Data_folder_id(folderId)
            }
        }
    }

    fun getNotesCard(){
        scope.launch {
            return@launch withContext(Dispatchers.IO){
                allNotesCard = folderRepository.getAllNotesCard()
            }
        }
    }

}