package w06_chapter12.code2

import org.example.w06_chapter12.code.code2.UserProfileFetcher
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserProfileFetcherTest {
    @Test
    fun `UserNameRepository가 반환하는 이름이 홍길동이면 UserProfileFetcher에서 UserProfile를 가져왔을 때 이름이 홍길동이어야 한다`() {
        // Given
        val userProfileFetcher = UserProfileFetcher(
            userNameRepository = StubUserNameRepositoryDI(
                userNameMap = mapOf<String, String>(
                    "0x1111" to "홍길동",
                    "0x2222" to "조세영"
                )
            ),
            userPhoneNumberRepository = FakeUserPhoneNumberRepository()
        )

        // When
        val userProfile = userProfileFetcher.getUserProfileById("0x1111")

        // Then
        assertEquals("홍길동", userProfile.name)
    }

    @Test
    fun `UserPhoneNumberRepository에 휴대폰 번호가 저장돼 있으면, UserProfile를 가져왔을때 해당 휴대폰 번호가 반환돼야 한다`() {
        // Given
        val userProfileFetcher = UserProfileFetcher(
            userNameRepository = StubUserNameRepositoryDI(
                userNameMap = mapOf<String, String>(
                    "0x1111" to "홍길동",
                    "0x2222" to "조세영"
                )
            ),
            userPhoneNumberRepository = FakeUserPhoneNumberRepository().apply {
                this.saveUserPhoneNUmber("0x1111", "010-xxxx-xxxx")
            }
        )

        // When
        val userProfile = userProfileFetcher.getUserProfileById("0x1111")

        // Then
        assertEquals("010-xxxx-xxxx", userProfile.phoneNumber)
    }
}