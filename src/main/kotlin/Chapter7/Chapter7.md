> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

---

# 7장 - 구조화된 동시성

> 비동기 작업을 구조화함으로써 비동기 프로그래밍을 보다 안정적이고 예측할 수 있게 만드는 원칙입니다.<br>코루틴은 구조화된 동시성의 원칙을 사용해 비동기 작업인 코루틴을 부모-자식 관계로 구조화함으로써 보다 안전하게 관리되고 제어 될 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    launch { // 부모 코루틴
        launch { // 자식 코루틴
            println("자식 코루틴 실행")
        }
    }
}
``` 

코루틴을 부모-자식 관계로 구조화 하는 방법은 간단합니다.
부모 코루틴을 만드는 코루틴 빌더의 람다식 속에서 새로운 코루틴 빌더를 호출하면 됩니다.

![](https://velog.velcdn.com/images/tien/post/baa79d3a-efbc-429c-a002-9c0d019563fd/image.png)

그림과 같이 안쪽의 launch 함수가 호출돼 생성되는 코루틴은 바깥쪽의 launch로 생성되는 코루틴의 자식 코루틴이 되며, 바깥쪽 launch 함수가 호출돼 생성되는 코루틴은 runBlocking으로 생성되는 코루틴의 자식 코루틴이 됩니다.

>#### 구조화된 코루틴은 여러 특징을 갖는데 그 대표적인 특징은 아래와 같습니다.
>- 부모 코루틴의 실행 환경이 자식 코루틴에게 상속 된다.
>- 작업을 제어하는 데 사용된다.
>- 부모 코루틴은 자식 코루틴이 완료될 때까지 대기한다.
>- CoroutineScope를 사용해 코루틴이 실행되는 범위를 제한할 수 있다.

---

## 7-1. 실행 환경 상속
부모 코루틴은 자식 코루틴에게 실행 환경을 상속합니다.<br>부모 코루틴이 자식 코루틴을 생성 하면 부모 코루틴의 CoroutineContext가 자식 코루틴에게 전달 됩니다.

```kotlin
fun main() = runBlocking<Unit> {
    val coroutineContext = newSingleThreadContext("MyThread") + CoroutineName("CoroutineA")

    launch(coroutineContext) {
        println("[${Thread.currentThread().name}] 부모 코루틴 실행")
        launch {
            println("[${Thread.currentThread().name}] 자식 코루틴 실행")
        }
    }
}


/** 결과:
[MyThread @CoroutineA#2] 부모 코루틴 실행
[MyThread @CoroutineA#3] 자식 코루틴 실행
 */
```

이 코드에서 정의된 coroutineContext를 표로 본다면 아래와 같습니다.

| 키 | 값 |
| :---: | :---: |
| CoroutineDispatcher 키 | newSingleThreadContext("MyThread") |
| CoroutineName 키 | CoroutineName("CoroutineA") |

이 coroutineContext로 launch 코루틴 빌더 함수를 호출하여 생성한 코루틴과 내부에서 한번 더 launch 코루틴 빌더 함수를 호출해서 생성한 자식 코루틴은 같은 Thread와 CoroutineName을 가지는 것을 확인할 수 있습니다.

자식 코루틴은 단순히 launch 함수를 호출 했을뿐인데 같은 CoroutineContext를 갖는 이유는 부모 코루틴의 실행 환경을 담는 CoroutineContext 객체가 자식 코루틴에게 상속되기 때문입니다.


### 실행 환경 덮어씌우기
부모 코루틴의 모든 실행 환경이 항상 자식 코루틴에게 상속되지 않습니다.<br>만약 자식 코루틴을 생성하는 코루틴 빌더 함수로 새로운 CoroutineContext 객체가 전달 된다면 부모 코루틴에게서 전달 받은 CoroutineContext 구성 요소들은 자식 코루틴 빌더 함수로 전달된 CoroutineContext 객체의 구성 요소들로 덮어씌어집니다.

```kotlin
fun main() = runBlocking<Unit> {
    val coroutineContext = newSingleThreadContext("MyThread") + CoroutineName("ParentCoroutine")

    launch(coroutineContext) {
        println("[${Thread.currentThread().name}] 부모 코루틴 실행")
        launch(CoroutineName("ChildCoroutine")) {
            println("[${Thread.currentThread().name}] 자식 코루틴 실행")
        }
    }
}


