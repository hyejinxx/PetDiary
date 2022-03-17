package com.example.petdiary

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.petdiary.Fragment.DiaryPost
import com.example.petdiary.Fragment.PetPage
import com.example.petdiary.Fragment.ScadulePost
import com.example.petdiary.data.ImageURI
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class Main : AppCompatActivity() {

    lateinit var showCalender: ImageButton
    lateinit var dateTextView: TextView
    lateinit var date: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setFragment(ScadulePost())
        var TodayDate = Date(System.currentTimeMillis())
        date = arrayListOf(TodayDate.year, TodayDate.month, TodayDate.day)

        var dateText = "${date[0]}년 ${date[1]}월 ${date[2]}일"
        dateTextView= findViewById(R.id.dateTv)
        dateTextView.setText(dateText)


        showCalender = findViewById(R.id.calender_btn)

        showCalender.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth
                    ->
                    dateText = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                    dateTextView.setText(dateText)
                    date[0] = year
                    date[1] = month
                    date[2] = dayOfMonth
                }
            DatePickerDialog(
                this, R.style.DatePickerStyle,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showCalender.visibility = View.VISIBLE
        dateTextView.visibility = View.VISIBLE
        when (item.itemId) {
            R.id.scaduale_management -> {
                setDataFragment(ScadulePost(), date)
            }
            R.id.diary -> {
                setDataFragment(DiaryPost(), date)
            }
            R.id.pet_page -> {
                dateTextView.visibility = View.GONE
                showCalender.visibility = View.GONE
                setFragment(PetPage())
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun setDataFragment(fragment: Fragment, date: ArrayList<Int>){
        var bundle = Bundle()
        bundle.putIntegerArrayList("date", date)
        fragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainfragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainfragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}

