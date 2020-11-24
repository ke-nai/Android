import android.media.AudioManager
import android.media.MediaPlayer
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object HttpConnect {
    fun post(url: String, headers: Map<String, String>, data: String?): String {
        val con = URL(url).openConnection() as HttpURLConnection
        con.requestMethod = "POST"

        for ((key, value) in headers)
            con.setRequestProperty(key, value)

        con.doOutput = true
        DataOutputStream(con.outputStream).use { wr ->
            wr.writeBytes(data)
            wr.flush()
        }

        return readBody( if(con.responseCode == HttpURLConnection.HTTP_OK)
            con.inputStream else con.errorStream)
    }

    operator fun get(url: String, headers: Map<String, String>): String {
        val con = URL(url).openConnection() as HttpURLConnection
        con.requestMethod = "GET"

        for ((key, value) in headers)
            con.setRequestProperty(key, value)

        return readBody( if(con.responseCode == HttpURLConnection.HTTP_OK)
            con.inputStream else con.errorStream)
    }
    
    private fun readBody(body: InputStream): String {
        val lineReader = BufferedReader(InputStreamReader(body))
        val responseBody = StringBuilder()
        var line: String?
        while (lineReader.readLine().also { line = it } != null) {
            responseBody.append(line)
        }
        return responseBody.toString()
    }
}