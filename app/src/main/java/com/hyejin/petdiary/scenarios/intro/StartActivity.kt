package com.hyejin.petdiary.scenarios.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hyejin.petdiary.scenarios.main.MainActivity
import com.hyejin.petdiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collect

class StartActivity : AppCompatActivity() {

    // Firebase
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val viewModel by viewModels<StartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        viewModel.tryLogin(this)

        lifecycleScope.launchWhenCreated {
            viewModel.loginResult.collect{ isLogin ->
                if(isLogin) {
                    toMainActivity(auth.currentUser)
                }else{
                    startActivity(Intent(this@StartActivity, SignActivity::class.java))
            }}
        }
    }
    private fun toMainActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }
}