package com.example.note_book.NotebookDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDataDao {

    @Insert
    fun insertNoteData(noteData: NoteData)

    @Update
    fun updateNoteData(noteData: NoteData)

//    @Query("SELECT * FROM note_table")
//    fun getFolderContent(): LiveData<List<NoteData>>

    @Query("SELECT * from note_table WHERE FolderId = :folderId")
    fun getFolderContent(folderId: String): LiveData<List<NoteData>>


    @Query("SELECT * FROM note_table WHERE noteId = :noteId")
    fun getNoteData(noteId: String): NoteData

    @Query("DELETE  FROM note_table WHERE noteId = :noteId")
    fun deleteNoteData(noteId: String)

    @Query("SELECT noteId FROM note_table WHERE FolderId = :folderId")
    fun getFolderNoteData(folderId: String): MutableList<String>

    @Query("DELETE FROM note_table WHERE FolderId= :folderId")
    fun delete_Notedata_Folder_id(folderId: String)

}