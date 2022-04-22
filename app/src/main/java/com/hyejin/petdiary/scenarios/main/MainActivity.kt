package com.hyejin.petdiary.scenarios.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.hyejin.petdiary.R
import com.hyejin.petdiary.scenarios.main.diaryPost.DiaryPostFragment
import com.hyejin.petdiary.scenarios.main.petPage.PetPageFragment
import com.hyejin.petdiary.scenarios.main.schedulePost.SchedulePostFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setToolBar()
        setFragment(DiaryPostFragment())
    }

    private fun setToolBar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        //actionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scaduale_management -> {
                setFragment(SchedulePostFragment())
            }
            R.id.diary -> {
                setFragment(DiaryPostFragment())
            }
            R.id.pet_page -> {
                setFragment(PetPageFragment())
            }
        }
        return super.onOptionsItemSelected(item)

    }
    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainfragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}


