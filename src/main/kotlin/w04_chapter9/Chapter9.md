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
하지만 위 구조로 생성하게 된다면 코루틴의 구조화가 유지되지 못한다는 단점이 있고, 구조화를 유지하려면 함수의 인자로 Parent Job을 전달하여 구조화를 유지하고, coroutineContext를 전달하여 상속을 받게끔 할 수 도 있지만 함수를 사용할 때 마다 많은 부분을 함수의 인자로 전달해야하는 번거로움이 생기게 됩니다.

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

---

## 9-2. 일시 중단 함수의 사용
### 1. 일시 중단 함수의 호출 가능 지점
일시 중단 함수는 내부에 일시 중단 가능 지점을 포함할 수 있기 때문에 일시 중단을 할 수 있는 곳에서만 호출할 수 있습니다.

> 코틀린에서 일시 중단 가능한 지점은 다음 두 가지입니다.
- 코루틴 내부
- 일시 중단 함수 내부

#### 1) 코루틴 내부에서 일시 중단 함수 호출하기
일시 중단 함수는 코루틴의 일시 중단이 가능한 작업을 재사용이 가능한 블록으로 구조화할 수 있도록 만들어진 함수로 코루틴은 언제든지 일시 중단 함수를 호출할 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    delayAndPrint(keyword = "I'm Parent Coroutine")
    launch {
        delayAndPrint(keyword = "I'm Child Coroutine")
    }
}

suspend fun delayAndPrint(keyword: String) {
    delay(1000L)
    println(keyword)
}

/** 결과:
    I'm Parent Coroutine
    I'm Child Coroutine
 */
```

위 코드에서 runBlocking 코루틴이 `delayAndPrint` 함수를 호출하고 launch 코루틴 또한 `delayAndPrint` 함수를 호출하는 것을 볼 수 있습니다. 이 처럼 코루틴에서 일시 중단 함수를 호출하여 정상적으로 실행 되는것을 확인할 수 있습니다.

#### 2) 일시 중단 함수에서 다른 일시 중단 함수 호출하기
일시 중단 함수는 또 다른 일시 중단 함수에서 호출될 수 있습니다. 데이터베이스와 서버에서 키워드로 검색을 실행해서 결과를 가져오는 상황을 `searchByKeyword` 일시 중단 함수를 호출하여 처리할 수 있는 코드를 다음과 같이 만들 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    val results = searchByKeyword("keyword")
    println("[검색 결과]")
    results.forEach { println(it) }
}

suspend fun searchByKeyword(keyword: String): Array<String> {
    val dbResults = searchFromDB(keyword)
    val serverResults = searchFromServer(keyword)
    return arrayOf(*dbResults, *serverResults)
}

suspend fun searchFromDB(keyword: String): Array<String> {
    delay(1000L)
    return arrayOf("[DB]${keyword}1", "[DB]${keyword}2")
}

suspend fun searchFromServer(keyword: String): Array<String> {
    delay(1000L)
    return arrayOf("[Server]${keyword}1", "[Server]${keyword}2")
}

/** 결과:
    [DB]keyword1
    [DB]keyword2
    [Server]keyword1
    [Server]keyword2
    지난 시간 : 2024ms
 */
```

`searchByKeyword` 일시 중단 함수에서 `searchFromDB`, `searchFromServer` 일시 중단 함수를 호출하여 결과를 반환한 것을 확인 할 수 있습니다. 이 처럼 일시 중단 함수 내부에서 일시 중단 함수를 호출할 수 있습니다.

### 2. 일시 중단 함수에서 코루틴 실행하기
#### 1) 일시 중단 함수에서 코루틴 빌더 호출 시 생기는 문제
앞서 다룬 `searchByKeyword` 일시 중단 함수를 다시 한번 살펴 본다면 `searchByKeyword` 일시 중단 함수가 호출되면 2개의 독립적인 작업인 `searchFromDB`, `searchFromServer`가 하나의 코루틴에서 실행됩니다.

```kotlin
suspend fun searchByKeyword(keyword: String): Array<String> {
    val dbResults = searchFromDB(keyword)
    val serverResults = searchFromServer(keyword)
    return arrayOf(*dbResults, *serverResults)
}
```

`searchFromDB` 일시 중단 함수와 `searchFromServer` 일시 중단 함수는 하나의 코루틴에서 실행 되므로, 각자 순차적으로 실행이 되고, 위 코드로 실행한다면 실행의 결과를 가져오는데 까지 2초의 시간이 걸리는 것을 확인할 수 있습니다.

만약 `searchFromDB`, `searchFromServer`의 결과가 영향을 끼치지 않고 독립적으로 결과를 얻어와도 된다면 우리는 병렬적 처리를 하기를 원합니다. 일시 중단 함수 내부에서 병렬적으로 작업을 처리 하기위해선 async 코루틴 빌더 함수로 감싸 서로 다른 코루틴에서 실행되도록 해야합니다.

```kotlin
suspend fun searchByKeyword(keyword: String): Array<String> {'
    val dbResultsDeferred = async { searchFromDB(keyword) }
    val serverResultsDeferred = async { searchFromServer(keyword) }
    return arrayOf(*dbResultsDeferred.await(), *serverResultsDeferred.await())
}
```

