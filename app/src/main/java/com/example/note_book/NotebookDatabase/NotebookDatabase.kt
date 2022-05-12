package com.example.note_book.NotebookDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.concurrent.thread

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
                    instance = Room.databaseBuilder(context.applicationContext, NotebookDatabase::class.java, "notes_database").addCallback(
                        object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)


                                thread {

                                    getDatabaseInstance(context).folderDataDao.insertDefaultFolder(
                                        FolderData(folderName = "Default Folder", createdTime = System.currentTimeMillis(), selected_item = false)
                                    )
                                }
                            }
                        }).fallbackToDestructiveMigration().build()
                    DATABASE_INSTANCE = instance

                }
                return instance
            }
        }
    }
}