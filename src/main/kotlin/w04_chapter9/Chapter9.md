> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

---

# 9장 - 일시 중단 함수
## 9-1. 일시 중단 함수와 코루틴
### 1. 일시 중단 함수란 무엇인가?
일시 중단 함수는 `suspend fun` 키워드로 선언되는 함수로 함수 내에 일시 중단 지점을 포함할 수 있는 특별한 기능을 하고 있습니다. 일시 중단 함수는 주로 **코루틴의 비동기 작업과 관련된 복잡한 코드들을 구조화하고 재사용할 수 있는 코드의 집합으로 만드는데 사용**된다.

```kotlin
fun main() = runBlocking<Unit> {
    delay(1000L)
    println("Hello World")
    delay(1000L)
    println("Hello World")
}
```

위 코드에서 `delay(1000L)`과 `println("Hello World")`의 실행을 두번 반복하고 있습니다. 코드의 중복이 있기에 일반적인 상황에서는 함수로 만들어서 사용합니다. 하지만 우리는 일반적인 함수로 사용한다면

```kotlin
fun main() = runBlocking<Unit> {
    normalDelayAndPrintHelloWorld().join()
    normalDelayAndPrintHelloWorld().join()
}

fun normalDelayAndPrintHelloWorld() = CoroutineScope(Dispatchers.Default).launch {
    delay(1000L)
    println("Hello World")
}
```

위와 같이 `delay` 함수를 사용하기 위해 코루틴을 생성하여 호출하는 방식을 사용합니다.
하지만 위 구조로 생성하게 된다면 코루틴의 구조화를 깨버릴 수 있고, 함수의 인자로 Parent Job을 전달하여 구조화를 유지하고, coroutineContext를 전달하여 상속을 받게끔 할 수 도 있지만 함수를 사용할 때 마다 많은 부분을 사용해야하는 문제가 생기게 됩니다.

코루틴에서는 이런 문제를 해결하기 위해 `suspend fun` 키워드를 이용해서 일시 중단 지점을 포함할 수 있도록 지원하고 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    suspendDelayAndPrintHelloWorld()
    suspendDelayAndPrintHelloWorld()
}

suspend fun suspendDelayAndPrintHelloWorld() {
    delay(1000L)
    println("[${Thread.currentThread().name}] Hello World")
}

/** 결과:
	[main @suspend Coroutine#4] Hello World
	[main @suspend Coroutine#4] Hello World
*/
```

위 코드처럼 사용할 수 있으며, `suspend fun` 키워드 함수를 호출하는 코루틴의 환경을 그대로 사용하는 것을 확인할 수 있습니다.

### 2. 일시 중단 함수는 코루틴이 아니다
일시 중단 함수 사용 시 많이 하는 실수 중 하나는 일시 중단 함수를 코루틴과 동일하게 생각하는 것 입니다. 분명한 것은 일시 중단 함수는 코루틴 내부에서 실행되는 코드의 집합일 뿐, 코루틴이 아닙니다!

`suspend fun` 키워드로 생성한 함수를 코루틴 내부에서 호출하는 것은 코루틴을 새롭게 생성하는 것이 아닙니다. 단지 코드의 집합일 뿐임을 기억 해야합니다!

즉 일시 중단 함수는 기존의 함수와 똑같은 재사용이 가능한 코드 블록입니다. 만약 일시 중단 함수를 코루틴처럼 사용하고 싶다면 일시 중단 함수를 코루틴 빌더로 감싸야 합니다.

### 3. 일시 중단 함수를 별도의 코루틴상에서 실행하기
일시 중단 함수를 새로운 코루틴에서 실행하고 싶다면 일시 중단 함수를 코루틴 빌더 함수로 감싸면 됩니다.

```kotlin
fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()

    launch { delayAndPrintHelloWorld() }
    launch { delayAndPrintHelloWorld() }

    println(getElapsedTime(startTime))
}

suspend fun delayAndPrintHelloWorld() {
    delay(1000L)
    println("Hello World")
}

/** 결과:
    지난 시간 : 2ms
    Hello World
    Hello World
 */
```

위 코드에서 launch 함수가 호출돼 생성된 코루틴들은 실행되자마자 delayAndPrintHelloWorld 함수의 호출로 1초간 스레드 사용 권한을 양보합니다. 결과적으로 2ms에 가까운 시간내에 runBlocking 코루틴내의 코드들이 모두 실행 되는 것을 확인할 수 있습니다.

