package com.hyejin.petdiary.scenarios.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyejin.petdiary.R
import com.hyejin.petdiary.extensions.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignActivity : AppCompatActivity() {

    // Firebase
    private lateinit var googleSignInClient : GoogleSignInClient
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val RC_SIGN_IN = 1313

    // View
    private val btn_google : SignInButton by lazy{ findViewById(R.id.btnGoogleLogin)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        // 처음 앱 실행
        googleLogin()

    }
    private fun googleLogin(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btn_google.setOnClickListener {
            googleSignIn()
        }
    }

    // 구글 회원가입
    private fun googleSignIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun toInputDataActivity(user: FirebaseUser?){
        if(user !=null){
            startActivity(Intent(this, InputDataFirstActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //구글 회원가입 Methods
        if(requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            }
            catch (e : ApiException){
                showToast("구글 회원가입에 실패하였습니다: ${e.message}")
            }
        }
        else{
        }
    }

    private fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) {
                task ->
            if (task.isSuccessful){

                toInputDataActivity(auth.currentUser)
            }
        }
    }

}
