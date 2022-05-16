package com.example.note_book.Sort

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.note_book.R
import com.example.note_book.displayFolderData.DisplayFolderContentViewModel
import com.example.note_book.displayFolderData.displayFolderContentViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheet(): BottomSheetDialogFragment() {

    private val sharedPref = "sharedPref"

    lateinit var viewModel: SortViewModel

    var state: String = "unChanged"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.item_layout_sort_fragment,container,false)

        val sharedPreferenceInsertDefaultFolder =
            activity?.getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        val shared = sharedPreferenceInsertDefaultFolder?.getString("SORT_OPTION","A1")

//     Declaration and Initialization of ViewModel Factory
        val viewModelFactory = SortViewModelFactory()

//      viewModel is initialized
        viewModel = ViewModelProvider(this, viewModelFactory).get(SortViewModel::class.java)

        val A1: ConstraintLayout = view.findViewById(R.id.A1)
        val A2: ConstraintLayout = view.findViewById(R.id.A2)
        val A3: ConstraintLayout = view.findViewById(R.id.A3)
        val A4: ConstraintLayout = view.findViewById(R.id.A4)
        val A5: ConstraintLayout = view.findViewById(R.id.A5)
        val A6: ConstraintLayout = view.findViewById(R.id.A6)

        val A1tick: ImageView = view.findViewById(R.id.A1_tick_button)
        val A2tick: ImageView = view.findViewById(R.id.A2_tick_button)
        val A3tick: ImageView = view.findViewById(R.id.A3_tick_button)
        val A4tick: ImageView = view.findViewById(R.id.A4_tick_button)
        val A5tick: ImageView = view.findViewById(R.id.A5_tick_button)
        val A6tick: ImageView = view.findViewById(R.id.A6_tick_button)

        val sortButton  = view.findViewById<TextView>(R.id.sort_close_button)


        viewModel.sortOrderTemp.value = shared

        viewModel.sortOrderTemp.observe(this.viewLifecycleOwner, Observer {sortOrder ->
            A1tick.visibility = View.INVISIBLE
            A2tick.visibility = View.INVISIBLE
            A3tick.visibility = View.INVISIBLE
            A4tick.visibility = View.INVISIBLE
            A5tick.visibility = View.INVISIBLE
            A6tick.visibility = View.INVISIBLE

            if (sortOrder == "A1"){
                A1tick.visibility = View.VISIBLE

            }else if (sortOrder == "A2"){
                A2tick.visibility = View.VISIBLE

            }else if (sortOrder == "A3"){

                A3tick.visibility = View.VISIBLE

            }else if (sortOrder == "A4"){
                A4tick.visibility = View.VISIBLE
            }else if (sortOrder == "A5"){
                A5tick.visibility = View.VISIBLE
            }else if (sortOrder == "A6"){
                A6tick.visibility = View.VISIBLE
            }
        })

        val editor: SharedPreferences.Editor? = sharedPreferenceInsertDefaultFolder?.edit()



        A1.setOnClickListener{
            viewModel.sortOrderTemp.value = "A1"
            editor?.putString("SORT_OPTION", "A1")
            editor?.apply()
            editor?.commit()
        }

        A2.setOnClickListener{
            viewModel.sortOrderTemp.value = "A2"
            editor?.putString("SORT_OPTION", "A2")
            editor?.apply()
            editor?.commit()
        }

        A3.setOnClickListener{
            viewModel.sortOrderTemp.value = "A3"
            editor?.putString("SORT_OPTION", "A3")
            editor?.apply()
            editor?.commit()
        }

        A4.setOnClickListener{
            viewModel.sortOrderTemp.value = "A4"
            editor?.putString("SORT_OPTION", "A4")
            editor?.apply()
            editor?.commit()
        }

        A5.setOnClickListener{
            viewModel.sortOrderTemp.value = "A5"
            editor?.putString("SORT_OPTION", "A5")
            editor?.apply()
            editor?.commit()
        }

        A6.setOnClickListener{
            viewModel.sortOrderTemp.value = "A6"
            editor?.putString("SORT_OPTION", "A6")
            editor?.apply()
            editor?.commit()
        }

        sortButton?.setOnClickListener{
            viewModel.sortOrder.value = viewModel.sortOrderTemp.value
            state = "changed"
            editor?.putString("SORT_OPTION", viewModel.sortOrder.value)
            editor?.apply()
            editor?.commit()
            dismiss()
            setFragmentResult("requestKey", bundleOf("data" to viewModel.sortOrder.value))
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onStop() {
        viewModel.sortOrder.value = viewModel.sortOrderTemp.value
        Log.i("TestingApp","Sort Bottom Sheet onStop Called")

        super.onStop()
        val sharedPreferenceInsertDefaultFolder = activity?.getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreferenceInsertDefaultFolder?.edit()
        editor?.putString("SORT_OPTION", viewModel.sortOrder.value)
        editor?.apply()
        editor?.commit()
        state = "changed"
        setFragmentResult("requestKey", bundleOf("data" to viewModel.sortOrder.value))
    }



}