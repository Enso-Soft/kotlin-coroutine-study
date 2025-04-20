package w06_chapter12.code2

import org.example.w06_chapter12.code.code2.UserNameRepository

class StubUserNameRepository: UserNameRepository {
    private val userNameMap = mapOf<String, String>(
        "0x1111" to "홍길동",
        "0x2222" to "조세영"
    )

    override fun saveUserName(id: String, name: String) {
        // 구현하지 않는다.
    }

    override fun getNameByUserId(id: String): String {
        return userNameMap[id] ?: ""
    }
}

class StubUserNameRepositoryDI(
    private val userNameMap: Map<String, String>
): UserNameRepository {
    override fun saveUserName(id: String, name: String) {
        // 구현하지 않는다.
    }

    override fun getNameByUserId(id: String): String {
        return userNameMap[id] ?: ""
    }
}