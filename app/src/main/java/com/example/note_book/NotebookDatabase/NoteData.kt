package com.example.note_book.NotebookDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "Note_Table")
data class NoteData(

    @PrimaryKey
    var noteId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "FolderId")
    var folderId: String,

    @ColumnInfo(name = "NoteTitle")
    var noteTitle: String,

    @ColumnInfo(name = "NoteDescription")
    var noteDescription: String,

    @ColumnInfo(name = "CreatedTime")
    var createdTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name="ModifiedTime")
    var modifiedTime: Long,

    @ColumnInfo(name="Selected")
    var selected: Boolean = false,

    @ColumnInfo(name = "Favourite")
    var favourite: Boolean = false

):Serializable