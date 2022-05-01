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

    suspend fun getFolderListMove(folderId: String): LiveData<List<FolderData>>{
        return folderDataDao.getAllDataByFolderMove(folderId)
    }

    suspend fun getFolderData(folderId: String): FolderData{
        return folderDataDao.getFolderData(folderId)
    }

    suspend fun updateFolderData(folderData: FolderData){
        folderDataDao.updateFolderData(folderData)
    }

    suspend fun get_folder_id_list(): MutableList<String>{
        return folderDataDao.getAllFolderId()
    }

    suspend fun  deleteFolderData(folderId: String){
        folderDataDao.deleteFolderData(folderId)
    }

    suspend fun getFolderListSize(): LiveData<Int>{
        return folderDataDao.getSizeFolders()
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

    suspend fun updateFavouriteNoteData(noteData: NoteData){
        noteDataDao.updateFavouriteNoteData(noteData)
    }

    suspend fun updateUnFavouriteNoteData(noteData: NoteData){
        noteDataDao.updateFavouriteNoteData(noteData)
    }

    suspend fun getNotesData(noteId: String): NoteData{
        return noteDataDao.getNoteData(noteId)
    }

    suspend fun getDefaultFolderId(): FolderData{
        return withContext(Dispatchers.IO){
            folderDataDao.getDefaultFolderId()
        }
    }

    suspend fun getAllNotesCard(): LiveData<List<NoteData>>{
        return noteDataDao.getAllNotesCard()
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

    suspend fun getAllNoteId(): MutableList<String>{
        return noteDataDao.getAllNoteId()
    }

    suspend fun delete_Note_Data_folder_id(folderId: String){
        noteDataDao.delete_Notedata_Folder_id(folderId)
    }

    suspend fun getFavouriteNotes(favourite: Boolean): LiveData<List<NoteData>>{
        return noteDataDao.getFavouriteNotes(favourite)
    }
}