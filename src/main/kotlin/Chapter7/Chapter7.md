> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

## 7장 - 구조화된 동시성

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

### 실행 환경 상속
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