package com.hyejin.petdiary.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.hyejin.petdiary.R
import com.hyejin.petdiary.data.DateData


class ScheduleDialog(context:Context) {

    //Dialog
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    //View
    private lateinit var scheduleET: EditText
    private lateinit var schedule : String
    private lateinit var cancelBtn : Button
    private lateinit var okBtn : Button
    private lateinit var dateTv : TextView

    lateinit var date : String

    fun start(dateData: DateData) {
        setDialog()
        initView()
        initDate(dateData)
        initTextChangeListener()
        initClickListener()
    }

    private fun setDialog(){
        dialog.setContentView(R.layout.dialog_schedule)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun initClickListener(){
        okBtn.setOnClickListener {
            onClickListener.onOKClicked(schedule)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun initDate(dateData: DateData){
        date = String.format("%d년 %d월 %d일", dateData.year, dateData.month, dateData.day)
        dateTv.text = date
    }
    private fun initView() {
        scheduleET = dialog.findViewById(R.id.scheduleET)
        cancelBtn = dialog.findViewById(R.id.cancel)
        okBtn = dialog.findViewById(R.id.ok)
        dateTv = dialog.findViewById(R.id.scheduleDate)
    }
    private fun initTextChangeListener() {
        scheduleET.doAfterTextChanged {
            schedule = it.toString()
        }
    }
    interface DialogOKCLickListener{
        fun onOKClicked(schedule : String)
    }
    fun setOnClickListener(listener: DialogOKCLickListener){
        onClickListener = listener
    }
}
