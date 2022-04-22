package com.hyejin.petdiary.scenarios.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class InputDataFirstViewModel: ViewModel() {
    val inputName = MutableStateFlow("")
    val inputAge = MutableStateFlow("")
    val inputWeight = MutableStateFlow("")

    val age = inputAge.map {
        it.toIntOrNull() ?: 0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val weight = inputWeight.map {
        it.toDoubleOrNull() ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun generateMonths() = run {
        val months = ArrayList<String>()
        repeat(12) {
            months += String.format("%s월", it + 1)
        }
        months
    }

    fun generateDays() = run {
        val days = ArrayList<String>()
        repeat(31) {
            days += String.format("%s일", it + 1)
        }
        days
    }
}