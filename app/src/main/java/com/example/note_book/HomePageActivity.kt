package com.example.note_book

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.note_book.NotebookDatabase.FolderRepository
import com.example.note_book.NotebookDatabase.NotebookDatabase
import com.example.note_book.databinding.PageHomeBinding

// interface for Drawer Controller
interface DrawerControler{

    fun drawerLocked()

    fun drawerUnLocked()

    fun drawerClosed()

}

class HomePageActivity : AppCompatActivity(), DrawerControler {

//   Viewbinding Declaration
    private lateinit var binding: PageHomeBinding

//  Shared Preferences Name To Check Selection is turned ON (or) OFF
    private val select = "sharedSelect"

//    ViewModel declaration
    private lateinit var viewModel: HomePageViewModel

//  AppBar Configuration declaration
    private lateinit var appBarConfiguration: AppBarConfiguration

//  NavController is declared and initialized to null
    private var navController: NavController ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      ViewBinding Initialization
        binding = PageHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

//      Declaration and Initialization of navHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

//      Initialization of NavController
        navController = navHostFragment.navController

//      Initialization of AppBarConfiguration
        appBarConfiguration = AppBarConfiguration(navController!!.graph, binding.drawerLayoutHome)

//      Toolbar has been setted as the supportActionBar
        setSupportActionBar(binding.toolbar)

//      Function for setting up the Drawer Layout
        setupDrawerLayout()

//      Declaration and Initialization of ViewModelFactory
        val viewModelFactory = HomePageViewModelFactory(this.application)

//      Initialization of the ViewModel
        viewModel = ViewModelProvider(this,viewModelFactory).get(HomePageViewModel::class.java)

//      Application Class Declaration
        val applicationClass = ApplicationInitialize()

//      If statement to check whether the DEFAULT FOLDER is already created (or) not
        if(applicationClass.insertDefaultFolder){

//          If the default folder is not created, the below function in the viewModel is called to insert the default folder to the database.
            viewModel.updateDefaultFolder()

        }
    }


//  onOptionsItemSelected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

//      Declaration and Initialization of the NavController
        val navController = findNavController(R.id.nav_host_fragment_container)

        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

    }

//  onSupportNavigateUp is called whenever the back button is clicked
    override fun onSupportNavigateUp(): Boolean {

//      Check Whether the selection is enabled (or) not on any Fragment
        val sharedPreferencesSelection = getSharedPreferences(select, Context.MODE_PRIVATE)

//      Getting the value of the sharedPreference
        val shared = sharedPreferencesSelection.getBoolean("SELECTED",false)

//      Declaration of Shared Preference for DRAWER
        val sharedValue = "drawer_shared"

        val sharedPreferencesDrawerSelection = getSharedPreferences(sharedValue, Context.MODE_PRIVATE)

        val drawerShared = sharedPreferencesDrawerSelection.getBoolean("IN",false)

//      If the User is any of the fragment declared in the navigation drawer
//      If statement to check whether the user is is on the declared fragment in the navigaton drawer.
        if (drawerShared){

//          In one of the fragment declared in the navigation drawer, if-else statement to check whether the selection is enabled in any of the fragments.
//          If the selection is enabled, selection is made to false
            if (shared) {

                val editor: SharedPreferences.Editor = sharedPreferencesSelection.edit()

                editor.putBoolean("SELECTED",false)

                editor.apply()

                editor.commit()

                return false

            }
//          if the selection is not enabled in one of the fragment declared in the navigation drawer, navigation drawer will open.
            else {
                binding.drawerLayoutHome.openDrawer(Gravity.LEFT)
                return false
            }
        }

//      If the selection is enabled, selection is made to false
        if (shared) {

            val editor: SharedPreferences.Editor = sharedPreferencesSelection.edit()

            editor.putBoolean("SELECTED",false)

            editor.apply()

            editor.commit()

            return false

        }

//      If the selection is not enabled, navigateUp will be executed
        else{
            return NavigationUI.navigateUp(navController!!, binding.drawerLayoutHome)
        }

    }

    private fun setupDrawerLayout() {

//      NavigationView is setup with the NavController
        binding.navMenuHome.setupWithNavController(navController!!)

//      ActionBar is setted up with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController!!, binding.drawerLayoutHome)

    }

//  Function is called when you want to Lock the drawer and drawer should not be opened
    override fun drawerLocked() {
        binding.drawerLayoutHome.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

//  Function is called when you want to the unlock the drawer and drawer can be accessed
    override fun drawerUnLocked() {
        binding.drawerLayoutHome.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

//  Function is called when you want to the close the drawer
    override fun drawerClosed() {
        binding.drawerLayoutHome.closeDrawer(Gravity.LEFT)
    }
}