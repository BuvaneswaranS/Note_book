package com.example.note_book.NotebookDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "folder_table")
data class FolderData(

    @PrimaryKey
    var folderId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "Folder Name")
    var folderName: String,

    @ColumnInfo(name = "Created_Time")
    var createdTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "Modified_Time")
    var modifiedTime: Long = 0L
)