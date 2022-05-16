package com.example.note_book.NotebookDatabase

import android.icu.text.CaseMap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import kotlin.coroutines.coroutineContext

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

    fun getSearchData(): Flow<List<FolderData>>{
        return folderDataDao.getSearchFolderData()
    }

    fun getSearchFolderData(searchText: String): LiveData<List<FolderData>>{
        return folderDataDao.getSearchFolderData(searchText)
    }

//  -------------------------------------------------------------------------------------------

    suspend fun getFolderListSortTitleAscending(): LiveData<List<FolderData>>{
        return folderDataDao.getFolderTitleSortASC()
    }

    suspend fun getFolderListSortTitleDescending(): LiveData<List<FolderData>>{
        return folderDataDao.getFolderTitleSortDESC()
    }

    suspend fun getFolderListSortCreatedTimeAscending(): LiveData<List<FolderData>>{
        return folderDataDao.getFolderCreatedTimeSortASC()
    }

    suspend fun getFolderListSortCreatedTimeDescending(): LiveData<List<FolderData>>{
        return folderDataDao.getFolderCreatedTimeSortDESC()
    }

    suspend fun getFolderListSortModifiedTimeAscending(): LiveData<List<FolderData>>{
        return folderDataDao.getFolderModifiedTimeSortASC()
    }

    suspend fun getFolderListSortModifiedTimeDescending(): LiveData<List<FolderData>>{
        return folderDataDao.getFolderModifiedTimeSortDESC()
    }
//  -------------------------------------------------------------------------------------------



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

    fun getSearchNoteData(): Flow<List<NoteData>>{
        return noteDataDao.getAllNotesSearchCard()
    }

    fun getSearchNoteData(searchText: String): Flow<List<NoteData>>{
        return noteDataDao.getSearchNotesCard(searchText)
    }
//  -------------------------------------------------------------------------------------------

    suspend fun getNoteListSortTitleAscending(): LiveData<List<NoteData>>{
        return noteDataDao.getTitleSortAscending()
    }

    suspend fun getNoteListSortTitleDescending(): LiveData<List<NoteData>>{
        return noteDataDao.getTitleSortDescending()
    }

    suspend fun getNoteListSortCreatedTimeAscending(): LiveData<List<NoteData>>{
        return noteDataDao.getCreatedTimeSortOld()
    }

    suspend fun getNoteListSortCreatedTimeDescending(): LiveData<List<NoteData>>{
        return noteDataDao.getCreatedTimeSortNew()
    }

    suspend fun getNoteListSortModifiedTimeAscending(): LiveData<List<NoteData>>{
        return noteDataDao.getModifiedTimeSortOld()
    }

    suspend fun getNoteListSortModifiedTimeDescending(): LiveData<List<NoteData>>{
        return noteDataDao.getModifiedTimeSortNew()
    }

//  -------------------------------------------------------------------------------------------
//  Display Notes based on the Folder
//  -------------------------------------------------------------------------------------------

    suspend fun getFolderNoteListSortTitleAscending(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderBasedNoteTitleSortAscending(folderId)
    }

    suspend fun getFolderNoteListSortTitleDescending(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderBasedNoteTitleSortDescending(folderId)
    }

    suspend fun getFolderNoteListSortCreatedTimeAscending(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderBasedNoteCreatedTimeSortOld(folderId)
    }

    suspend fun getFolderNoteListSortCreatedTimeDescending(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderBasedNoteCreatedTimeSortOld(folderId)
    }

    suspend fun getFolderNoteListSortModifiedTimeAscending(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderBasedNoteModifiedTimeSortOld(folderId)
    }

    suspend fun getFolderNoteListSortModifiedTimeDescending(folderId: String): LiveData<List<NoteData>>{
        return noteDataDao.getFolderBasedNoteModifiedTimeSortNew(folderId)
    }

//  -------------------------------------------------------------------------------------------
//  Favourite
//  -------------------------------------------------------------------------------------------

    suspend fun getFavNoteListSortTitleAscending(): LiveData<List<NoteData>>{
        return noteDataDao.getFavTitleSortAscending(fav = true)
    }

    suspend fun getFavNoteListSortTitleDescending(): LiveData<List<NoteData>>{
        return noteDataDao.getFavTitleSortDescending(fav = true)
    }

    suspend fun getFavNoteListSortCreatedTimeAscending(): LiveData<List<NoteData>>{
        return noteDataDao.getFavCreatedTimeSortOld(fav = true)
    }

    suspend fun getFavNoteListSortCreatedTimeDescending(): LiveData<List<NoteData>>{
        return noteDataDao.getFavCreatedTimeSortNew(fav = true)
    }

    suspend fun getFavNoteListSortModifiedTimeAscending(): LiveData<List<NoteData>>{
        return noteDataDao.getFavModifiedTimeSortOld(fav = true)
    }

    suspend fun getFavNoteListSortModifiedTimeDescending(): LiveData<List<NoteData>>{
        return noteDataDao.getFavModifiedTimeSortNew(fav = true)
    }

//  -------------------------------------------------------------------------------------------
//  -------------------------------------------------------------------------------------------

}