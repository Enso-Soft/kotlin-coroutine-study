package org.example.w06_chapter12.code.code2

interface UserNameRepository {
    fun saveUserName(id: String, name: String)
    fun getNameByUserId(id: String): String
}