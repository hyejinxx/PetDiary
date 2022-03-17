package com.example.petdiary

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.petdiary.data.PetData
import com.example.petdiary.data.WeightData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class InputData_first : AppCompatActivity() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

    lateinit var name: String
    lateinit var gender : String
    lateinit var births : Array<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_data_first)

        var age : Int
        var weight : Double

        //반려동물 이름, 나이, 체중
        val inputName: EditText = findViewById(R.id.inputName)
        val inputAge: EditText = findViewById(R.id.inputAge)
        val inputWeight: EditText = findViewById(R.id.inputWeight)

        name = inputName.text.toString()

        try {
            age = Integer.parseInt(inputAge.text.toString())
        } catch (e: NumberFormatException) {
            age = 0
        }
        try {
            weight = inputWeight.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            weight = 0.0
        }

        //반려동물 성별

        val spinner_Gender = findViewById<Spinner>(R.id.spinner_Gender)

        spinner_Gender.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.genderArray,
            android.R.layout.simple_spinner_item
        )

        spinner_Gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = spinner_Gender.selectedItem.toString()
                }
            }

        //*************************************************************

        //반려동물 생일

        val spinnerBirthMonths : Spinner = findViewById(R.id.spinner_month)
        val spinnerBirthDays : Spinner = findViewById(R.id.spinner_day)


        // 날짜 배열 생성
        val months :Array<String> = Array(12, {""})
        for (i in 1..12){
            months[i-1] = i.toString() + "월"
        }
        val days: Array<String> = Array(31, { "" })
        for (i in 1..31) {
            days[i - 1] = i.toString() +"일"
        }

        births = arrayOfNulls<String>(2)

        spinnerBirthMonths.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        spinnerBirthDays.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)

        spinnerBirthMonths.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                births[0] = spinnerBirthMonths.selectedItem.toString()
            }
        }
        spinnerBirthDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                births[1] = spinnerBirthDays.selectedItem.toString()
            }
        }


        var user = auth.currentUser?.uid
        var plusButton: Button = findViewById(R.id.PlusButton)
        plusButton.setOnClickListener {
            showDialog(user, name, age, weight, gender, births)

        }
    }

        fun showDialog(uId: String?, name: String, age: Int, weight: Double, gender: String, births: Array<String?>) {
            var birthtext = "${births[0]} "+"${births[1]}"
            AlertDialog.Builder(this)
                .setTitle("정보확인")
                .setMessage("이름 : $name\n" + "성 : $gender\n"+"나이 : $age\n" +"생일 : ${birthtext}\n"+ "체중 : $weight")
                .setPositiveButton("확인", { dialogInterface: DialogInterface, i: Int ->
                    val pet = PetData(uId, name, age, births,weight, gender)
                    AddPet(pet)
                })
                .setNegativeButton("취소", { dialogInterface: DialogInterface, i: Int ->
                })
                .show()
        }

        fun AddPet(pet : PetData) {
            val now = System.currentTimeMillis()
            val dateNow = Date(now)
            //val currentWeight = WeightData(dateNow, pet.weight)

            if (pet.uId != null) {
                db.collection("UsersData").document(pet.uId!!).collection("aboutPet")
                    .document("firstData").set(pet)
                    .addOnSuccessListener {
                        Toast.makeText(
                            getApplicationContext(),
                            "저장완료",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            getApplicationContext(),
                            "저장실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            val toMain = Intent(this, Start::class.java)
            startActivity(toMain)
        }

    }
