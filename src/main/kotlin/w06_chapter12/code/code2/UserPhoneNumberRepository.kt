package org.example.w06_chapter12.code.code2

interface UserPhoneNumberRepository {
    fun saveUserPhoneNUmber(id: String, phoneNumber: String)
    fun getPhoneNumberByUserId(id: String): String
}