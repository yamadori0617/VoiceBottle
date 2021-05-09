package com.example.voicebottle

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

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
                     private val hint: String,
                     private val okLabel: String,
                     private val okSelected: () -> Unit,
                     )
    : DialogFragment() {

    override fun onCreateDialog (savedInstanceState: Bundle?): Dialog {
        val input: EditText = EditText(requireActivity())
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(message)
        builder.setView(input)
        builder.setPositiveButton(okLabel) { dialog, which ->
            okSelected()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}