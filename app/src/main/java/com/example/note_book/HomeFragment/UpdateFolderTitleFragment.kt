package com.example.note_book.HomeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.note_book.NotebookDatabase.FolderData
import com.example.note_book.R

class UpdateFolderTitleFragment(viewModel: HomeFragmentViewModel, folderData: FolderData): DialogFragment() {

    val viewModel: HomeFragmentViewModel = viewModel

    var folderData: FolderData = folderData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var rootView: View = inflater.inflate(R.layout.item_layout_update_folder_title, container, false)

        var inputData = rootView.findViewById<EditText>(R.id.update_folder_name_input)

        var cancelButton = rootView.findViewById<Button>(R.id.change_folder_title_cancelButton)
        var okButton = rootView.findViewById<Button>(R.id.change_folder_title_ok_button)

        inputData.setText(folderData.folderName)

        cancelButton.setOnClickListener {
            dismiss()
        }

        okButton.setOnClickListener {
            Log.i("TestingApp","Input text --> ${inputData.text.toString()}")
            val data= FolderData(folderId = folderData.folderId,folderName = inputData.text.toString(), createdTime = folderData.createdTime, modifiedTime = System.currentTimeMillis())

          Log.i("Testing1App","FolderNAME -> ${data.folderName}")

            viewModel.updateFolderName(data)
            dismiss()
        }
        return rootView
    }
}