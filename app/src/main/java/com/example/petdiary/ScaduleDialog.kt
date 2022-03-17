package com.example.petdiary

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.EditText
import androidx.core.content.contentValuesOf


class ScaduleDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener



    fun start(){
        dialog.setContentView(R.layout.scadule_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.show()

        var scaduleEV : EditText = dialog.findViewById(R.id.scadule)
        var scadule : String = scaduleEV.text.toString()

        var cancelBtn : Button = dialog.findViewById(R.id.cancel)
        var okBtn : Button = dialog.findViewById(R.id.ok)

        okBtn.setOnClickListener {
            onClickListener.onOKClicked(scadule)
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }


    }

    interface DialogOKCLickListener{
        fun onOKClicked(scadule : String)
    }
}