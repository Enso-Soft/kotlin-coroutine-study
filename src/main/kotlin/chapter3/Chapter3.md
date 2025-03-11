> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

## 3장 - CoroutineDispatcher
### CoroutineDispatcher 란?
_Dispatcher 란 무엇일까요?_
**Dispatch** : 보내다
뜻에 -er 가 붙어서 '무언가를 보내는 주체' 라는 뜻 입니다.

여기에서 Coroutine 이 붙는다면 **코루틴을 보내주는 주체** 가 됩니다.
코루틴을 어디로 보내는 것 일까? 바로 스레드 입니다.

코루틴 일시 중단이 가능한 **작업** 이기 때문에 어떤 스레드에서 **작업**을 실행하지 결정하는 역할을 하게 하는 것이 CoroutineDispatcher 입니다.

### CoroutineDispatcher 동작
코루틴을 실행하기 위해서 2개의 스레드를 구성된 스레드풀을 사용할 수 있는 CoroutineDispatcher 객체를 가정하여, 그림을 그린다면 아래와 같습니다.
![](https://velog.velcdn.com/images/tien/post/76ab272e-95ba-4ae8-b65d-4b4306f8d22c/image.png)

CoroutineDispatcher 객체는 실행돼야 하는 작업을 저장하는 **작업 대기열**을 가집니다.
CoroutineDispatcher 객체는 사용할 수 있는 **스레드풀**에는 Thread-1, Thread-2 라는 스레드가 포함되어있습니다.

![](https://velog.velcdn.com/images/tien/post/41d46b5f-84db-4cf6-809c-12d857963f15/image.png)

CoroutineDispatcher 객체에 Coroutine1 작업 요청이 온다면

![](https://velog.velcdn.com/images/tien/post/22322c56-2f10-40a6-8d8e-53dde1ff9c21/image.png)

작업 대기열로 적재되고, CoroutineDispatcher 객체는 스레드풀에서 사용할 수 있는 스레드가 있는지 확인하는 작업을 진행하고

![](https://velog.velcdn.com/images/tien/post/0d82eb46-e505-4ec9-b3bf-84c49fdfa698/image.png)

Thread-1 스레드를 사용할 수 있으므로 CoroutineDispatcher 작업 대기열에 적재중인 Coroutine1 은 해당 스레드로 보내 실행하게 됩니다.

이와 같은 동작성으로 동일 CoroutineDispatcher 객체에 여러개의 Coroutine을 요청하게 된다면 사용 가능한 스레드풀에서 사용할 수 있는 스레드를 찾아서 코루틴을 적절히 실행할 수 있게 됩니다.

### CoroutineDispatcher 역할
정리하자면 CoroutineDispatcher는 코루틴의 실행을 관리하는 주체로 자신에게 실행 요청된 코루틴들을 작업 대기열에 적재하고, 자신이 사용할 수 있는 스레드가 새로운 작업을 실행할 수 있는 상태라면 스레드로 코루틴을 보내 실행될 수 있게 만드는 역할을 합니다.
> _**[코틀린 코루틴의 정석 책]** 발췌_
CoroutineDispatcher 객체에 코루틴의 실행이 요청되면 일반적으로는 작업 대기열에 적재한 후에 스레드로 보낸다. 하지만 코루틴의 실행 옵션에 따라 작업 대기열에 적재되지 않고, 즉시 실행될 수도 있고, 작업 대기열이 없는 CoroutineDispatcher 구현체도 있다. 이는 매우 예외적인 경우이다.

### 제한된 디스패처 / 무제한 디스패처
지금까지 그림으로 설명한 CoroutineDispatcher 의 경우 사용할 수 있는 스레드풀을 제한 한경우로 **제한된 디스패처**로 CoroutineDispatcher 객체가 어떤 작업을 처리할지 미리 역할을 부여하고 역할에 맞춰 요청에 대한 실행을 합니다.

하지만 사용할 수 있는 스레드가 제한 되지 않은 CoroutineDispatcher 객체도 존재하며, 이를 **무제한 디스패처**라 부릅니다. 실행할 수 있는 스레드가 제한되지 않았다고 해서 자유로운 스레드 사용이 가능하여 무제한 디스패처라 부르는것이 아닌 실행 요청된 코루틴이 이전 코드가 실행 되던 스레드에서 계속해서 실행되도록 하는 것입니다.

현재 내용에는 제한된 디스패처만을 다루는 내용을 작성할 예정입니다

### 제한된 디스패처 생성 코드로 보기
이제 Kotlin 코드로 실제 제한된 디스패처를 생성 해보록하겠습니다.
위에서 설명한 그림을 계속해서 생각해주시고, 코드에 적용해서 보시면 더욱 이해하기 쉽습니다!

#### 단일 스레드 디스패처
``` kotlin
fun main() = runBlocking {
    val singleDispatcher: CoroutineDispatcher = newSingleThreadContext(
    	name = "Single Thread"
    )
    
	...
}
```
위와 같이 newSingleThreadContext 함수를 사용해서 단일 스레드 디스패처를 만들 수 있습니다.
생성한 CoroutineDispatcher 객체의 스레드풀은 하나의 스레드만 사용할 수 있으며, 이름은 "Single Thread" 입니다.

```kotlin
repeat(10) {
	launch(context = singleDispatcher) {
		println("[${Thread.currentThread().name}] 실행")
	}
}
```
생성한 CoroutineDispatcher 를 사용하여 코루틴을 실행한다면 아래와 같은 결과를 얻을 수 있습니다.

> **[결과]**
[Single Thread @coroutine#2] 실행
[Single Thread @coroutine#3] 실행
[Single Thread @coroutine#4] 실행
[Single Thread @coroutine#5] 실행
[Single Thread @coroutine#6] 실행
[Single Thread @coroutine#7] 실행
[Single Thread @coroutine#8] 실행
[Single Thread @coroutine#9] 실행
[Single Thread @coroutine#10] 실행
[Single Thread @coroutine#11] 실행

Single Thread 만을 이용해서 코루틴을 실행하는 것을 확인할 수 있습니다.

#### 멀티 스레드 디스패처
```kotlin
fun main() = runBlocking {
    val multiDispatcher: CoroutineDispatcher = newFixedThreadPoolContext(
        nThreads = 2,
        name = "Multi Thread"
    )
    
    ...
}
```
newFixedThreadPoolContext 함수를 사용하여, 멀티 스레드 디스패처를 생성할 수 있습니다.
생성한 CoroutineDispatcher 객체의 스레드풀은 2개의 스레드를 사용할 수 있으며, 이름은 "Multi Thread" 입니다.

```kotlin
repeat(10) {
	launch(context = multiDispatcher) {
		println("[${Thread.currentThread().name}] 실행")
	}
}
```
생성한 CoroutineDispatcher 를 사용하여 코루틴을 실행한다면 아래와 같은 결과를 얻을 수 있습니다.

> **[결과]**
[Multi Thread-2 @coroutine#3] 실행
[Multi Thread-1 @coroutine#2] 실행
[Multi Thread-1 @coroutine#4] 실행
[Multi Thread-1 @coroutine#5] 실행
[Multi Thread-2 @coroutine#6] 실행
[Multi Thread-1 @coroutine#7] 실행
[Multi Thread-2 @coroutine#8] 실행
[Multi Thread-1 @coroutine#9] 실행
[Multi Thread-2 @coroutine#10] 실행
[Multi Thread-1 @coroutine#11] 실행

Multi Thread-1 / Multi Thread-2 두개의 스레드를 사용하여 코루틴을 실행한 결과를 얻을 수 있습니다.

_**여기서 잠깐**_
newSingleThreadContext와 newFixedThreadPoolContext 함수를 보면 스레드 갯수가 1개인 것과 N개 인것을 확인할 수 있는데요. 실제 함수 구현부쪽을 들여다보면
![](https://velog.velcdn.com/images/tien/post/16265830-380f-4cd5-9a3a-1de48c36852c/image.png)

![](https://velog.velcdn.com/images/tien/post/8a803893-ad5f-4a21-80e6-bf962beb3853/image.png)

newSingleThreadContext 함수는 내부적으로 nThreads = 1로 가진 newFixedThreadPoolContext 를 호출하는 것으로 볼 수 있습니다.

결국 같은 두 함수 모두 같은 함수로 사용함을 볼 수 있습니다.

그리고 newFixedThreadPoolContext 함수를 보면 **isDaemon = true** 로 되어있는걸 보면 Coroutine 에서 사용하는 스레드는 작동중이더라도 Process 가 종료 될 수 있음을 볼 수 있습니다. 이부분에 대해서는 다음 파트에서 바로 설명 드리겠습니다.

여기에서 개발적인 궁금증이 들었습니다.
왜 **OverLoading** 을 사용하지 않고 함수 이름을 다르게 했을까?
주관적인 생각에서는 이 함수를 사용하는 사람이 정말로 주의해서 사용을 해주었으면 좋겠다 라는 의도를 품은것이 아닐까 생각하고 있습니다.

그런데 newFixedThreadPoolContext 함수에서 이와 같은 경고가 출력 되고 있었습니다.
> This is a delicate API. The result of this method is a closeable resource with the associated native resources (threads). It should not be allocated in place, should be closed at the end of its lifecycle, and has non-trivial memory and CPU footprint. If you do not need a separate thread-pool, but only have to limit effective parallelism of the dispatcher, it is recommended to use CoroutineDispatcher.limitedParallelism instead.
>
_이 API는 신중하게 다뤄야 합니다. 이 메서드의 결과는 closeable한 리소스이며, 관련된 네이티브 리소스(예: 스레드)를 포함하고 있습니다.
따라서 즉석에서 할당하면 안 되며,
수명 주기가 끝날 때 반드시 닫아야 합니다.
또한, 비효율적인 메모리 및 CPU 사용 가능성이 높으므로 주의가 필요합니다.
만약 별도의 스레드 풀을 생성할 필요가 없고, 단순히 디스패처의 동시 실행 수준(병렬성)을 제한하려는 경우,
👉 CoroutineDispatcher.limitedParallelism을 사용하는 것이 권장됩니다._

이러한 경고를 하는 이유는 개발자가 직접 디스패처 객체를 만드는 행위는 비효율적일 가능성이 높기 때문입니다. 제대로 관리하지 않는다면 이미 만든 CoroutineDispatcher 객체는 메모리에 게속해서 존재할 것이며, 이를 잘못 다를 경우 계속해서 객체를 만들어서 리소스 낭비를 할 수 있다라는 것입니다.

> _이 때문에 함수 설계부터 의도를 품고 있는것이 느껴져서 다시 한번 좋은 개발은 어떻게 해야하는지에 대해서 배우는 계기가 되었습니다._

다시 원래 내용을 이어서 설명하자면 코루틴 라이브러리는 개발자가 직접 CoroutineDispatcher 객체를 생성하는 문제를 방지하기 위해 미리 정의된 CoroutineDispatcher 목록을 제공합니다.

### Thread 에서 isDaemon 이란?
JVM 프로세스는 일반적으로 메인 스레드의 작업이 종료되면 종료된다.
하지만 새로운 스레드를 추가적으로 만든다면 메인 스레드의 작업이 완료 될 때 종료되는것이 아닌 새로운 스레드의 작업까지 완료 되어야지만 프로세스가 종료 됩니다.

JVM 에서 스레드는 두 종류로 나눌 수 있는데, 이를 **사용자 스레드**와 **데몬 스레드**로 구분합니다.
**사용자 스레드** : 우선도가 높은 스레드
**데몬 스레드** : 우선도가 낮은 스레드

새로운 스레드를 사용할 때 아래와 같이 지정 해줄 수 있다.
```kotlin
NewThread().apply() {
	isDaemon = true op false
}
```

### Dispatchers.IO
> _Dispatcher.IO_ 에서 사용 가능한 스레드의 수는 JVM에서 사용이 가능한 프로세서의 수와 64개 중 큰 값을 설정 됩니다.
>
예를들어 JVM에서 사용이 가능한 프로세서의 수가 24개 일 경우에는 64개의 스레드를 사용할 수 있게 설정 됩니다.
다른 상황으로 사용이 가능한 프로세서의 수가 100개 일 경우에는 100개의 스레드를 사용할 수 있도록 설정이 됩니다.

네트워크 통신을 하거나 DB 작업 같은 입출력 작업 여러 개를 동시에 수행하기 위해서 많은 스레드가 필요하다. 이를 위해 코루틴 라이브러리에서 미리 정된 Dispatcher.IO 를 제공합니다.

![](https://velog.velcdn.com/images/tien/post/9194f34b-6a74-4ed3-a261-2808e3d0859a/image.png)

![](https://velog.velcdn.com/images/tien/post/6bd38756-daaf-43db-abb6-9d6b25787214/image.png)

Dispatchers.IO는 싱글톤 인스턴스 이므로 다음과 같이 launch 함수 인자로 사용할 수 있습니다.
```kotlin
fun main() = runBlocking<Unit> {
    launch(context = Dispatchers.IO) {
        println("[${Thread.currentThread().name}] IO 실행")
    }
}
```

### Dispatchers.Default
> _Dispatchers.Default_ 에서 사용 가능한 스레드의 수는 디바이스의 CPU 코어의 수와 2개 중 큰 값으로 설정 됩니다.
>
예를 들어 CPU 코어가 8개인 환경에서는 8개의 스레드를 사용할 수 있으며, 1개인 환경에서는 2개의 스레드를 사용할 수 있습니다.


대용량 데이터를 처리해야 하는 작업 처럼 CPU 연산이 필요한 작업이 있으며, 이를 CPU 바운드 작업이라고 합니다. Dispatchers.Default는 CPU 바운드 작업이 필요할때 사용하는 CoroutineDispatcher 입니다.

Dispatchers.Default는 싱글톤 인스턴스 이므로 다음과 같이 launch 함수 인자로 사용할 수 있습니다.
```kotlin
fun main() = runBlocking<Unit> {
    launch(context = Dispatchers.Default) {
        println("[${Thread.currentThread().name}] Default 실행")
    }
}
```

### Dispatchers.Main
Dispatchers.IO나 Dispatchers.Default와 달리 UI가 있는 애플리케이션에서 메인 스레드의 사용을 위해 사용되는 CoroutineDispatcher 객체 입니다. 이 객체를 사용하기 위해서는 별도의 라이브러리를 추가하여 (ex kotlinx-coreoutines-android 등) 을 추가해야야지만 Dispatchers.Main 구현체를 제공하기 때문에 사용이 가능하다.

### limitedParallelism 사용해서 CoroutineDispatcher 스레드 제한
Dispatchers.Default 를 사용해서 무겁고 오래 걸리는 작업을 수행한다면 이를 위해 CoroutineDispatcher의 모든 스레드를 사용할 수 있습니다. 그렇게 된다면 새로운 작업은 작업 대기열에 묶여있는 상태가 되어버리게 되버립니다.

이를 해결하기 위해서 CorountineDispatcher 객체의 limitedParallelism 함수를 사용하여 스레드의 수를 제한할 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    launch(context = Dispatchers.Default.limitedParallelism(2)) {
        repeat(10) {
            launch {
                println("[${Thread.currentThread().name}] 실행")
            }
        }
    }
}
```
>**[결과]**
[DefaultDispatcher-worker-1 @coroutine#4] 실행
[DefaultDispatcher-worker-2 @coroutine#3] 실행
...
[DefaultDispatcher-worker-1 @coroutine#11] 실행
[DefaultDispatcher-worker-2 @coroutine#12] 실행

2개의 스레드만을 사용해서 Dispatchers.Default 작업을 수행하는것을 볼 수 있습니다.

### 공유 스레드풀 이란?

![](https://velog.velcdn.com/images/tien/post/9f45baed-d9f6-4e1a-8b4a-6bb6264bd210/image.png)

Dispatchers.IO와 Dispatchers.Default에서 코드를 실행해보면 동일한 스레드 이름을 출력하는 것을 볼 수 있습니다.

**DefaultDispatcher-worker** 스레드 이름으로 같은 스레드풀을 사용 하는데 이는 코루틴 라이브러리에서 IO와 Default 가 공유 스레드풀을 사용하는 것이기 때문입니다.

만약 여기에서 **limitedParallelism**을 사용한다면 어떻게 될까요?
- **Dispatchers.IO**
    - 공유 스레드풀의 스레드로 구성된 새로운 스레드풀을 만들어내며, 스레드 수의 제한이 없습니다.

- **Dispatchers.Default**
    - 공유 스레드풀에서 사용 가능한 스레드 중 일부를 사용하도록 합니다. 이때 설정한 스레드는 Default에서 사용 가능한 수를 가져오기 때문에 신중히 사용 해야합니다.

여기에서 우리가 알아야할 점은 newFixedThreadPoolContext로 만들어지는 <CoroutineDispatcher 는 자신만이 사용 가능한 스레드풀을 생성하는 것과 다르게 Dispatchers.IO와 Dispatchers.Default 는 같은 공유 스레드풀을 사용한다는 것을 확실하게 알고 사용해야합니다.

---

### CoroutineDispatcher 면접 CS 예상 질문
- **CoroutineDispatcher는 어떤 역할을 하는가?**
    - 코루틴을 어떤 스레드에서 작동시키게 할건지를 결정시키는 역할을 하고 있습니다.

- **CoroutineDispatcher가 가지는 스레드 풀의 모든 스레드가 코루틴을 실행하고 있을 때 새로운 코루틴이 생성되어 요청이 되면 어떻게 동작하는가?**
    - 작업 대기열에 해당 코루틴을 적재하고 있다가 사용 가능한 스레드가 존재할 때 해당 스레드에 코루틴을 보내어 실행하게 됩니다.

- **Dispatchers.IO와 Dispatchers.Default는 각각 어떤 역할에 적합한 CoroutineDispatcher 인가?**
    - Dispatchers.IO : Network, DB 작업
    - Dispatchers.Default : CPU 집약 작업

- **Dispatchers.IO와 Dispatchers.Default는 각각 몇개의 스레드를 가지고 있는가?**
    - Dispatchers.IO : 64 or JVM 프로세서 수 중 큰 값을 사용합니다.
    - Dispatchers.Default : 2 or 코어 갯수 중 큰 값을 사용합니다.

- **Dispatchers.Default의 스레드 개수가 정해진 이유가 어떤것인가?**
    - 코루틴은 중단 가능한 작업으로 작업을 중단하고 재개할 때 스레드의 변경인 컨텍스트 스위칭이 발생할 수 있습니다. 이를 큰 리소스를 소모할 수 있기 때문에 스레드 개수를 CPU 코어 만큼으로 설정한 것입니다. 추가적으로 CPU 코어는 하나마다 스레드 하나를 돌릴 수 있습니다.

- **Dispatchers.IO와 Dispatchers.Default의 limitedParallelism() 함수는 각각 어떤 스레드풀을 만드는가?**
    - 공유 스레드풀에서 새로운 스레드 풀을 만들어서 사용합니다.
        - Default 는 Default 내에서 만들어지고
        - IO는 64개가 있지만 새로운 Pool을 만든다.


- **Dispatchers.Main의 구현체는 어디에 있고, 어떻게 가져와서 사용하는가?**
    - kotlinx.coroutines.android 에서 구현하며, AndroidDispatcherFactory 를 통해 Dispatcher.Main 이 초기화 됩니다.
        - kotlinx.coroutines.android.AndroidDispatcherFactory
        - kotlinx.coroutines.test.internal.TestMainDispatcherFactory
          위 두개의 Class로 부터 구현체를 받습니다.

- **Dispatchers.Main.immediate는 어떤 것인가?**
    - 코루틴을 사용하는 스레드가 이미 Main 이라면 코루틴을 Dispatcher 에 보내는 작업, 즉 작업 대기열에 적재하는 행위를 하지 않고 바로 실행할 수 있는 행동을 하게 해줍니다.

## 4장

- Job은 어떤 역할을 하는가?
  코루틴의 생명주기를 관리하는 객체 입니다. 또한 상태를 지정할 수 있습니다.

- Job.cancel()함수를 호출 하면 코루틴이 즉시 취소 되는가? 언제 취소 되는가?
  즉시 취소되지 않으며, 확인 작업을 거치는 스레드 전환 과정에서 cancel flag를 확인하여 취소 된다.

- Job의 상태 변수는 어떤 것들이 있는가?
  Activity / Cancelled  / Completed

- Job이 취소 완료된 것을 확인 하기 위해서 Job의 어떤 값을 확인해야하는가?
  isCancelled와 isCOmpleted가 true 일 경우

withContext 뭐가 달라?  