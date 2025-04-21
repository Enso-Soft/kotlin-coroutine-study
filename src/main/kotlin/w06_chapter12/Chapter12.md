> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

---

# 12장 - 코루틴 단위 테스트
> - 코틀린 단위 테스트 기초
> - 테스트 더블을 사용한 다른 객체에 의존성 있는 객체 테스트
> - 코루틴 테스트 라이브러리 사용법
> - 코루틴 테스트 라이브러리 사용해 코루틴 테스트 작성하기

## 12-1. 단위 테스트 기초
### 1. 단위 테스트란 무엇인가?
단위란 **명확히 정의된 역할의 범위**를 갖는 코드의 집합.
정의된 동작을 실행하는 개별 함수나 클래스 또는 모듈이 단위가 될 수 있습니다.

단위 테스트는 이런 **`단위`**에 대한 자동화된 테스트를 작성하고 실행하는 프로세스입니다.

객체 지향에서는 책임을 객체에 할당하고, 객체 간의 유연한 협력관계를 구축하는 것을 의미합니다.
따라서 소프트웨어의 기능을 담는 역할은 객체가 하며, 객체 지향 프로그래밍에서는 **테스트 대상이 되는 단위는 주로 객체**가 됩니다.

![](https://velog.velcdn.com/images/tien/post/1e0c1336-a9e9-4365-8681-789138a78ab6/image.png)

### 2. 코틀린에서 테스트 환경 설정하기

```kotlin
dependencies {
	...
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
```

코틀린에서 단위 테스트를 만들기 위해서는 테스트 라이브러리에 대한 의존성을 build.gradle.kts에 추가 해주어야합니다.

Junit5를 사용해 테스틀 진행하기 위해서 `junit-jupiter-api`와 이 API를 사용해 테스트를 실행하는 엔진인 `junit-jupiter-engine`에 대한 의존성을 추가합니다. 추가로 테스트에 Junit5를 사용하기 위해서 tasks.test 블록에 `useJUnitPlatform()`을 추가해야합니다.

### 3. 간단한 테스트 만들고 실행하기

```kotlin
class AddUseCase {
    fun add(vararg args: Int): Int {
        return args.sum()
    }
}
```

가변 변수를 입력 받아 모두 더하는 함수를 포함하고 있는 AddUseCase 클래스 입니다.

```kotlin
class AddUseCaseTest {
    @Test
    fun `1 더하기 2는 3이다`() {
        val addUseCase: AddUseCase = AddUseCase()
        val result = addUseCase.add(1, 2)
        assertEquals(3, result)
    }
}
```

개별 테스트는 `@Test` 어노테이션이 붙은 함수로 작성됩니다.
`1 더하기 2는 3이다` 테스트 함수에서는 AddUseCase를 사용해 1과 2의 더하기 연산을 실행 후 `assertEquals`를 사용해 결괏값이 3인지 단언합니다.

![](https://velog.velcdn.com/images/tien/post/91cb64b5-b2e7-4ead-97f7-1672e092d71a/image.png)

테스트는 통과된 것을 확인할 수 있습니다.
만약 테스트가 실패하면 어떤 결과가 만들어질까요?

```kotlin
class AddUseCaseTest {
    @Test
    fun `1 더하기 2는 4이다`() {
        val addUseCase: AddUseCase = AddUseCase()
        val result = addUseCase.add(1, 2)
        assertEquals(4, result)
    }
}
```

테스트 코드를 위와 같이 `1 + 2 = 4`로 만들어두고 단언을 선언한다면

![](https://velog.velcdn.com/images/tien/post/7178f674-6611-43e5-8ec5-ea061514b83b/image.png)

위와 같은 결과 화면을 볼 수 있습니다.

### 4. @BeforeEach 어노테이션을 사용한 테스트 환경 설정

```kotlin
@Test
    fun `-1 더하기 2는 1이다`() {
        val addUseCase: AddUseCase = AddUseCase()
        val result = addUseCase.add(-1, 2)
        assertEquals(1, result)
    }
```

위 코드처럼 하나의 케이스를 더 생성해봅니다.
지금까지 `1 더하기 2는 3이다`, `1 더하기 2는 4이다`, `-1 더하기 2는 1이다` 총 3개의 케이스를 생성했는데 AddUseCase를 인스터스화 하는 과정을 반복적으로 하고 있습니다.

이런 문제를 해결하기 위해 `@BeforeEach` 어노테이션을 사용할 수 있습니다.
`@BeforeEach` 어노테이션을 사용해 함수를 작성하면, 해당 함수는 모든 테스트 실행 전 공통으로 실행 됩니다.

```kotlin
class AddUseCaseTestBeforeEach {
    lateinit var addUseCase: AddUseCase

    @BeforeEach
    fun setup() {
        addUseCase = AddUseCase()
    }

    @Test
    fun `1 더하기 2는 3이다`() {
        val result = addUseCase.add(1, 2)
        println(result)
        assertEquals(3, result)
    }

    @Test
    fun `1 더하기 2는 4이다`() {
        val result = addUseCase.add(1, 2)
        println(result)
        assertEquals(4, result)
    }

    @Test
    fun `-1 더하기 2는 1이다`() {
        val result = addUseCase.add(-1, 2)
        println(result)
        assertEquals(1, result)
    }
}
```

### 5. 테스트 더블을 사용해 의존성 있는 객체 테스트하기
이전까지 테스트 했던 AddUseCase는 내부에서 다른 객체의 의존성이 없었습니다. 하지만 다른 객체의 의존성이 있는 경우 테스트를 어떻게 해야할까요?

```kotlin
class UserProfileFetcher(
    private val userNameRepository: UserNameRepository,
    private val userPhoneNumberRepository: UserPhoneNumberRepository
) {
    fun getUserProfileById(id: String): UserProfile {
        val userName = userNameRepository.getNameByUserId(id)
        val userPhoneNumber = userPhoneNumberRepository.getPhoneNumberByUserId(id)
        
        return UserProfile(
            id = id,
            name = userName,
            phoneNumber = userPhoneNumber
        )
    }
}

data class UserProfile(
    val id: String,
    val name: String,
    val phoneNumber: String
)

interface UserNameRepository {
    fun saveUserName(id: String, name: String)
    fun getNameByUserId(id: String): String
}

interface UserPhoneNumberRepository {
    fun saveUserPhoneNUmber(id: String, phoneNumber: String)
    fun getPhoneNumberByUserId(id: String): String
}
```

`UserProfileFetcher` 클래스는 `UserNameRepository`와 `UserPhoneNumberRepository`의 의존성을 가지고 있으며, `fun getUserProfileById()`는 두개의 객체에서 정보를 가져와 `UserProfile` 데이터 클래스를 만들어 반환하도록 되어있습니다.

이 코드에서는 `UserNameRepository`와 `UserPhoneNumberRepository`의 인터페이스에 대한 구현체가 없기에 `UserProfileFetcher` 객체에 대한 테스트 코드를 작성하는 것이 쉽지 않다는 것을 생각해 볼 수 있습니다.

#### 1) 테스트 더블을 통한 객체 모방
다른 객체와의 의존성을 가진 객체를 테스트하기 위해서는 테스트 더블이 필요합니다.
테스트 더블은 **객체에 대한 대체물**을 뜻하며, **객체의 행동을 모방하는 개체**를 만드는데 사용 됩니다.

![](https://velog.velcdn.com/images/tien/post/2aec8a25-8e3c-4e75-aff6-4f3072c6cb75/image.png)

> 테스트 더블의 대표적인 종류는 아래 3가지가 있습니다.
> - **스텁 _(Stub)_**
> - **페이크 _(Fake)_**
> - **목 _(Mock)_**
>
> 이외에도 더미 _(Dummy)_, 스파이 _(Spy)_ 등 종류가 매우 많습니다.

**스텁 _(Stub)_**
스텁 객체는 미리 정의된 데이터를 반환하는 모방 객체로 반환값이 있는 동작만 미리 정의된 데이터를 반환하도록 구현하며, 반환값이 없는 동작은 구현하지 않습니다.

```kotlin
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
```

위 구현된 스텁 객체는 userNameMap에 미리 정의된 값들을 반환할 수 있도록 합니다.
하지만 userNameMap이 특정한 값으로 고정되어 있기에 유연하지 못한 단점이 있습니다.

이를 해결 하여, 스텁을 좀 더 유연하게 만들기 위해서는 userNameMap을 주입받도록 만들면 됩니다.
```kotlin
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
```

**페이크 _(Fake)_**
페이크 객체는 실제 객체와 비슷하게 동작하도록 구현된 모방 객체입니다. 예를 들어 `UserPhoneNumberRepository` 인터페이스의 실체 구현체가 로컬 데이터베이스를 사용해 유저의 전화번호를 저장한다고 가정한다면

```kotlin
class FakeUserPhoneNumberRepository : UserPhoneNumberRepository {
    private val userPhoneNumberMap = mutableMapOf<String, String>()

    override fun saveUserPhoneNUmber(id: String, phoneNumber: String) {
        userPhoneNumberMap[id] = phoneNumber
    }

    override fun getPhoneNumberByUserId(id: String): String {
        return userPhoneNumberMap[id] ?: ""
    }
}
```

`FakeUserPhoneNumberRepository` 객체는 유저의 전화번호를 로컬 데이터베이스 대신 인메모리에 저장하여 실제 객체처럼 동작할 수 있도록 만듭니다.

#### 2) 테스트 더블을 사용한 테스트

앞서 만든 스텁(`StubUserNameRepositoryDI`)과 페이크(`FakeUserPhoneNumberRepository`) 객체를 사용해 `UserProfileFetcher` 객체를 테스트 해볼 수 있습니다.

`fun getUserProfileById()` 함수에 id = "0x1111" 아이디에 대해 호출 했을 때 UserNameRepository에서 "홍길동"을 반환한다면 `fun getUserProfileById()`로 가져온 유저 프로필에도 이름이 홍길동으로 설정돼 있는지 확인하는 테스트를 작성 해보겠습니다.

```kotlin
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
}
```

위 테스트를 실행 해보면 테스트를 통과하는 것을 확인할 수 있습니다.

> **Given-When-Then**
Given-When-Then은 테스트 코드 작성법 중 하나로 테스트의 시나리오를 설명함으로써 테스트 코드의 가족성을 높히는데 사용 됩니다.
>
**Given** : 테스트 환경을 설정하는 작업
**When** : 동작이나 이벤트를 발생시키고 결과를 얻는다.
**Then** : 테스트 결과를 검증한다.

이번에는 `UserPhoneNumberRepository` 객체에 유저의 휴대폰 번호가 저장돼 있는 경우 `UserProfileFetcher`를 사용해 유저 프로필을 가져오면 저장된 휴대폰 번호가 반환되는지 확인하는 테스트를 작성 해보도록 하겠습니다.

```kotlin
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
```

위 테스트를 실행 해보면 정상적으로 테스트가 통과되는것을 확인할 수 있습니다.

---

## 12-2. 코루틴 단위 테스트 시작하기
### 1. 코루틴 테스트 작성하기
```kotlin
class RepeatAddUseCase {
    suspend fun add(repeatTime: Int): Int = withContext(Dispatchers.Default) {
        var result = 0
        repeat(repeatTime) {
            result += 1
        }
        return@withContext result
    }
}
```

테스트를 위한 객체를 만들 `RepeatAddUseCase` 클래스를 작성했습니다.

```kotlin
class RepeatAddUseCaseTest {
    @Test
    fun `100번 더하면 100이 반환된다 - 일반함수`() {
        // Given
        val repeatAddUseCase = RepeatAddUseCase()

        // When
        val result = repeatAddUseCase.add(100)

        // Then
        assertEquals(100, result)
    }
}
```

객체 기능에 대한 테스트 코드를 작성하고 컴파일을 할 때 `Suspend function 'suspend fun add(repeatTime: Int): Int' should be called only from a coroutine or another suspend function.`이라는 오류를 발생하는 것을 확인할 수 있습니다. 이유는 `fun add()`는 일시 중단 함수인데 테스트 함수는 일반 함수 이기 때문입니다.

이를 해결하는 방법은 테스트 함수를 일시 중단 함수를 호출할 수 있는 `runBlocking`으로 감싸면 됩니다.

```kotlin
@Test
fun `100번 더하면 100이 반환된다 - 코루틴 함수`() = runBlocking {
    // Given
    val repeatAddUseCase = RepeatAddUseCase()

    // When
    val result = repeatAddUseCase.add(100)

    // Then
    assertEquals(100, result)
}
```

위 테스트를 실행해보면 정상적으로 테스트가 통과하는 것을 확인할 수 있습니다.

#### 2) runBlocking을 사용한 테스트의 한계
일반적으로 `runBlocking` 함수를 사용한 테스트에서는 실행에 오랜 시간이 걸리는 일시 중단 함수를 실행하면 오랜 테스트 시간이 걸리는 문제가 발생합니다.

```kotlin
class RepeatAddWithDelayUseCase {
    suspend fun add(repeatTime: Int): Int = withContext(Dispatchers.Default) {
        var result = 0
        repeat(repeatTime) {
            delay(100L)
            result += 1
        }
        return@withContext result
    }
}

class RepeatAddWithDelayUseCaseTest {
    @Test
    fun `100번 더하면 100이 반환된다-runBlocking`() = runBlocking {
        // Given
        val repeatAddUseCase = RepeatAddWithDelayUseCase()

        // When
        val result = repeatAddUseCase.add(100)

        // Then
        assertEquals(100, result)
    }
}
```

위 테스트를 실행하면 테스트는 정상적으로 통과 되지만 테스트 실행 완료까지 10초 이상의 시간이 걸리는것을 확인할 수 있습니다. 이유는 runBlocking으로 하나의 스레드에서만 일시중단 함수를 실행하는것과 0.1초를 기다리는것이 실제 시간을 기다리는 이유이기 때문입니다.

---

## 12-3. 코루틴 테스트 라이브러리
시간이 걸리는 작업이 포함된 일시 중단 함수를 테스트할 때 runBlocking 함수를 사용하면 오랜 시간이 걸릴 수 있다. 이를 해결하기 위해 코루틴 테스트 라이브러리는 가상 시간을 사용하는 코루틴 스케줄러를 제공합니다.

### 1. 코루틴 테스트 라이브러리 의존성 설정하기

```kotlin
dependencies {
    ...
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
}
```

build.gradle.kts에 위 의존성을 추가로 설정할 수 있습니다.

### 2. TestCoroutineScheduler 사용해 가상 시간에서 테스트 진행하기

#### 1) advanceTimeBy 사용해 가상 시간 흐르게 만들기
```kotlin
class TestCoroutineScheduler {
    @Test
    fun `가상 시간 조절 테스트`() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        testCoroutineScheduler.advanceTimeBy(5000L)
        assertEquals(5000L, testCoroutineScheduler.currentTime)

        testCoroutineScheduler.advanceTimeBy(6000L)
        assertEquals(11000L, testCoroutineScheduler.currentTime)

        testCoroutineScheduler.advanceTimeBy(10000L)
        assertEquals(21000L, testCoroutineScheduler.currentTime)
    }
}
```

위 테스트를 실행 해보면 `advanceTimeBy` 함수를 사용해 시간이 흐르게 한 뒤 `testCoroutineScheduler.currentTime`의 값이 선언된 단언을 통과하는 것을 확인할 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/a4a03b31-f571-4014-86eb-c04633834e95/image.png)

#### 2) TestCoroutineScheduler와 StandardTestDispatcher 사용해 가상 시간 위에서 테스트 진행하기

```kotlin
class TestCoroutineScheduler {
    @Test
    fun `가상 시간 위에서 테스트 진행`() {
        val testCoroutineScheduler: TestCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher: TestDispatcher = StandardTestDispatcher(scheduler = testCoroutineScheduler)

        val testCoroutineScope: CoroutineScope = CoroutineScope(context = testDispatcher)

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // Then
        assertEquals(0, result)

        testCoroutineScheduler.advanceTimeBy(5000L)
        assertEquals(0, result)

        testCoroutineScheduler.advanceTimeBy(6000L)
        assertEquals(1, result)

        testCoroutineScheduler.advanceTimeBy(10000L)
        assertEquals(2, result)
    }
}
```

`testCoroutineScope`는 `testCoroutineScheduler`에 의해 시간이 관리되기 때문에 이 범위에서 실행되는 코루틴들은 `advanceTimeBy` 함수를 호출해서 테스트를 진행하게 되며, 위 테스트를 실행했을 때 테스트 통과가 되게 됩니다.

![](https://velog.velcdn.com/images/tien/post/ca6c4f2d-d68e-49a6-a081-3c5bfe784e81/image.png)

#### 3) advanceUntilIdle 사용해 모든 코루틴 실행시키기
위에서 `advanceTimeBy`를 호출해서 직접 시간을 컨트롤하는 경우는 테스트를 할 때 거의 없습니다.
코루틴을 테스트 하기 위해선 모든 코루틴이 완료 될때 즉 코루틴 작업이 모두 끝나는 가상 시간이 모두 흘렀을 때를 기준으로 단언을 체크하도록 해야합니다. 이에 코루틴 라이브러리는 `advanceUntilIdle` 함수를 제공해 테스트를 할 수 있게끔 해주고 있습니다.

```kotlin
class TestCoroutineScheduler {
    @Test
    fun `advanceUntilIdle의 동작 살펴보기`() {
        val testCoroutineScheduler: TestCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher: TestDispatcher = StandardTestDispatcher(scheduler = testCoroutineScheduler)

        val testCoroutineScope: CoroutineScope = CoroutineScope(context = testDispatcher)

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }
        testCoroutineScheduler.advanceUntilIdle()

        // Then
        assertEquals(2, result)
    }
}
```

위 테스트를 실행 해보면 정상적으로 단언이 통과 되는것을 확인할 수 있습니다. 설정된 20초의 delay를 모두 흐르게 하는 `advanceUntilIdle`를 호출하였기 때문입니다.

### 3. TestCoroutineScheduler를 포함하는 StandardTestDispatcher
```kotlin
public fun StandardTestDispatcher(
    scheduler: TestCoroutineScheduler? = null,
    name: String? = null
): TestDispatcher = StandardTestDispatcherImpl(
    scheduler ?: TestMainDispatcher.currentTestScheduler ?: TestCoroutineScheduler(), name)
```

`StandardTestDispatcher` 함수를 살펴보면 scheduler 인자로 아무런 값이 전달되지 않으면 TestMainDispatcher.currentTestScheduler를 확인하고, 없으면 `TestCoroutineScheduler()`를 호출해 TestCoroutineScheduler 객체를 생성합니다.

![](https://velog.velcdn.com/images/tien/post/82209253-4d0b-4953-9551-64e5862f4f37/image.png)

```kotlin
class TestCoroutineScheduler {
    @Test
    fun `StandardTestDispatcher 사용하기`() {
        val testDispatcher: TestDispatcher = StandardTestDispatcher()
        val testCoroutineScope: CoroutineScope = CoroutineScope(context = testDispatcher)

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(2, result)
    }
}
```

testDispatcher의 scheduler 프로퍼티를 통해 `advanceUntilIdle()` 함수를 호출할 수 있습니다.

### 4. TestScope 사용해 가상 시간에서 테스트 진행하기
```kotlin
class TestCoroutineScheduler {
    @Test
    fun `TestScope 사용하기`() {
        val testCoroutineScope: TestScope = TestScope()

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }
        testCoroutineScope.advanceUntilIdle()

        // Then
        assertEquals(2, result)
    }
}
```

위 테스트에서는 TestDispatcher 객체를 생성해 이를 CoroutineScope 함수로 감싸는 대신 `TestScope` 함수만 호출하여 진행한 것을 확인할 수 있습니다.

```kotlin
public fun TestScope.advanceUntilIdle(): Unit = testScheduler.advanceUntilIdle()
```

`advanceUntilIdle()` 함수는 위와 같이 확장함수로 TestScope 인터페이스 내의 `testScheduler`에 접근하여 호출하는것을 확인할 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/b70ab140-1e36-4b31-8b21-733b8a4f1d28/image.png)

### 5. runTest 사용해 테스트 만들기
```kotlin
class TestCoroutineScheduler {
    @Test
    fun `runTest 사용하기`() {
        // Given
        var result = 0
        
        // When
        runTest {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
        }
        
        // Then
        assertEquals(2, result)
    }
}
```

더욱 간단하게 runTest 함수를 사용하면 TestScope 객체를 사용해 코루틴을 실행시키고, 그 코루틴 내부에서 일시 중단 함수가 실행되더라도 작업이 곧바로 실행 완료 될 수 있도록 가상 시간을 흐르게 만드는 기능을 가진 코루틴 빌더 입니다.

![](https://velog.velcdn.com/images/tien/post/127d9715-02ca-4f5e-b0de-6400900de7e6/image.png)
