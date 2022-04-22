package com.hyejin.petdiary.scenarios.main.diaryPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyejin.petdiary.data.DateData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDate
import java.time.Month

class DiaryPostViewModel : ViewModel(){

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    private val _date = MutableStateFlow(
        LocalDate.now().let { current ->
            DateData(current.year, current.monthValue, current.dayOfMonth, current.month)
        }
    )
    var date = _date.asStateFlow()
    val dateString = date.map { date ->
        String.format("%s. %s. %s", date.year, date.monthText, date.day)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "-" )

    var diaryContent = MutableStateFlow("")

    private val listenerRegistration = MutableStateFlow<ListenerRegistration?>(null)

    private val _event = MutableSharedFlow<DiaryPostEvent>()
    val event = _event.asSharedFlow()


    init {
        viewModelScope.launch {
            date.collect { date ->
                listenerRegistration.value?.remove()
                listenerRegistration.value = db.collection("UsersData")
                    .document(user).collection("Diary")
                    .document(date.toString())
                    .addSnapshotListener{ value, error ->
                        var _diaryContent = ""
                        value?.data?.get("contents")?.let {
                            _diaryContent = it.toString()
                        }
                        diaryContent.value = _diaryContent
                    }

            }
        }
    }
    fun addDiaryContent(onSuccess : () -> Unit, onFailure : () -> Unit){
        val data = hashMapOf("contents" to diaryContent.value)
        db.collection("UsersData")
            .document(user).collection("Diary")
            .document(date.value.toString())
            .set(data)
            .addOnSuccessListener {
                postSuccess()
            }
            .addOnFailureListener{
                postFailuer(it)
            }
    }
    fun changeDate(year: Int, month : Int, day : Int, monthText: Month){
        _date.value = DateData(year, month, day, monthText)
    }
    private fun postSuccess() = viewModelScope.launch { _event.emit(DiaryPostEvent.PostSuccess) }
    private fun postFailuer(exception: Exception) = viewModelScope.launch { _event.emit(DiaryPostEvent.PostFailure(exception)) }
    fun showCalenderDialog() = viewModelScope.launch { _event.emit(DiaryPostEvent.ShowCalenderDialog) }
    fun saveData() = viewModelScope.launch { _event.emit(DiaryPostEvent.SaveData) }

    sealed class DiaryPostEvent{
        object PostSuccess : DiaryPostEvent()
        data class PostFailure(val exception: Exception) : DiaryPostEvent()
        object ShowCalenderDialog : DiaryPostEvent()
        object SaveData : DiaryPostEvent()
    }
}