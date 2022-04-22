package com.hyejin.petdiary.scenarios.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StartViewModel : ViewModel(){
    private val _loginResult = MutableSharedFlow<Boolean>()
    var loginResult = _loginResult.asSharedFlow()

    fun tryLogin(context: Context){
        viewModelScope.launch {
            var account = async {
                getLastSignedInAccount(context)
            }
            delay(2500)

            setLoginResult(account.await() !=null)
        }

    }
    private fun getLastSignedInAccount(context: Context) = GoogleSignIn.getLastSignedInAccount(context)

    private fun setLoginResult(isLogin : Boolean){
        viewModelScope.launch {
            _loginResult.emit(isLogin)
        }
    }
}