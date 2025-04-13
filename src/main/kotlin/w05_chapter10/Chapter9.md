> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

---

# 10장 - 코루틴의 이해
## 10-1. 서브루틴과 코루틴
### 1. 루틴과 서브 루틴
> 우리는 종종 운동루틴 또는 생활루틴이라는 단어를 사용합니다. 이 단어들에서 루틴은 **특정한 일을 하기 위한 일련의 처리 과정** 이라는 뜻 입니다. 이와 비슷하게 프로그래밍에서는 루틴을 **특정한 일을 처리하기 위한 일련의 명령** 이라는 뜻으로 사용하고 있는데 이런 일련의 명령을 **함수 또는 메서드**라고 부릅니다.

그렇다면 서브 루틴이란? **함수 내에서 하뭇가 호출될 경우 호출된 함수를 서브루틴**이라고 부릅니다.

```kotlin
fun routine() {
    routineA() // routineA는 routine의 서브루틴입니다.
    routineB() // routineB는 routine의 서브루틴입니다.
}

fun routineA() {
    ...
}

fun routineB() {
    ...
}
```

위 코드에서는 `routineA`, `routineB` 함수를 `routine`의 서브 루틴이라고 합니다.
마찬가지로 `routine` 함수는 main 함수 내부에서 호출되면 `routine` 함수는 main 함수의 서브루틴이 됩니다.

> 프로그래밍에서 서브루틴이란 함수의 하위에서 실행되는 함수를 말합니다.

