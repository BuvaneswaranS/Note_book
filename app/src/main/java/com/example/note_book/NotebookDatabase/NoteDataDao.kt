package com.example.note_book.NotebookDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.concurrent.Flow

@Dao
interface NoteDataDao {

    @Insert
    fun insertNoteData(noteData: NoteData)

    @Update
    fun updateNoteData(noteData: NoteData)

    @Update
    fun updateFavouriteNoteData(noteData: NoteData)

    @Update
    fun updateUnFavouriteNoteData(noteData: NoteData)

    @Query("SELECT * FROM note_table")
    fun getAllNotesCard(): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table")
    fun getAllNotesSearchCard(): kotlinx.coroutines.flow.Flow<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE NoteTitle LIKE :search_Text OR NoteDescription LIKE :search_Text ")
    fun getSearchNotesCard(search_Text: String): kotlinx.coroutines.flow.Flow<List<NoteData>>


    @Query("SELECT noteId FROM note_table")
    fun getAllNoteId(): MutableList<String>

    @Query("SELECT * from note_table WHERE FolderId = :folderId")
    fun getFolderContent(folderId: String): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE favourite = :isFavourite")
    fun getFavouriteNotes(isFavourite: Boolean): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE noteId = :noteId")
    fun getNoteData(noteId: String): NoteData

    @Query("DELETE  FROM note_table WHERE noteId = :noteId")
    fun deleteNoteData(noteId: String)

    @Query("SELECT noteId FROM note_table WHERE FolderId = :folderId")
    fun getFolderNoteData(folderId: String): MutableList<String>

    @Query("DELETE FROM note_table WHERE FolderId= :folderId")
    fun delete_Notedata_Folder_id(folderId: String)
//  -----------------------------------------------------------------------
//  All Note Card
//  ----------------------------------------------------------------
    @Query("SELECT * FROM note_table ORDER BY NoteTitle ASC")
    fun getTitleSortAscending(): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table ORDER BY NoteTitle DESC")
    fun getTitleSortDescending(): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table ORDER BY CreatedTime ASC")
    fun getCreatedTimeSortOld(): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table ORDER BY CreatedTime DESC")
    fun getCreatedTimeSortNew(): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table ORDER BY ModifiedTime ASC")
    fun getModifiedTimeSortOld(): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table ORDER BY ModifiedTime DESC")
    fun getModifiedTimeSortNew(): LiveData<List<NoteData>>
//  -----------------------------------------------------------------------

//   -----------------------------------------------------------------------
//   Notes based on the Folder Id
//   -----------------------------------------------------------------------
    @Query("SELECT * FROM note_table WHERE FolderId = :folderId  ORDER BY NoteTitle ASC")
    fun getFolderBasedNoteTitleSortAscending(folderId: String): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE FolderId = :folderId ORDER BY NoteTitle DESC")
    fun getFolderBasedNoteTitleSortDescending(folderId: String): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE FolderId = :folderId ORDER BY CreatedTime ASC")
    fun getFolderBasedNoteCreatedTimeSortOld(folderId: String): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE FolderId = :folderId ORDER BY CreatedTime DESC")
    fun getFolderBasedNoteCreatedTimeSortNew(folderId: String): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE FolderId = :folderId ORDER BY ModifiedTime ASC")
    fun getFolderBasedNoteModifiedTimeSortOld(folderId: String): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE FolderId = :folderId ORDER BY ModifiedTime DESC")
    fun getFolderBasedNoteModifiedTimeSortNew(folderId: String): LiveData<List<NoteData>>

//   ---------- --------------------------------------------------------------

//   -----------------------------------------------------------------------
//   Favourite
//   -----------------------------------------------------------------------
    @Query("SELECT * FROM note_table WHERE Favourite = :fav ORDER BY NoteTitle ASC")
    fun getFavTitleSortAscending(fav: Boolean): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE Favourite = :fav ORDER BY NoteTitle DESC")
    fun getFavTitleSortDescending(fav: Boolean): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE Favourite = :fav ORDER BY CreatedTime ASC")
    fun getFavCreatedTimeSortOld(fav: Boolean): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE Favourite = :fav ORDER BY CreatedTime DESC")
    fun getFavCreatedTimeSortNew(fav: Boolean): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE Favourite = :fav ORDER BY ModifiedTime ASC")
    fun getFavModifiedTimeSortOld(fav: Boolean): LiveData<List<NoteData>>

    @Query("SELECT * FROM note_table WHERE Favourite = :fav ORDER BY ModifiedTime DESC")
    fun getFavModifiedTimeSortNew(fav: Boolean): LiveData<List<NoteData>>

//   ---------- --------------------------------------------------------------
}