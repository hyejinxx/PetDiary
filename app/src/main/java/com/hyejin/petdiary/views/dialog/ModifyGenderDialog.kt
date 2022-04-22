package com.hyejin.petdiary.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.hyejin.petdiary.R

class ModifyGenderDialog (context: Context){

    //Dialog
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    var gender : String = ""

    private lateinit var spinnerGender : Spinner
    lateinit var okBtn : Button
    lateinit var cancelBtn : Button

    fun start() {
        setDialog()
        initView()
        initSpinnerListener()
        initClickListener()
    }
    private fun setDialog() {
        dialog.setContentView(R.layout.dialog_modify_gender)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun initView(){
        spinnerGender  = dialog.findViewById(R.id.modifySpinnerGender)
        okBtn = dialog.findViewById(R.id.okModify)
        cancelBtn = dialog.findViewById(R.id.cancelModify)
    }
    private fun initSpinnerListener() {

        spinnerGender.adapter = ArrayAdapter.createFromResource(
            dialog.context,
            R.array.genderArray,
            android.R.layout.simple_spinner_item
        )

        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = spinnerGender.selectedItem.toString()
            }
        }
    }
    private fun initClickListener(){
        okBtn.setOnClickListener {
            onClickListener.onOKClicked(gender)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface DialogOKCLickListener {
        fun onOKClicked(gender : String)
    }
    fun setOnClickListener(listener: DialogOKCLickListener){
        onClickListener = listener
    }
}
