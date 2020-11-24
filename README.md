# Android
## HttpConnect 클래스
간단한 String 형태로 데이터를 주고 받기 좋게 작성한 httpconnect 라이브러리
### 이용 방법
#### 1.POST

```
val url = "" // 연결할 URL 주소 준비
val data = "" // 전송할 데이터 준비

val headers: MutableMap<String, String> = HashMap() // 헤더 정보 준비
headers["device-type"] = "pc"
headers["User-Agent"] = "Mozilla/5.0"

val result = HttpConnect.post(url, headers, data) // 연결하고 값 받아오기
```
연결한 URL 주소, 전송할 데이터, 헤더 정보를 준비한 뒤

post 함수를 이용해서 값을 송수신함.

#### 2.GET
```
val url = "" // 연결할 URL 주소 준비

val headers: MutableMap<String, String> = HashMap() // 헤더 정보 준비
headers["device-type"] = "pc"
headers["User-Agent"] = "Mozilla/5.0"

val result = HttpConnect.post(url, headers) // 연결하고 값 받아오기
```
POST에서 전송할 데이터를 제외함.

## AsyncTask 대체용 추상 클래스 MyAsyncTask
AsyncTaskr가 deprecated 되면서 비슷한 용도로 사용하기 위해 만든 추상 클래스

### 이용 방법

이용 방법은 AsyncTask와 동일하므로 자세한 설명은 생략함.

excute()로 실행하면 먼저 onPreExcute()가 실행되고

doInBackground()내에서 publishProgress()를 이용하면

onProgressUpdate()가 실행된다.

중간에 cancel()을 호출할 수 있고

doInBackground()내에서 isCancelled()를 이용해서 알아서 멈춰야한다.

마지막으로 도중에 cancel 됐으면 onCancelled()가

정상 종료되면 onPostExcute()가 실행된다.

여기서 doInBackground()를제외한 함수는 모두 메인 UI 스레드에서 실행된다.
