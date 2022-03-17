package com.example.petdiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.ActionBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Start : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        auth = FirebaseAuth.getInstance()
        val pageMove = Intent(this, Sign::class.java)



        Handler(Looper.getMainLooper()).postDelayed({
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if(account!==null){ // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
                toMainActivity(auth.currentUser)
            }
            else{startActivity(pageMove)}
        }, 2500L)



    }
    fun toMainActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, Main::class.java))
            this.finish()
        }
    }
}