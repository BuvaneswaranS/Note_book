package com.example.note_book.NotebookDatabase

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface NoteDataDao {

    @Insert
    fun insertNoteData(noteData: NoteData)
}