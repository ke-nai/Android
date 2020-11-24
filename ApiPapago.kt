import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder


open class ApiPapago(var query: String, var source: String, var target: String) : MyAsyncTask<Void, Void, String?>() {
    override fun onPreExecute() {}

    override fun doInBackground(arg: Void?): String? {
        var result = ""
        return try {
            val data = "source=$source&target=$target&text=" + URLEncoder.encode(query, "UTF-8")
                    .replace("\\+".toRegex(), "%20").replace("%0A".toRegex(), "%5Cn")
                    .replace("%5", "\\").replace("%22", "\"")

            result = post(url, headers, data)
            JSONObject(result).getJSONObject("message").getJSONObject("result").getString("translatedText")
        } catch (e: JSONException) {
            e.printStackTrace()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onProgressUpdate(progress: Void?) {}
    override fun onPostExecute(result: String?) {}
    override fun onCancelled(result: String?) {}

    companion object {
        const val url = "https://openapi.naver.com/v1/papago/n2mt"
        val headers = hashMapOf<String, String>(
                "X-Naver-Client-Id" to  "-----", //애플리케이션 클라이언트 아이디값
                "X-Naver-Client-Secret" to "-----" //애플리케이션 클라이언트 시크릿값
        )
    }

}