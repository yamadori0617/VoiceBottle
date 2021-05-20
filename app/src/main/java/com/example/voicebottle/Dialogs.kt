package com.example.voicebottle

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.InputFilter
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar

class ConfirmDialog(private val message: String,
                    private val okLabel: String,
                    private val okSelected: () -> Unit,
                    private val cancelLabel: String,
                    private val cancelSelected: () -> Unit)
    : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(message)
        builder.setPositiveButton(okLabel) { dialog, which ->
            okSelected()
        }
        builder.setNegativeButton(cancelLabel) { dialog, which ->
            cancelSelected()
        }
        return builder.create()
    }
}

class EditTextDialog(private val message: String,
                     private val okLabel: String,
                     private val okSelected: (String) -> Unit)
    : DialogFragment() {

    override fun onCreateDialog (savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val input = EditText(requireActivity()).apply {
            filters = arrayOf(InputFilter.LengthFilter(10))
        }
        builder.setMessage(message)
        builder.setView(input)
        builder.setPositiveButton(okLabel) { dialog, which ->
            val userText = input.text.toString()
            okSelected(userText)
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}