/** 결과:
    [MyThread @ParentCoroutine#2] 부모 코루틴 실행
    [MyThread @ChildCoroutine#3] 자식 코루틴 실행
*/
```

이 코드에서 coroutineContext 객체는 newSingleThreadContext("MyThread") + CoroutineName("ParentCoroutine") 으로 구성 됩니다. 이때 바깥쪽 launch 코루틴 빌더 함수는 coroutineContext 객체를 사용해서 부모 코루틴을 생성하고, 안쪽 launch 코루틴 빌더 함수는 CoroutineName("ChildCoroutine")을 context 인자로 전달해 자식 코루틴을 생성합니다.

코드의 결과를 본다면 부모 코루틴과 자식 코루틴은 같은 CoroutineDispatcher 객체를 사용하지만 CoroutineName은 부모 코루틴은 ParentCoroutine이고, 자식 코루틴은 ChildCoroutine인 것을 확인할 수 있습니다. 자식 코루틴의 CoroutineContext 객체가 이렇게 만들어지는 이유를 알아보자!

![](https://velog.velcdn.com/images/tien/post/af9a33bc-098b-4e3e-901e-b6edf174a986/image.png)

부모 코루틴이 자식 코루틴을 생성할 때 부모 CoroutineContext + 자식 CoroutineContext 연산을 하게 됩니다.<br> 즉 부모 코루틴의 CoroutineContext 구성 요소에 자식 코루틴의 CoroutineContext 구성 요소를 덮어 씌우기를 하기 때문에 부모 코루틴에 이미 존재하는 CoroutineName("ParentCoroutine")을 자식 코루틴의 CoroutineName("ChildCoroutine")으로 대체한 CoroutineContext를 사용하여 자식 코루틴을 생성하기 때문입니다.

이처럼 자식 코루틴 빌더에 새로운 CoroutineContext 객체를 전달함으로써 부모 코루틴으로부터 전달된 CoroutineContext 객체의 구성요소를 재정의할 수 있습니다.

>실행 환경 상속 중 주의할 점은 다른 CoroutineContext 구성 요소들과 다르게 **Job 객체는 상속되지 않고 코루틴 빌더 함수가 호출되면 새롭게 생성된다는 것** 입니다.

### 상속되지 않는 Job
launch나 async를 포함한 모든 코루틴 빌더 함수는 호출 때마다 코루틴 추상체인 Job 객체를 새롭게 생성한다. 코루틴 제어에 Job 객체가 필요한데 Job 객체를 부모 코루틴으로부터 상속받게 되면 개별 코루틴 제어가 어려워지기 때문이다. 따라서 코루틴 빌더를 통해 생성된 코루틴들은 서로 다른  Job을 가진다.

```kotlin
fun main() = runBlocking<Uinit> { // 부모 코루틴 생성
 	/** 부모 코루틴의 CoroutineContext 부터 부모 코루틴의 Job 추출 */
	val runBlockingJob = coroutineContext[Job]
    
    launch { // 자식 코루틴 생성
    	/** 자식 코루틴의 CoroutineContext 부터 자식 코루틴의 Job 추출 */
    	val launchJob = coroutineContext[Job]
        
        if (runBlockingJob === launchJob) {
            println("runBlocking으로 생성된 Job과 launch로 생성된 Job이 동일합니다.")
        } else {
            println("runBlocking으로 생성된 Job과 launch로 생성된 Job이 다릅니다.")
        }
    }
}

