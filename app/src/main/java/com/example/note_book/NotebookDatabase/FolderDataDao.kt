package com.example.note_book.NotebookDatabase


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    @Query("SELECT * from folder_table ORDER BY Created_Time ASC")
    fun getAllDataByFolder(): LiveData<List<FolderData>>

    @Query("SELECT * from folder_table WHERE folderId = :folderId")
    fun getFolderData(folderId: String): FolderData

}