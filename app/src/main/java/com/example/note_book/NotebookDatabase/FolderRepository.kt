package com.example.note_book.NotebookDatabase

import android.icu.text.CaseMap
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

class FolderRepository(val folderDataDao: FolderDataDao, val noteDataDao: NoteDataDao){


//    ---------------------------------------------------------------------------------------------------------------------------
//    Folder Area
//    ---------------------------------------------------------------------------------------------------------------------------
    suspend fun insertDefaultFolder(){
//        Log.i("TestingApp","Entered Default folder function")
        folderDataDao.insertDefaultFolder(FolderData(folderName = "Default Folder", createdTime = System.currentTimeMillis()))
//        Log.i("TestingApp","inserted successfully")
    }


    suspend fun insert(folderData: FolderData){
        folderDataDao.insertUserDefinedFolder(folderData)
    }

    suspend fun updatefolderName(folderData: FolderData){
        folderDataDao.updateFolderName(folderData)
    }

    suspend fun updatedFolderModifiedTime(folderData: FolderData){
        folderDataDao.updateFolderModifiedTime(folderData)
    }

    suspend fun getFolderList(): LiveData<List<FolderData>>{
        return folderDataDao.getAllDataByFolder()
    }

    suspend fun getFolderData(folderId: String): FolderData{
        return folderDataDao.getFolderData(folderId)
    }

//    ---------------------------------------------------------------------------------------------------------------------------
//    Note Area
//    ----------------------------------------------------------------------------------------------------------------------------

    suspend fun insertNoteData(noteData: NoteData){
        noteDataDao.insertNoteData(noteData)
    }

    suspend fun updateNoteData(noteData: NoteData){
        noteDataDao.updateNoteData(noteData)
    }

    suspend fun getDefaultFolderId(): FolderData{
        return withContext(Dispatchers.IO){
            folderDataDao.getDefaultFolderId()
        }
    }

    suspend fun getNotesListFolder(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderContent(folderId)
    }

    suspend fun deleteNoteDate(noteId: String){
        noteDataDao.deleteNoteData(noteId)
    }

    suspend fun getNoteData(folderId: String): MutableList<String>{
        return noteDataDao.getFolderNoteData(folderId)
    }

}