위 코드처럼 작성을 한다면`Unresolved reference: async` 이라는 오류 문구를 확인할 수 있습니다. 이유는 `launch`,`async` 함수는 CoroutineScope의 확장 함수로 선언돼 있기 때문에 일시 중단 함수 내부에서는 일시 중단 함수를 호출한 코루틴의 CoroutineScope 객체에 접근할 수 없기 때문 입니다.

#### 2) coroutineScope 사용해 일시 중단 함수에서 코루틴 실행하기
`coroutineScope` 일시 중단 함수를 사용하면 일시 중단 함수 내부에 새로운 CoroutineScope 객체를 생성할 수 있습니다. `coroutineScope`는 구조화를 깨지 않는 CoroutineScope 객체를 생성하며, 생성된 CoroutineScope 객체는 block 람다식에서 수신 객체(this)로 접근할 수 있습니다.

```kotlin
public suspend fun <R> coroutineScope(block: suspend CoroutineScope.() -> R): R
```

`coroutineScope` 일시 중단 함수를 사용해 앞서 다룬 코드를 바꿔보겠습니다.

```kotlin
fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val results = searchByKeywordAsync("keyword")

    results.forEach { println(it) }
    println(getElapsedTime(startTime))
}

suspend fun searchByKeywordAsync(keyword: String): Array<String> = coroutineScope {
    val dbResultsDeferred = async { searchFromDB(keyword) }
    val serverResultsDeferred = async { searchFromServer(keyword) }
    return@coroutineScope arrayOf(*dbResultsDeferred.await(), *serverResultsDeferred.await())
}

/** 결과:
    [DB]keyword1
    [DB]keyword2
    [Server]keyword1
    [Server]keyword2
    지난 시간 : 1022ms
 */
```

위 코드의 결과를 보면 실행 시간이 1초라는 것을 확인할 수 있고, `searchBykeywordAsync` 일시 중단 함수 내부에서 병렬적으로 두 작업이 처리 된 것을 확인할 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/f1acdd4d-dcf2-4f45-a3a1-002781be8431/image.png)

구조화된 코루틴을 그림으로 표시하면 위와 같습니다. runBlocking 코루틴에서 일시 중단 함수를 호출하면 `coroutineScope` 함수를 통해 새로운 Job 객체를 가진 CoroutineScope 객체가 생성되고, 그 자식으로 데이터베이스와 서버로부터 데이터를 가져오는 코루틴이 각각 생성 됩니다.

하지만 여기에는 문제가 있을 수 있습니다. 만약 데이터베이스에서 데이터를 조회하는 코루틴이 예외를 발생 시키면 구조환된 코루틴의 특성에 따라 부모 코루틴으로 예외를 전파해 runBlocking 코루틴까지 취소되며, 서버에서 데이터를 조회하는 코루틴까지 취소한다는 점 입니다.

![](https://velog.velcdn.com/images/tien/post/bcb971b2-da64-4680-99df-0395b1088b85/image.png)

만약 이러한 결과를 원한다면 문제가 없지만 데이터베이스, 서버 둘중 하나에서 예외가 발생하더라도, 예외가 발생하지 않은 결과를 사용해야한다면 문제가 생길 수 있습니다.

#### 3) supervisorScope 사용해 일시 중단 함수에서 코루틴 사용하기
`supervisorScope` 일시 중단 함수를 사용해 예외 전파를 제한하면서 구조화를 깨지 않는 CoroutineScope 객체를 생성할 수 있다는 점을 우리는 알아보았습니다.

`supervisorScope` 일시 중단 함수는 Job 대신 SuperviosrJob 객체를 생성한다는 점을 제외하고는 `coroutineScope` 일시 중단 함수와 동일하게 동작합니다.

```kotlin
public suspend fun <R> supervisorScope(block: suspend CoroutineScope.() -> R): R
```

```kotlin
fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val results = searchByKeywordAsyncException("keyword")

    results.forEach { println(it) }
    println(getElapsedTime(startTime))

    supervisorScope {  }
}

suspend fun searchByKeywordAsyncException(keyword: String): Array<String> = supervisorScope {
    val dbResultsDeferred = async {
        throw Exception("dbResultDeferred에서 예외가 발생했습니다.")
        searchFromDB(keyword)
    }
    val serverResultsDeferred = async { searchFromServer(keyword) }

    val dbResult = try { dbResultsDeferred.await() } catch (_: Exception) {  arrayOf() }
    val serverResult = try { serverResultsDeferred.await() } catch (_: Exception) {  arrayOf() }

    return@supervisorScope arrayOf(*dbResult, *serverResult)
}

/** 결과:
    [Server]keyword1
    [Server]keyword2
    지난 시간 : 1022ms
 */
```

위 코드의 실행 결과를 보면 서버 검색만 정상적으로 실행된 것을 확인할 수 있습니다. 데이터베이스 검색을 실행하는 dbResultsDeferred 코루틴에서 예외가 발생해 해당 코루틴이 취소됐기 떄문입니다. 또한 dbResultDeferred는 부모로 `supervisorScope`를 통해 생성되는 SupervisorJob 객체를 가지므로 dbResultsDeferred에서 발생한 예외는 부모 코루틴으로 전파 되지 않습니다.

![](https://velog.velcdn.com/images/tien/post/7b232725-e5bc-4efa-b821-cd018860ea7a/image.png)
