> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

---

# 8장 - 예외처리
> 애플리케이션은 여러 예외(Exception)에 노출됩니다. 이러한 예외를 적절히 처리하지 못한다면 애플리케이션이 예측하지 못한 방향으로 동작 하거나 심하게는 비정상 종료될 수 있습니다. 따라서 안정적인 애플리케이션을 위해서는 예외를 적절하게 처리하는 것이 중요합니다.<br><br>비동기 작업을 수행하는 코루틴의 예외 처리 또한 중요합니다. 특히 코루틴의 비동기 작업은 `네트워크 요청`, `데이터 베이스` 같은 입출력(IO) 작업을 수행하는데 쓰이는 경우가 많아 예측할 수 없는 예외가 발생할 가능성이 높으므로 코루틴에 대한 적절한 예외 처리는 안정적인 애플리케이션을 만드는 데 필수적입니다.

## 8-1. 코루틴의 예외 전파
### 1. 코루틴에서 예외가 전파되는 방식
코루틴 실행 도중 예외가 발생하면 예외가 발생한 코루틴은 취소되고 부모 코루틴으로 예외가 전팝니다. 만약 부모 코루틴에서도 예외가 적절히 처리되지 않으면 부모 코루틴도 취소되고 예외는 다시 상위 코루틴으로 전파 됩니다. 이것이 반복 되면 최상위 코루틴인 루트 코루틴까지 예외가 전파 될 수 있습니다.

코루틴이 예외를 전파 받아서 취소가 된다면 하위(자식) 코루틴에게 취소가 전파 됩니다. 따라서 예외가 적절히 처리되지 않아서 루트 코루틴까지 예외가 전파 된다면 하위의 모든 코루틴에 취소가 전파 됩니다.

![](https://velog.velcdn.com/images/tien/post/81d544b2-252c-479c-8698-fcb16cdfda0c/image.png)

위 그림과 같이 구조화된 코루틴 있을 때 예외가 전파 되는 방식을 설명 해보겠습니다.

![](https://velog.velcdn.com/images/tien/post/8da3f5f2-d299-4c89-a618-1704599901c7/image.png)

만약 Coroutine5 코루틴에서 예외가 발생하면 Coroutine2 코루틴으로 예외가 전파 되며, Coroutine2 코루틴에서 예외가 처리되지 않으면 Coroutine1 코루틴까지 예외가 전파됩니다. 만약 Coroutine1 코루틴에서도 예외가 적절히 처리되지 않으면 Coroutine1 코루틴은 취소 됩니다.

![](https://velog.velcdn.com/images/tien/post/f3e3f0e1-af41-42aa-80c0-d4c01bba2fcd/image.png)

코루틴이 취소 되면 자식 코루틴에게도 취소가 전파 되므로, 코루틴의 예외를 제대로 막지 못하여 루트 코루틴이 취소되면 구조화된 코루틴 모두 취소 될 수 있습니다.

### 2. 예제로 알아보는 예외 전파
