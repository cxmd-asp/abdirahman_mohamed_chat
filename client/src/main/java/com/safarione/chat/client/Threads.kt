package com.safarione.chat.client

import android.os.Handler
import android.os.Looper
import android.view.View
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.*
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicBoolean

fun notify(lock: Any) {
    synchronized(lock) {
        (lock as Object).notify()
    }
}

val mainThread: Thread?
    get() {
        return try {
            Looper.getMainLooper().thread
        }
        catch (e: Throwable) {
            null //the method is not mocked
        }
    }

fun isMainThread() = mainThread == Thread.currentThread()

fun assertIsMainThread() {
    if (!isMainThread())
        throw IllegalStateException("Should be called from the main thread")
}

fun Handler.isForCurrentThread(): Boolean {
    return looper === Looper.myLooper()
}

private val mainThreadHandler: Handler? = try {
    Handler(Looper.getMainLooper())
}
catch (e: Throwable) {
    null //occurs only in test environments since android.os.Looper is not mocked.
}

/**
 * Runs the passed lambda on the main thread.
 * If this thread is the main thread then the lambda will be run with some delay.
 */
fun post(runnable: () -> Unit) {
    mainThreadHandler!!.post(runnable)
}

fun postDelayed(delay: Long, runnable: () -> Unit) {
    mainThreadHandler!!.postDelayed(runnable, delay)
}

/**
 * Runs the passed runnable on the ui thread while blocking this
 * thread until the runnable has finished running.
 * @return The return value of the lambda
 */
fun <T> postAndWait(runnable: () -> T): T {
    check(!isMainThread()) { "This function can't be called on the main thread" }
    return mainThreadHandler!!.postAndWait(runnable)
}

/**
 * Posts the specifieid [runnable] and blocks the current thread until it has finished running.
 */
fun <T> Handler.postAndWait(runnable: () -> T): T {
    check(!isForCurrentThread()) { "This function can't be called on the thread that the runnable will be posted to" }

    val done = AtomicBoolean(false)

    var result: T? = null
    val lock = Any()
    var throwable: Throwable? = null

    synchronized(lock) {
        post {
            try {
                result = runnable()
            }
            catch (e: Throwable) {
                throwable = e
            }
            finally {
                done.set(true)
                notify(lock)
            }
        }

        while (true) {
            (lock as Object).wait()
            if (done.get())
                break
        }
    }

    throwable?.let { throw InvocationTargetException(it) }

    return result as T
}

/**
 * Runs the passed lambda on the ui thread while blocking this
 * thread until the lambda has finished running
 * @return The return value of the lambda
 */
fun <T> View.postAndWait(runnable: () -> T): T {
    if (isMainThread())
        throw IllegalStateException("This function can't be called on the main thread")

    val done = AtomicBoolean(false)

    var result: T? = null
    val lock = Any()
    var throwable: Throwable? = null

    synchronized(lock) {
        post {
            try {
                result = runnable()
            }
            catch (e: Throwable) {
                throwable = e
            }
            finally {
                done.set(true)
                notify(lock)
            }
        }

        while (true) {
            (lock as Object).wait()
            if (done.get())
                break
        }
    }

    throwable?.let { throw InvocationTargetException(it) }

    return result as T
}

private val executor = object : ThreadPoolExecutor(10, 10, 0, MILLISECONDS, LinkedBlockingQueue()) {

    override fun afterExecute(r: Runnable, t: Throwable?) {
        if (t != null)
            throw t

        if (r is Future<*> && r.isDone) {
            try {
                r.get()
            }
            catch (e: CancellationException) {
            }
            catch (e: ExecutionException) {
                throw e.cause ?: e
            }
        }

    }
}

fun <T> submit(runnable: () -> T): Future<T> {
    return executor.submit(runnable)
}