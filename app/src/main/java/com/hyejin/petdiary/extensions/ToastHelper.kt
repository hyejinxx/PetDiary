package com.hyejin.petdiary.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

//fun showToast(context: Context, message: String) {
//    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//}
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun Fragment.showToast(message: String){
    requireActivity().showToast(message)
}