![](https://velog.velcdn.com/images/tien/post/83858386-91ee-4f0c-afab-c71dbeb46300/image.png)

루틴 속에서 서브루틴이 실행되는 것을 그림으로 설명하면 위와 같습니다.

서브루틴은 한 번 호출되면 끝까지 실행 됩니다. 따라서 루틴에 의해 서브루틴이 호출되면 루틴을 실행하던 스레드는 서브루틴을 실행하는 데 사용돼 서브루틴의 실행이 완료될 때까지 루틴은 다른 작업을 할 수 없습니다.

### 2. 서브루틴과 코루틴의 차이
루틴에서 서브루틴이 호출되면 서브루틴이 완료될 때까지 루틴이 아무런 작업을 할 수 없는 것과 다르게 코루틴은 함께 실행되는 루틴으로 서로 간에 스레드 사용을 양보하며 함께 실행 됩니다.

```kotlin
fun main() = runBlocking<Unit> {
    launch {
        while(true) {
            println("자식 코루틴에서 작업 실행 중")
            yield()
        }
    }

    while(true) {
        println("부모 코루틴에서 작업 실행 중")
        yield()
    }
}

/** 결과:
    부모 코루틴에서 작업 실행 중
    자식 코루틴에서 작업 실행 중
    부모 코루틴에서 작업 실행 중
    자식 코루틴에서 작업 실행 중
    ...
 */
```

위 코드에서 runBlocking 부모 코루틴과 launch 자식 코루틴이 생성 되어 있으며, 이 코루틴 작업에서는 메인 스레드 하나만 사용 하고 있습니다. 부모 코루틴에서는 while 반복문을 사용해 `부모 코루틴에서 작업 실행중`을 출력하고 자식 코루틴에서도 while 반복문을 사용해 `자식 코루틴에서 작업 실행 중`을 출력하고 있습니다. 이때 `yield()` 함수를 호출해 스레드 사용 권한을 양보하며 스레드가 필요한 코루틴이 스레드 사용 권한을 가져가 실행하게 됩니다.

![](https://velog.velcdn.com/images/tien/post/6404b7ca-9bb3-49cf-8611-31e83ca246ef/image.png)

위 그림과 같이 runBlocking 코루틴_(corutine#1)_ 과 launch 코루틴_(corutine#2)_이 자신의 작업을 실행하고 스레드를 양보하는 작업을 반복하는 것을 볼 수 있습니다.

한 번 실행되면 실행이 완료될 때까지 스레드를 사용하는 서브루틴과 다르게 **코루틴은 스레드 사용 권한을 양보하며 함께 실행** 됩니다.코루틴은 서로 간에 협력적으로 동작한다고도 합니다. 코루틴이 협력적으로 동작하기 위해서는 코루틴이 작업을 하지 않는 시점에 스레드 사용 권한을 양보하고 일시 중단해야 합니다.

## 10-2. 코루틴의 스레드 양보
코루틴은 작업 중간에 스레드의 사용이 필요 없어지면 스레드를 양보하며, 양보된 스레드는 다른 코루틴을 실행하는 데 사용할 수 있습니다. 그렇다면 **스레드를 양보하는 주체는 누구일까요? 바로 코루틴**입니다. 스레드에 코루틴을 할당해 실행되도록 만드는 주체는 CoroutineDispatcher 객체이지만 스레드를 양보하는 주체는 코루틴으로 CoroutineDispatcher는 코루틴이 스레드를 양보하도록 강제하지 못합니다.

코루틴이 스레드를 양보하려면 코루틴에서 직접 스레드 양보를 위한 함수를 호출해야합니다. **코루틴에서 스레드 양보를 위한 함수가 호출되지 않는다면 코루틴은 실행 완료될 때까지 스레드를 점유**합니다. 이러한 특성 때문에 코루틴의 스레드 양보는 작업의 실행 흐름에서 중요한 역할을 합니다.

### 1. delay 일시 중단 함수를 통해 알아보는 스레드 양보

```kotlin
fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    repeat(10) { repeatTime ->
        launch {
            delay(1000L)
            println("[${getElapsedTime(startTime)}] 코루틴${repeatTime} 실행 완료")
        }
    }
}

/** 결과:
    [지난 시간 : 1011ms] 코루틴0 실행 완료
    [지난 시간 : 1024ms] 코루틴1 실행 완료
    [지난 시간 : 1024ms] 코루틴2 실행 완료
    [지난 시간 : 1024ms] 코루틴3 실행 완료
    [지난 시간 : 1024ms] 코루틴4 실행 완료
    [지난 시간 : 1024ms] 코루틴5 실행 완료
    [지난 시간 : 1024ms] 코루틴6 실행 완료
    [지난 시간 : 1024ms] 코루틴7 실행 완료
    [지난 시간 : 1024ms] 코루틴8 실행 완료
    [지난 시간 : 1024ms] 코루틴9 실행 완료
 */
```

각 코루틴은 메인 스레드에서 실행 되지만 시작하자마자 `delay` 함수로 1초 동안 메인 스레드 사용을 양보합니다. 이 때문에 하나의 코루틴이 실행된 후 바로 다음 코루틴이 실행 될 수 있으며, 10개의 코루틴이 거의 동시에 시작됩니다. 이후 각 코루틴은 `코루틴 실행 완료`를 출력할 때만 메인 스레드를 잠시 점유하므로 10개의 코루틴이 거의 동시에 완료 될 수 있습니다. 따라서 10개의 코루틴을 모두 실행하는 데 걸린 시간이 1초 정도밖에 안 되는 것을 확인할 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    repeat(10) { repeatTime ->
        launch {
            Thread.sleep(1000L)
            println("[${getElapsedTime(startTime)}] 코루틴${repeatTime} 실행 완료")
        }
    }
}

/** 결과:
    [지난 시간 : 1009ms] 코루틴0 실행 완료
    [지난 시간 : 2030ms] 코루틴1 실행 완료
    [지난 시간 : 3035ms] 코루틴2 실행 완료
    [지난 시간 : 4040ms] 코루틴3 실행 완료
    [지난 시간 : 5046ms] 코루틴4 실행 완료
    [지난 시간 : 6048ms] 코루틴5 실행 완료
    [지난 시간 : 7053ms] 코루틴6 실행 완료
    [지난 시간 : 8058ms] 코루틴7 실행 완료
    [지난 시간 : 9064ms] 코루틴8 실행 완료
    [지난 시간 : 10069ms] 코루틴9 실행 완료
 */
```

만약 `delay` 함수를 호출해서 스레드를 양보하는 것이 아닌 방법으로 실행해보려면 위와 같이 `Thread.sleep` 함수를 호출하면 됩니다. 이 함수는 스레드를 블록킹 하는것이므로 각 대기 시간동안 스레드를 점유하는 것을 뜻 합니다. 결과적으로 10개의 코루틴을 모두 실행 하는데 10초의 시간이 걸린 것을 확인할 수 있습니다.

### 2. join과 await의 동작 방식 자세히 알아보기
Job의 `join` 함수나 Deferred의 `await` 함수가 호출되면 해당 함수를 호출한 코루틴은 스레드를 양보하고 `join` 또는 `await`의 대상이 된 코루틴 내부의 코드가 실행 완료될 때까지 일시 중단 됩니다.

```kotlin
fun main() = runBlocking<Unit> {
    val job = launch {
        println("1. launch 코루틴 작업이 시작됐습니다")
        delay(1000L)
        println("2. launch 코루틴 작업이 완료됐습니다")
    }
    println("3. runBlocking 코루틴이 곧 일시 중단 되고 메인 스레드가 양보됩니다")
    job.join()
    println("4. runBlocking이 메인 스레드에 분배돼 작업이 다시 재개됩니다.")
}

/** 결과:
    3. runBlocking 코루틴이 곧 일시 중단 되고 메인 스레드가 양보됩니다
    1. launch 코루틴 작업이 시작됐습니다
    2. launch 코루틴 작업이 완료됐습니다
    4. runBlocking이 메인 스레드에 분배돼 작업이 다시 재개됩니다.
 */
```

위 코드는 하나의 스레드를 사용해서 코루틴들을 실행하게 됩니다.<br>실행 순서는 항상 `3` -> `1` -> `2` -> `4` 순서로 실행 되게 됩니다. 코드가 작성된 순서로 보면 `1`이 먼저 실행될것 같지만 위 코드를 몇번이고 실행 해봐도 그러한 일을 일어나지않고 항상 `3` -> `1` -> `2` -> `4` 순서가 고정적으로 출력 되는 것을 확인할 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/12962fa0-2884-4b22-8cd4-b909895e27e4/image.png)

이유는 위 코드에서 runBlocking 코루틴과 launch 코루틴은 단일 스레드인 메인 스레드에서 실행되기 때문에 하나의 코루틴이 스레드를 양보하지 않으면 다른 코루틴이 실행되지 못합니다. 따라서 각 코루틴은 다음 순서로 동작 합니다.

1. 처음 메인 스레드를 점유하는 것은 `runBlocking` 코루틴 입니다. `launch` 함수를 호출해 새로운 코루틴을 생성하지만 `launch` 코루틴이 생성 후에도 `runBlocking` 코루틴이 계속해서 메인 스레드를 점유하고 있고 `launch` 코루틴은 Dispatcher 내 작업 대기열에 적재된 상태로 머뭅니다. 여기에서 `join` 함수를 호출할 때 `runBlocking`은 메인 스레드를 양보하게 됩니다.
2. 이때 자유로워진 메인 스레드를 `launch` 코루틴이 점유하게 됩니다. 이후 `1`번 로그를 출력하고 이어서 `delay` 일시 중단 함수를 호출해 메인 스레드를 양보합니다.
3. `launch` 코루틴은 `delay`에 의한 일시 중단 시간 1초가 끝나고 재개 되며, `2`번 로그를 출력하고 실행이 완료 됩니다.
4. `launch` 코루틴의 실행이 완료되면 `join`에 의해 일시 중단된 `runBlocking` 코루틴은 재개돼 `4`번 로그를 출력하게 됩니다.

> `join`이나 `await` 함수가 호출되면 호출부의 코루틴은 스레드를 양보하고 일시 중단하며, 대상이 된 코루틴이 실행 완료될 때까지 재개되지 않습니다. 그 사이 양보된 스레드는 다른 코루틴을 실행하는 데 사용될 수 있습니다.

### 3. yield 함수 호출해 스레드 양보하기
`delay`, `join`, `await` 같은 일시 중단 함수들은 스레드 양보를 직접 호출하지 않아도 작업을 위해 내부적으로 스레드 양보를 일으킵니다. 이는 개발자가 직접 세세하게 조정할 필요가 없게 합니다.

하지만 몇 가지 특수한 상황에서는 스레드 양보를 직접 호출해야 할 필요가 있습니다. 이를 위해 코루틴 라이브러리는 `yield` 함수를 통해 직접 스레드 양보를 실행하는 방법을 제공합니다.

만약 반복적으로 작업하며 일시 중단, 즉 지연되는 시점이 없어야하는 상황이 있다면 어떻게 해야할까요?

```kotlin
fun main() = runBlocking<Unit> {
    val job = launch {
        while (this.isActive) {
            println("작업 중")
        }
    }

    delay(100L)
    job.cancel()
}

/** 결과:
    작업 중
    작업 중
    작업 중
    작업 중
    ...
 */
```

위 코드에서는 runBlocking 자식인 launch 코루틴에서 while 반복문을 통해 실행될때 마다 코루틴이 활성화돼 있는지를 `this.isActivity`를 통해 체크하고 있습니다. launch 코루틴을 실행한 후 0.1초 이후 launch 코루틴을 취소하고 있습니다.

코드의 실행 결과를 보면 launch 코루틴은 취소되지 않고 `작업 중`이 무한하게 출력되는 것을 확인할 수 있습니다. 이러한 결과가 나오는 이유는 runBlocking 코루틴이 delay 일시 중단 함수를 호출해 메인 스레드를 양보하면 launch 코루틴이 메인 스레드를 점유하게 되는데 launch 코루틴에서는 스레드를 양보하지 않기 때문에 계속 메인 스레드를 점유하기 때문입니다.

![](https://velog.velcdn.com/images/tien/post/78e8308a-13b4-45de-a456-993a6e517bd4/image.png)

`job.cancel()` 자체가 실행되지 못하므로 while 반복문에서 `this.isActivity`로 코루틴이 활성화 돼 있는지 체크하더라도 취소가 요청된 적이 없으므로 계속해서 실행 됩니다.

이 문제를 해결 하기 위해서 while문 내에 스레드 양보를 할 수 있도록 `yield` 함수를 호출해야합니다.

```kotlin
fun main() = runBlocking<Unit> {
    val job = launch {
        while (this.isActive) {
            println("작업 중")
            yield()
        }
    }

    delay(100L)
    job.cancel()
}

/** 결과:
    작업 중
    작업 중
    작업 중
    작업 중
    ...
    프로세스 종료
 */
```

## 10-3. 코루틴의 실행 스레드
실제 애플리케이션에서는 멀티 스레드상에서 코루틴이 동작합니다. 멀티 스레드 환경에서 코루틴이 스레드를 양보한 후 실행이 재개될 때 실행 스레드에 어떤 변화가 일어날 수 있는지 알아보도록 하겠습니다.

### 1. 코루틴의 실행 스레드는 고정이 아니다
코루틴이 일시 중단 후 재개되면 CoroutineDispatcher 객체는 재개된 코루틴을 다시 스레드에 할당하게 됩니다. 이때 CoroutineDispatcher 객체는 코루틴을 자신이 사용할 수 있는 스레드 중 하나에 할당하는데 이 스레드는 코루틴이 일시 중단 전에 실행되던 스레드와 다를 수 있습니다.

예를 들어 `Thread-1` 스레드와 `Thread-2` 스레드를 사용하는 CoroutineDispatcher 객체가 있고, 이 CoroutineDispatcher 객체에 실행 요청된 코루틴이 있다고 가정을 해보겠습니다.

![](https://velog.velcdn.com/images/tien/post/3ab07b8b-27e2-4470-83d3-f6101e02bc6a/image.png)

위 그림과 같이 `Thread-1` 스레드에서 실행되고 있던 코루틴이 일시 중단되면 코루틴은 실행중이던 스레드에서 그대로 일시 중단 됩니다.

![](https://velog.velcdn.com/images/tien/post/68aeabe6-2adb-4f68-9b95-266c191cb8f7/image.png)

이렇게 코루틴이 일시 중단되면 해당 스레드는 다른 코루틴에 의해 점유될 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/6b0f2b6f-5fd6-420e-82e0-46655c159507/image.png)
> 새로운 코루틴은 비어 있는 스레드 중 하나에 보내지므로 Thread-2 스레드에도 할당될 수 있습니다. 하지만 여기에서는 Thread-1 스레드에 할당돼 실행되는 상황을 가정합니다.

기존 코루틴이 일시 중단된 상황에서 새로운 코루틴이 CoroutineDispatcher 객체에 실행 요청될 경우 CoroutineDispatcher 객체의 작업대기열로 이동한 후 새로운 코루틴을 `Thread-1` 스레드에 보내 실행될 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/7fd65096-29ca-41bf-b970-b1b577719ada/image.png)

기존 일시 중단됐던 코루틴이 재개되면 다시 CoroutineDispatcher 객체의 작업 대기열로 이동하며, CoroutineDispatcher 객체에 의해 스레드로 보내져 실행됩니다. `Thread-1` 스레드에서는 이미 새로운 코루틴이 실행되고 있으므로 재개된 코루틴은 남은 스레드인 `Thread-2` 스레드로 보내집니다.

이처럼 CoroutineDispatcher 객체는 쉬고 있는 스레드에 코루틴을 할당해 실행하기 때문에 콜튄은 일시 중단 전 실행되던 스레드와 재개 후 실행되는 스레드가 다를 수 있습니다. 즉 코루틴의 실행 스레드는 고정이 아니라 바뀔 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    val dispatcher = newFixedThreadPoolContext(2, "MyThread")
    launch(dispatcher) {
        repeat(5) {
            println("[${Thread.currentThread().name}] 코루틴 실행이 일시 중단 됩니다")
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행이 재개 됩니다")
        }
    }
}

/** 결과:
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-2] 코루틴 실행이 재개 됩니다
    [MyThread-2] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-2] 코루틴 실행이 재개 됩니다
    [MyThread-2] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-2] 코루틴 실행이 재개 됩니다
 */
```

위 코드에서는 `MyThread-1`, `MyThread-2` 스레드로 구성된 스레드풀을 사용하는  CoroutineDispatcher를 사용해 launch 코루틴을 실행하도록 되어있고 실제로 `일시 중단` 지점과 `실행 재개` 지점에서 사용하는 Thread가 다른 것을 확인할 수 있습니다,


### 2. 스레드를 양보하지 않으면 실행 스레드가 바뀌지 않는다
코루틴의 실행 스레드가 바뀌는 시점은 코루틴이 재개될 때입니다. 즉, 코루틴이 스레드 양보를 하지 않아 일시 중단될 일이 없다면 실행 스레드는 바뀌지 않습니다.

```kotlin
fun main() = runBlocking<Unit> {
    val dispatcher = newFixedThreadPoolContext(2, "MyThread")
    launch(dispatcher) {
        repeat(5) {
            println("[${Thread.currentThread().name}] 코루틴 실행이 일시 중단 됩니다")
            Thread.sleep(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행이 재개 됩니다")
        }
    }
}

/** 결과:
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
 */
```

위 코드에서는 일시 중단 함수인 `delay`가 아닌 스레드 블록킹을 하는 `Thread.sleep` 함수를 사용했습니다. 결과를 보면 모두 같은 `Thread-1` 스레드를 사용하는 것을 볼 수 있습니다. 모두 같은 스레드에서 실행 되는 이유는 `Thread.sleep` 함수는 코루틴이 스레드를 양보하지 않고 블로킹 시키도록 만들기 떄문입니다. 즉, 코루틴에 재개 시점이 없어 CoroutineDispatcher 객체가 코루틴 작업을 다시 스레드에 할당할 일이 없으므로 처음 할당된 스레드에서 계속 실행 되는 것 입니다. 