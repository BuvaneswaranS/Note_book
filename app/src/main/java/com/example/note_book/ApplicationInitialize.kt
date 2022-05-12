package com.example.note_book

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get

class ApplicationInitialize: Application() {

    //  Declaration and Initialization of SharedPreference Name
    private val sharedPref = "sharedPref"


    //  onCreate
    override fun onCreate() {
        super.onCreate()


//     Getting a Boolean from shared Preference to check whether the app is installed already (or) not
        val sharedPreferenceInsertDefaultFolder =
            getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        val shared = sharedPreferenceInsertDefaultFolder.getBoolean("STARTED", false)

        if (!shared) {
//            Log.i("Tester","DefaultFolder --> ${shared}")
            val sharedPreferenceInsertDefaultFolder = getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferenceInsertDefaultFolder.edit()
            editor.putBoolean("STARTED", true)
            editor.apply()
            editor.commit()

    }

}
}