/** 결과:
    runBlocking으로 생성된 Job과 launch로 생성된 Job이 다릅니다.
**/
```

코드의 실행 결과를 보면 runBlockingJob과 launchJob이 동일하지 않은 것을 확인할 수 있습니다. launch 코루틴이 runBlocking 코루틴으로부터 실행 환경을 상속받았음에도 서로 다른 Job 객체를 가집니다.

> 부모 코루틴의 Job 객체는 자식 코루틴의 Job 객체와 아무런 관계도 없는 것일까? 그렇지 않다.<br> **자식 코루틴이 부모 코루틴으로 부터 전달 받은 Job 객체는 코루틴을 구조화하는 데 사용된다.**

### 구조화에 사용되는 Job

![](https://velog.velcdn.com/images/tien/post/e6f21cdc-bdf0-4a9e-b9b6-55252d90263e/image.png)

코루틴 빌더가 호출되면 Job 객체는 새롭게 생성되지만 생성된 Job 객체는 위 그림과 같이 내부에 정의된 parent 프로퍼티를 통해 부모 코루틴의 Job 객체에 대한 참조를 가지게 됩니다. 또한 부모 코루틴의 Job 객체는 Sequence 타입의 Children 프로퍼티를 통해 자식 코루틴의 Job에 대한 참조를 가져 자식 코루틴의 Job 객체와 부모 코루틴의 Job 객체는 양방향 참조를 가진다.

| Job 프로퍼티 | 타입 | 설명 |
| :---: | :---: | :---: |
| parent | Job? | 코루틴은 부모 코루틴이 없을 수 있고, 부모 코루틴이 있더라도 최대 하나이다.|
| children | Sequence`<Job>` | 하나의 코루틴이 복수의 자식 코루틴을 가질 수 있다.|

>코루틴은 하나의 부모 코루틴만을 가질 수 있기 때문에 부모 코루틴의 Job 객체를 가리키는 parent 프로퍼티 타입은 Job? 입니다. 여기서 타입 뒤에 붙은 '?'에 주목해보자면 최상위에 있는 코루틴_(루트 코루틴)_ 은 부모 코루틴이 없을 수 있기 때문에 parent 프로퍼티는 null이 될 수 있는 타입인 Job?가 됩니다.

parent 프로퍼티와 children 프로퍼티가 어떤 객체를 참조하는지 다음 코드를 통해 확인 해보자!

```kotlin
fun main() = runBlocking<Uinit> { // 부모 코루틴 생성
 	/** 부모 코루틴의 CoroutineContext 부터 부모 코루틴의 Job 추출 */
	val parentJob = coroutineContext[Job]
    
    launch { // 자식 코루틴 생성
    	/** 자식 코루틴의 CoroutineContext 부터 자식 코루틴의 Job 추출 */
    	val childJob = coroutineContext[Job]
        
        println("1. 부모 코루틴과 자식 코루틴의 Job은 같은가? ${parentJob === childJob}")
        println("2. 자식 코루틴의 Job이 가지고 있는 parent는 부모 코루틴의 Job인가? ${childJob?.parent === parentJob}")
        println("3. 부모 코루틴의 Job은 자식 코루틴의 Job에 대한 참조를 가지는가? ${parentJob?.children?.contains(childJob)}")
    }
}

