package com.example.note_book

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class ApplicationInitialize: Application() {

//  Declaration and Initialization of SharedPreference Name
    private val sharedPref = "sharedPref"

//  Variable to check whether the default folder is inserted (or) not  
    var insertDefaultFolder: Boolean = false

//  companion object  
    companion object{
        lateinit var applicationInitialize: ApplicationInitialize
    }
    
//  onCreate  
    override fun onCreate() {
        super.onCreate()
        applicationInitialize = this
        
//     Getting a Boolean from shared Preference to check whether the app is installed already (or) not
        val sharedPreferenceInsertDefaultFolder = getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        val shared = sharedPreferenceInsertDefaultFolder.getBoolean("STARTED",false)

//      if statement to check whether default folder is inserted (or) not
        if (!shared){

            val editor: SharedPreferences.Editor = sharedPreferenceInsertDefaultFolder.edit()
            editor.putBoolean("STARTED",true)
            editor.apply()
            editor.commit()

//          if the app is installed for the first time, insertDefaultFolder is set to true
            insertDefaultFolder = true

        }
    }

}