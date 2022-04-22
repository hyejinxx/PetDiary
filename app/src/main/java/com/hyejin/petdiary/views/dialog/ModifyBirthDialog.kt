package com.hyejin.petdiary.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.hyejin.petdiary.R
import com.hyejin.petdiary.data.Birth

class ModifyBirthDialog(context: Context) {

    //Dialog
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    var birth = Birth()

    private lateinit var months : ArrayList<String>
    private lateinit var days : ArrayList<String>

    lateinit var spinnerMonth : Spinner
    lateinit var spinnerDay : Spinner
    lateinit var okBtn : Button
    lateinit var cancelBtn : Button

    fun start() {
        setDialog()
        initView()
        initSpinnerListener()
        initClickListener()
    }
    private fun setDialog() {
        dialog.setContentView(R.layout.dialog_modify_birth)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun initView(){
        spinnerMonth  = dialog.findViewById(R.id.modifySpinnerMonth)
        spinnerDay =  dialog.findViewById(R.id.modifySpinnerDay)
        okBtn = dialog.findViewById(R.id.okModify)
        cancelBtn = dialog.findViewById(R.id.cancelModify)
    }
    private fun generateMonths() = run {
        val months = ArrayList<String>()
        repeat(12) {
            months += String.format("%s월", it + 1)
        }
        months
    }

    private fun generateDays() = run {
        val days = ArrayList<String>()
        repeat(31) {
            days += String.format("%s일", it + 1)
        }
        days
    }
    private fun initSpinnerListener() {
        months = generateMonths()
        days = generateDays()

        spinnerMonth.adapter = ArrayAdapter(dialog.context, android.R.layout.simple_spinner_item, months)
        spinnerDay.adapter = ArrayAdapter(dialog.context, android.R.layout.simple_spinner_item, days)

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                birth.month = spinnerMonth.selectedItem.toString()
            }
        }
        spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                birth.day = spinnerDay.selectedItem.toString()
            }
        }
    }
    private fun initClickListener(){
        okBtn.setOnClickListener {
            onClickListener.onOKClicked(birth)
            dialog.dismiss()
        }
       cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface DialogOKCLickListener {
        fun onOKClicked(birth: Birth)
    }
    fun setOnClickListener(listener: DialogOKCLickListener){
        onClickListener = listener
    }
}