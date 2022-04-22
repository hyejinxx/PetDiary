package com.hyejin.petdiary.scenarios.main.petPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyejin.petdiary.data.PetData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PetPageViewModel : ViewModel() {

    // Firebase
    val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    val user = auth.currentUser!!.uid
    var dataIndex = MutableStateFlow(0)
    var petData = MutableStateFlow<PetData?>(null)
    var birthString = MutableStateFlow("")

    private val listenerRegistration = MutableStateFlow<ListenerRegistration?>(null)

    private val _event = MutableSharedFlow<PetPageEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            listenerRegistration.value?.remove()
            listenerRegistration.value = db.collection("UsersData")
                .document(user).collection("aboutPet")
                .document("Data")
                .addSnapshotListener{ value, error ->
                    var _petData = value?.toObject(PetData::class.java)!!
                    petData.value = _petData
                    birthString.value = "${petData.value?.birth?.month} ${petData.value?.birth?.day}"
                }
        }
    }
    fun changePetData(index: String, data : Any){
        db.collection("UsersData")
            .document(user).collection("aboutPet").document("Data").update(index, data)
    }
     fun showDialog(index : Int){
         dataIndex.value = index
         viewModelScope.launch { _event.emit(PetPageEvent.ShowDialog)}
     }
    sealed class PetPageEvent{
        object ShowDialog : PetPageEvent()
    }
}