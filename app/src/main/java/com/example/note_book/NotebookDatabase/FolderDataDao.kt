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

    @Query("SELECT folderId from folder_table ORDER BY Created_Time ASC")
    fun getDefaultFolderId(): String


    @Insert
    fun insertUserDefinedFolder(folderData: FolderData)

    @Update
    fun updateFolderName(folderData: FolderData)

    @Query("SELECT * from folder_table ORDER BY Created_Time ASC")
    fun getAllDataByFolder(): LiveData<List<FolderData>>
}