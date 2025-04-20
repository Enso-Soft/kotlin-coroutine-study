package w06_chapter12.code2

import org.example.w06_chapter12.code.code2.UserPhoneNumberRepository

class FakeUserPhoneNumberRepository : UserPhoneNumberRepository {
    private val userPhoneNumberMap = mutableMapOf<String, String>()

    override fun saveUserPhoneNUmber(id: String, phoneNumber: String) {
        userPhoneNumberMap[id] = phoneNumber
    }

    override fun getPhoneNumberByUserId(id: String): String {
        return userPhoneNumberMap[id] ?: ""
    }
}