package com.example.note_book.NotebookDatabase

import android.icu.text.CaseMap
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FolderRepository(val folderDataDao: FolderDataDao) {


    init {

    }

    suspend fun insertDefaultFolder(){
        Log.i("TestingApp","Entered Default folder function")
        folderDataDao.insertDefaultFolder(FolderData(folderName = "Default Folder", createdTime = System.currentTimeMillis()))
        Log.i("TestingApp","inserted successfully")
    }

    suspend fun insert(folderData: FolderData){
        folderDataDao.insertUserDefinedFolder(folderData)
    }

    suspend fun updatefolderName(folderData: FolderData){
        folderDataDao.updateFolderName(folderData)
    }

    suspend fun getFolderList(): LiveData<List<FolderData>>{
        return folderDataDao.getAllDataByFolder()
    }




}