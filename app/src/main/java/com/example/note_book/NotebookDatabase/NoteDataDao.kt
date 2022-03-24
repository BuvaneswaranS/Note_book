package com.example.note_book.NotebookDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDataDao {

    @Insert
    fun insertNoteData(noteData: NoteData)

//    @Query("SELECT * FROM note_table")
//    fun getFolderContent(): LiveData<List<NoteData>>

    @Query("SELECT * from note_table WHERE FolderId = :folderId")
    fun getFolderContent(folderId: String): LiveData<List<NoteData>>
}