/** 결과:
	1. 부모 코루틴과 자식 코루틴의 Job은 같은가? false
    2. 자식 코루틴의 Job이 가지고 있는 parent는 부모 코루틴의 Job인가? true
    3. 부모 코루틴의 Job은 자식 코루틴의 Job에 대한 참조를 가지는가? true
**/
```

이를 통해 부모 코루틴과 자식 코루틴은 서로 다른 Job 객체를 가지며, 코루틴 빌더가 호출될 때마다 Job 객체가 새롭게 생성돼 상속되지 않는 것을 확인할 수 있습니다. 또한 자식 코루틴의 Job 객체는 parent 프로퍼티를 통해 부모 코루틴의 Job 객체에 대한 참조를 갖고, 부모 코루틴의 Job 객체 또한 children 프로퍼티를 통해 자식 코루틴의 Job 객체에 대한 참조를 갖는 것을 확인할 수 있습니다.

---

## 7-2. 코루틴의 구조화와 작업 제어

코루틴의 구조화는 하나의 큰 비동기 작업을 작은 비동기 작업으로 나눌 때 일어납니다.<br> 예를 들어 3개의 서버로부터 데이터를 다운로드하고, 그 후에 합쳐진 데이터를 변환하는 비동기 작업이 있다고 할 때

![](https://velog.velcdn.com/images/tien/post/76019d71-a1a0-4824-9390-d6c1219c5596/image.png)

위 그림과 같이 작은 비동기 작업으로 분할할 수 있습니다.

**여러 서버로부터 데이터를 다운로드 후 변환하는 작업**은 그자체로 하나의 큰 작업이 되며,<br> 하위에 **여러 서버로부터 데이터를 다운로드 하는 작업**과 **데이터를 변환하는 작업**을 포함하게 됩니다. 여기에서 '여러 서버로부터 데이터를 다운로드 하는 작업은 다시 **각 서버로부터 데이터를 다운로드하는 작업**들로 나뉘게 됩니다.

![](https://velog.velcdn.com/images/tien/post/a38c94d5-b45f-4298-bf82-c0ab35814120/image.png)

이러한 **작업**을 **코루틴**으로 바꾸면 코루틴의 구조화로 표현할 수 있습니다.<br>위 그림과 같이 코루틴의 구조화는 큰 작업을 연관된 작은 작업으로 분할하는 방식으로 이뤄집니다.

>코루틴을 구조화 하는 가장 중요한 이유는 코루틴을 안전하게 관리하고 제어하기 위함입니다.<br> 구조화된 코루틴은 안전하게 제어되기 위해 여러가지 특성을 갖는데 아래 두 가지 특성이 대표적입니다.
>- 코루틴으로 취소가 요청되면 자식 코루틴으로 전파된다.
>- 부모 코루틴은 모든 작식 코루틴이 실행 완료돼야 완료될 수 있다.

### 취소의 전파
코루틴은 자식 코루틴으로 취소를 전파하는 특성을 갖기 때문에 특정 코루틴이 취소되면 하위의 모든 코루틴이 취소됩니다.

![](https://velog.velcdn.com/images/tien/post/81720992-d5ac-4df3-b3e9-ddff84625dc8/image.png)

위 그림의 구조화된 코루틴을 설명하자면
- **Coroutine#1**
    - Coroutine#2 :: _(Coroutine#1 의 자식)_
        - Coroutine#5 :: _(Coroutine#2 의 자식)_
    - Coroutine#3 :: _(Coroutine#1 의 자식)_
        - Coroutine#6 :: _(Coroutine#3 의 자식)_
        - Coroutine#7 :: _(Coroutine#3 의 자식)_
    - Coroutine#4 _(Coroutine#1 의 자식)_
        - Coroutine#8 :: _(Coroutine#4 의 자식)_

코루틴은 부모 코루틴에 취소가 요청될 경우 자식 코루틴에 자동으로 취소가 전파됩니다.

![](https://velog.velcdn.com/images/tien/post/2da82167-bf05-4247-9141-0d3707f479a1/image.png)

만약 Coroutin#1 에 취소가 요청 된다면 Coroutine#1은 자식 코루틴인 Coroutine#2, Coroutine#3, Coroutine#4 에 취소를 전파하게 되며 이 코루틴 또한 각 자식 코루틴들에게 취소를 전파하게 됩니다.

그렇다면 Coroutine#1이 아닌 Coroutine#2에 취소가 요청되면 어떻게 될까?

![](https://velog.velcdn.com/images/tien/post/3b932585-c96e-473b-9897-d3caf7328f70/image.png)

Coroutine#2는 Coroutine#5만 자식으로 가지므로 Coroutine#5로만 취소가 전파 됩니다.

특정 코루틴에 취소가 요청되면 취소는 자식 코루틴 방향으로만 전파되며, 부모 코루틴으로는 취소가 전파되지 않습니다. 자식 코루틴으로만 취소가 전파되는 이유는 **자식 코루틴이 부모 코루틴 작업의 일부**이기 때문입니다.

취소의 전파를 상황을 가정하여 설명 해보겠습니다.
화면에 표시를 위해 3개의 데이터베이스로부터 데이터를 가져와 합치는 작업을 하는 코루틴이 있다고 가정해보자.

![](https://velog.velcdn.com/images/tien/post/d99873e5-d5af-4a5e-a6c1-02772e53fe3d/image.png)

위 그림과 같은 구조화된 코루틴을 작성할 수 있으며, 정상적으로 코루틴이 완료될 때와 작업 중간에 코루틴이 취소됐을 때를 코드로 표시하면 아래와 같습니다.

```kotlin
fun main() = runBlocking<Unit> {
    println("[3개의 데이터베이스로 부터 데이터를 가져와 실행]")
    searchDBJob().join()

    println("\n[3개의 데이터베이스로 부터 데이터를 가져와 실행중 취소]")
    searchDBJob().cancel()
}

