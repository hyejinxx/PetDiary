package com.hyejin.petdiary.scenarios.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.hyejin.petdiary.R
import com.hyejin.petdiary.data.Birth
import com.hyejin.petdiary.data.PetData
import com.hyejin.petdiary.databinding.ActivityInputDataFirstBinding
import com.hyejin.petdiary.extensions.showToast
import com.hyejin.petdiary.views.dialog.Datadialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class InputDataFirstActivity : AppCompatActivity() {
    // ViewModel
    val viewModel by viewModels<InputDataFirstViewModel>()
    val binding by lazy { DataBindingUtil.setContentView<ActivityInputDataFirstBinding>(this, R.layout.activity_input_data_first) }

    // Firebase
    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val user = auth.currentUser?.uid

    // Input Data
    var gender = ""
    var birth = Birth("0월", "0일")

    // Date Data
    private lateinit var months : ArrayList<String>
    private lateinit var days : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        initData()
        initUI()
        initListener()
    }
    private fun initData(){
        months = viewModel.generateMonths()
        days = viewModel.generateDays()
    }

    private fun initUI() {
        binding.spinnerGender.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.genderArray,
            android.R.layout.simple_spinner_item
        )
        binding.spinnerMonth.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        binding.spinnerDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
    }

    private fun initListener() {
        initSpinnerListener()
        initClickListener()
    }

    private fun initClickListener() {
        binding.PlusButton.setOnClickListener {
            if(viewModel.inputName.value==""){
                showToast("이름을 입력해주세요")
            }else{
                showDialog(
                    viewModel.inputName.value,
                    viewModel.age.value,
                    viewModel.weight.value,
                    gender,
                    birth
                )
            }
        }
    }

    private fun initSpinnerListener() {
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = binding.spinnerGender.selectedItem.toString()
            }
        }

        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                birth.month = binding.spinnerMonth.selectedItem.toString()
            }
        }

        binding.spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                birth.day = binding.spinnerDay.selectedItem.toString()
            }
        }
    }

    private fun showDialog(
        name: String,
        age: Int,
        weight: Double,
        gender: String,
        birth: Birth
    ) {
        val dialog = Datadialog(this)
        dialog.startSaveDialog(name, age.toString(), gender, birth, weight.toString())
        dialog.setOnClickListener(object : Datadialog.DaialogSaveClickListener{
            override fun onSaveClicked(petData: PetData) {
                addPet(petData)
            }
        })
    }

    private fun addPet(pet: PetData) {

        db.collection("UsersData")
            .document(user!!).collection("aboutPet").document("Data").set(pet)
            .addOnSuccessListener {
                showToast("저장 성공")
                startActivity(Intent(this, StartActivity::class.java))
            }
            .addOnFailureListener {
                showToast("저장 실패: ${it.message}")
            }
    }


}