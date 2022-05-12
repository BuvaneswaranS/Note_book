package com.example.note_book.NotebookDatabase


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@Dao
interface FolderDataDao {

    @Insert
    fun insertDefaultFolder(folderData: FolderData)

    @Query("SELECT * from folder_table LIMIT 1")
    fun getDefaultFolderId(): FolderData

    @Insert
    fun insertUserDefinedFolder(folderData: FolderData)

    @Update
    fun updateFolderName(folderData: FolderData)

    @Update
    fun updateFolderModifiedTime(folderData: FolderData)

    @Update
    fun updateFolderData(folderData: FolderData)



    @Query("SELECT * from folder_table ORDER BY Created_Time ASC")
    fun getAllDataByFolder(): LiveData<List<FolderData>>

    @Query("SELECT * from folder_table WHERE folderId != :folderId ORDER BY Created_Time ASC")
    fun getAllDataByFolderMove(folderId: String): LiveData<List<FolderData>>


    @Query("SELECT * from folder_table WHERE folderId = :folderId")
    fun getFolderData(folderId: String): FolderData

    @Query("SELECT * from folder_table")
    fun getSearchFolderData(): Flow<List<FolderData>>

    @Query("SELECT * from folder_table WHERE Folder_Name LIKE :searchData")
    fun getSearchFolderData(searchData: String): LiveData<List<FolderData>>

    @Query("SELECT folderId from folder_table")
    fun getAllFolderId(): MutableList<String>

    @Query("SELECT COUNT(*) FROM folder_table ORDER BY Created_Time ASC")
    fun getSizeFolders(): LiveData<Int>

    @Query("DELETE from folder_table WHERE folderId= :folderId")
    fun deleteFolderData(folderId: String)

}