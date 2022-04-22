package com.hyejin.petdiary.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.hyejin.petdiary.R
import com.hyejin.petdiary.data.Birth
import com.hyejin.petdiary.data.PetData

class Datadialog(context: Context) {

    //Dialog
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DaialogSaveClickListener

    private val cancelBtn : Button by lazy { dialog.findViewById(R.id.cancelBtn) }
    private val saveBtn : Button by lazy { dialog.findViewById(R.id.saveBtn) }
    private val nameTextView: TextView by lazy { dialog.findViewById(R.id.dialogDataName) }
    private val ageTextView: TextView by lazy { dialog.findViewById(R.id.dialogDataAge) }
    private val genderTextView: TextView by lazy { dialog.findViewById(R.id.dialogDataGender) }
    private val birthTextView: TextView by lazy { dialog.findViewById(R.id.dialogDataBirth) }
    private val weightTextView: TextView by lazy { dialog.findViewById(R.id.dialogDataWeight) }

    // PetData
    lateinit var petData : PetData
    lateinit var birthText : String

    fun startSaveDialog( name : String, age : String, gender: String, birth: Birth, weight : String) {
        petData = PetData(name, age, gender, birth, weight)
        birthText = "${petData.birth.month} ${petData.birth.day}"
        setDialog()
        initView()
        initClickListener()
    }
    private fun initView(){
        nameTextView.text = petData.name
        ageTextView.text = petData.age
        genderTextView.text = petData.gender
        weightTextView.text = petData.weight
        birthTextView.text = birthText
    }
    private fun setDialog(){
        dialog.setContentView(R.layout.dialog_data)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun initClickListener(){
        saveBtn.setOnClickListener {
            onClickListener.onSaveClicked(petData)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface DaialogSaveClickListener{
        fun onSaveClicked(petData: PetData)
   }
    fun setOnClickListener(listener: DaialogSaveClickListener){
        onClickListener = listener
    }
}