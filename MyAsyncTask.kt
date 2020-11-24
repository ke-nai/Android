import android.os.Handler
import android.os.Looper

abstract class MyAsyncTask<T1, T2, T3> {
    private var cancelled = false

    fun excute(args: T1?) {
        onPreExecute()
        object : Thread() {
            override fun run() {
                val result = doInBackground(args)
                if (cancelled) cancelRun(result) else postRun(result)
            }
        }.start()
    }

    fun excute() {
        onPreExecute()
        object : Thread() {
            override fun run() {
                val result = doInBackground(null)
                if (cancelled) cancelRun(result) else postRun(result)
            }
        }.start()
    }


    fun cancel() {
        cancelled = true
    }

    protected fun isCancelled(): Boolean {
        return cancelled
    }

    private fun publishProgress(progress: T2?) {
        Handler(Looper.getMainLooper()).post { onProgressUpdate(progress) }
    }

    private fun postRun(result: T3?) {
        Handler(Looper.getMainLooper()).post { onPostExecute(result) }
    }

    private fun cancelRun(result: T3?) {
        Handler(Looper.getMainLooper()).post { onCancelled(result) }
    }

    protected abstract fun onPreExecute()
    protected abstract fun doInBackground(arg: T1?): T3?
    protected abstract fun onProgressUpdate(progress: T2?)
    protected abstract fun onPostExecute(result: T3?)
    protected abstract fun onCancelled(result: T3?)
}