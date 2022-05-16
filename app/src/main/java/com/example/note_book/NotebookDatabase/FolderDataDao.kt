package com.example.note_book.NotebookDatabase


import android.icu.text.CaseMap
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

//   --------------------------------------------------------------------------
//    Sort FOLDER
//   --------------------------------------------------------------------------
    @Query("SELECT * FROM folder_table ORDER BY Folder_Name ASC")
    fun getFolderTitleSortASC(): LiveData<List<FolderData>>

    @Query("SELECT  * FROM folder_table ORDER BY Folder_Name DESC")
    fun getFolderTitleSortDESC(): LiveData<List<FolderData>>

    @Query("SELECT * FROM folder_table ORDER BY Created_Time ASC")
    fun getFolderCreatedTimeSortASC(): LiveData<List<FolderData>>

    @Query("SELECT * FROM FOLDER_TABLE ORDER BY Created_Time DESC")
    fun getFolderCreatedTimeSortDESC(): LiveData<List<FolderData>>

    @Query("SELECT * FROM folder_table ORDER BY Modified_Time ASC")
    fun getFolderModifiedTimeSortASC(): LiveData<List<FolderData>>

    @Query("SELECT * FROM folder_table ORDER By Modified_Time DESC")
    fun getFolderModifiedTimeSortDESC(): LiveData<List<FolderData>>

//   --------------------------------------------------------------------------

}
