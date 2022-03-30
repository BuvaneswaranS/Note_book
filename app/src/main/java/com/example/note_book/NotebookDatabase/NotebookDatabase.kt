package com.example.note_book.NotebookDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FolderData::class,NoteData::class], version = 1, exportSchema = false)
abstract class NotebookDatabase: RoomDatabase() {

    abstract val folderDataDao: FolderDataDao
    abstract val noteDataDao: NoteDataDao


    companion object{

        @Volatile
        private var DATABASE_INSTANCE: NotebookDatabase? = null

        fun  getDatabaseInstance(context: Context): NotebookDatabase{
            synchronized(this){
                var instance = DATABASE_INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext, NotebookDatabase::class.java, "notes_database").fallbackToDestructiveMigration().build()
                    DATABASE_INSTANCE = instance
                }
                return instance
            }
        }
    }
}