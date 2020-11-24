# Android
## HttpConnect 클래스
간단한 String 형태로 데이터를 주고 받기 좋게 작성한 httpconnect 라이브러리

<details>
  
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

</details>

## AsyncTask 대체용 > MyAsyncTask 추상 클래스 
AsyncTaskr가 deprecated 되면서 비슷한 용도로 사용하기 위해 만든 추상 클래스

<details>

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

</details>

## 파파고 API 사용 예제 > ApiPapago 클래스
파파고 API를 간단한 용도로 사용하기 위해 만든 클래스

API 신청 및 Key 입력 과정은 생략함

<details>
  
### 이용 방법
```
btn_trans.setOnClickListener {
            object : ApiPapago("번역할 문장", "ko", "en") {
                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)

                    textView.text = result
                }
            }.excute()
        }
```
위와 같이 함수를 작성하고 번역할 문장, 원본 언어, 목적 언어를 입력하고

onPostExcute 함수 내에서 결괏값을 어떻게 사용할 것인지 작성하면 된다.

</details>


## Activity 외부에서 context 사용

<details>
  
### 개요
Activity 외부에서 getString을 사용하고 싶은데 context가 없어서 안된다.

매번 context를 넘겨주거나 해서 companion object에 있는 값을 바꿔주는 것도 귀찮은 일인데

검색해보니까 좋은 방식이 있어서 나한테 필요 없는 부분은 빼고 코틀린 버전으로 만들었다.

### 이용 방법
```
import android.app.Application
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: Context
    }
}
```
이렇게 App이라는 class를 하나 만들어주고
```
...
    <application
        ...
        android:name=".App">
    ...
```
manifest 파일에서 application 하위에 android:name=".App" 를 추가해 준 뒤
```
App.context.getString(R.string.~~~)
```
코드 아무 곳에서나 이런 방식으로 꺼내 쓰면 된다.

좀 야매로 해결하는 거 같지만 일단은 유용하게 써먹을 수 있다.

</details>

## 클립보드에 텍스트 복사, 붙여넣기

<details>
  
클립보드에 텍스트 복사, 붙여넣기를 하기 위해 코드를 찾아서 좀 변형했는데

설명할만큼 아는 거 같지는 않아서 설명은 생략함


```
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context

object Clipboard {
    fun copy(copyData: String?) {
        val context = App.context

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("simple text", copyData))

        //Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    fun paste(): String {
        val clipboard = App.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var pasteData: String = ""
        if (!clipboard.hasPrimaryClip()) {

        } else if (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)!!) {
            // This disables the paste menu item, since the clipboard has data but it is not plain text
        } else {
            // This enables the paste menu item, since the clipboard contains plain text.
            val clip = clipboard.primaryClip
            if (clip != null) {
                val item = clip.getItemAt(0)
                pasteData = item.text.toString()
            }
        }
        return pasteData
    }
}
```
copy랑 paste 함수에서 context를 인자로 받아도 되는데
[이 방식](#Activity-외부에서-context-사용)을 이용해서 좀 더 쓰기 편하게 했다.

```
Clipboard.copy("복사할 텍스트")
Clipboard.paste()
```
이런식으로 사용하면 된다.

</details>

## 코드 아무데서나 Toast 쓰기

<details>

코드 작성 중에 간단한 동작 확인을 위해 Toast를 쓸 때가 많은데

Toast는 메인 스레드에서 호출해야되고 context가 필요해서 귀찮다.

그래서 아무데서나 쓸 수 있는 형태로 만들었다.

```
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object Toaster {
    fun pop(message:String, duration:Int = Toast.LENGTH_SHORT){
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(App.context, message, duration).show()
        }
    }
}
```

[이 방식](#Activity-외부에서-context-사용)을 이용해서 context를 해결했다.

```
Toaster.pop("메세지", Toast.LENGTH_LONG)
Toaster.pop("메세지")
```
사용할 때는 이렇게

</details> 

## 앱에 아이콘 변경 기능 넣기
<details>

런처 액티비티를 여러개 만들고

액티비티의 활성화, 비활성화를 이용해서 아이콘 변경을 구현한다.

```
 <activity android:name=".MainActivity">
            <intent-filter>
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity-alias
            android:name=".MainActivity.a"
            android:label="app-a"
            android:enabled="true"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>

        <activity-alias
            android:name=".MainActivity.b"
            android:label="app-b"
            android:enabled="false"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>
```

먼저 manifest에 activity-alias를 이런식으로 넣어주고
```
        packageManager.apply {
            setComponentEnabledSetting(
                ComponentName(
                    "com.e.asdf",
                    "com.e.asdf.MainActivity.a"
                ), COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP
            )
            setComponentEnabledSetting(
                ComponentName(
                    "com.e.asdf",
                    "com.e.asdf.MainActivity.b"
                ), COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP
            )
        }
```
앱 내에서 이런 코드를 통해 바꿀 수 있다.

</details>
