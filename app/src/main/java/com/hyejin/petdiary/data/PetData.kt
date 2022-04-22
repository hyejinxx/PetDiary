package com.hyejin.petdiary.data

data class PetData(
    var name: String = "",
    var age: String = "",
    var gender: String = "",
    var birth: Birth = Birth("1월", "1일"),
    var weight: String = ""
)