fun CoroutineScope.searchDBJob(): Job = launch(Dispatchers.IO) {
    val dbResultDeferred: List<Deferred<String>> = listOf("db1", "db2", "db3").map {
        async {
            delay(1000L)
            println("${it}으로부터 데이터를 가져오는데 성공 했습니다.")
            return@async "[${it} data]"
        }
    }

    val dbResult: List<String> = dbResultDeferred.awaitAll()

    println(dbResult)
} 

/** 결과:
    [3개의 데이터베이스로 부터 데이터를 가져와 실행]
    db2으로부터 데이터를 가져오는데 성공 했습니다.
    db1으로부터 데이터를 가져오는데 성공 했습니다.
    db3으로부터 데이터를 가져오는데 성공 했습니다.
    [[db1 data], [db2 data], [db3 data]]

    [3개의 데이터베이스로 부터 데이터를 가져와 실행중 취소]
*/
```

첫 번째 **searchDBJob** 의 경우 join()을 호출하여, 작업이 완료 될때까지 runBlocking 코루틴을 일시중지 하였고<br> 두 번째 **searchDBJob** 의 경우 cacel()을 호출하여, 작업이 시작되고 취소를 요청 했습니다.

결과로는 첫 번째 코루틴의 경우 delay(1000L) 이후 데이터를 가져오는데 성공하며, 전체 데이터를 표시하는데 성공 하였습니다. 두 번쨰 코루틴의 경우 그 어떤 작업을 완료하지 못하고 아무런 데이터를 표시하지 못하고, 프로세스가 종료되는 것을 확인할 수 있습니다.

![](https://velog.velcdn.com/images/tien/post/7e2477da-cdc7-4fe8-86dd-966ed11dfa85/image.png)

위 그림과 같이 부모 코루틴에 취소가 요청 되었고, 자식 코루틴에게 취소가 전파되는 결과를 확인할 수 있습니다.

### 부모 코루틴의 자식 코루틴에 대한 완료 의존성
부모 코루틴은 모든 자식 코루틴이 실행 완료돼야 완료될 수 있습니다.<br> 코루틴의 구조화는 큰 작업을 연관된 여러 작은 작업으로 나누는 방식으로 이뤄지는데 작은 작업이 모두 완료돼야 큰 작업이 완료 될 수 있기 때문입니다. 이를 **부모 코루틴이 자식 코루틴에 대해 완료 의존성**을 가진다고 합니다.
