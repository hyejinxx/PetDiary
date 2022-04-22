package com.hyejin.petdiary.scenarios.main.schedulePost

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

class SchedulePostViewModel():ViewModel() {

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid
    private val listenerRegistration = MutableStateFlow<ListenerRegistration?>(null)

    private val _event = MutableSharedFlow<SchedulePostEvent>()
    val event = _event.asSharedFlow()

    private val _date = MutableStateFlow(
        LocalDate.now().let { current ->
            DateData(current.year, current.monthValue, current.dayOfMonth, current.month)
        }
    )
    val date = _date.asStateFlow()
    val dateString = date.map { date ->
        String.format("%s. %s. %s", date.year, date.monthText, date.day)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "-" )


    var schedule = MutableStateFlow("")
    var scheduleArray = MutableStateFlow<ArrayList<String>?>(null)

    init {
        viewModelScope.launch {
            date.collect { date ->
                listenerRegistration.value?.remove()
                listenerRegistration.value = db.collection("UsersData")
                    .document(user)
                    .collection("Schedule")
                    .document(date.toString()).addSnapshotListener { value, error ->
                        var _scheduleArray = arrayListOf<String>()
                        value?.data?.entries?.sortedBy { it.key }?.forEach {
                            _scheduleArray.add(it.value.toString())
                        }
                        scheduleArray.value = _scheduleArray
                    }
            }
        }
    }
    fun addSchedule(onSuccess : ()-> Unit, onFailure : ()->Unit){
        if (scheduleArray.value?.isEmpty()!!){
            var data = hashMapOf(System.currentTimeMillis().toString() to schedule.value)
            db.collection("UsersData").document(user)
                .collection("Schedule").document(date.value.toString()).set(data)
                .addOnSuccessListener {
                    postSuccess()
                }
                .addOnFailureListener {
                    postFailure(it)
                }
        }
        else {
            db.collection("UsersData").document(user)
                .collection("Schedule").document(date.value.toString())
                .update(System.currentTimeMillis().toString(), schedule.value)
                .addOnSuccessListener {
                    postSuccess()
                }
                .addOnFailureListener {
                    postFailure(it)
                }
        }
    }

    fun changeDate(year: Int, month : Int, day : Int, monthText : Month){
        _date.value = DateData(year, month + 1, day, monthText)
    }

    fun showCalenderDialog() = viewModelScope.launch { _event.emit(SchedulePostEvent.ShowCalenderDialog) }
    fun showScheduleDialog() = viewModelScope.launch { _event.emit(SchedulePostEvent.ShowScheduleDialog) }
    private fun postSuccess() = viewModelScope.launch { _event.emit(SchedulePostEvent.PostSuccess) }
    private fun postFailure(exception: Exception) = viewModelScope.launch { _event.emit(SchedulePostEvent.PostFailure(exception)) }

    sealed class SchedulePostEvent{
        object ShowCalenderDialog : SchedulePostEvent()
        object ShowScheduleDialog : SchedulePostEvent()
        object PostSuccess : SchedulePostEvent()
        data class PostFailure (val exception: Exception) : SchedulePostEvent()

    }

}
