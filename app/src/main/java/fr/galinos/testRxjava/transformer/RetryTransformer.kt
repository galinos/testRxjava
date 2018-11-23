package fr.galinos.testRxjava.transformer

import android.util.Log
import fr.galinos.testRxjava.exception.RetryException
import fr.galinos.testRxjava.model.StatusResponse
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> Observable<T>.applySocketTimeoutRetry(): Observable<T> {
    val retryCount = 5
    return flatMap { response ->
        Log.i("DEBUG", "[RetryTransformer] applyRetry flatMap response : $response")

        if (response is StatusResponse && response.status != 0) {
            Log.e("DEBUG", "[RetryTransformer] applyRetry flatMap response status : ${response.status}")
            Observable.error(Throwable())
        } else {
            Log.i("DEBUG", "[RetryTransformer] applyRetry flatMap just")
            Observable.just(response)
        }
    }.retryWhen { errors ->
        errors.zipWith(Observable.range(1, retryCount)) { throwable, count -> RetryException(throwable, count) }.flatMap { exception ->
            Log.e("DEBUG", "[RetryTransformer] applyRetry retryWhen zipWith throwable : ${exception.count} / $retryCount")
            if (exception.count >= retryCount) {
                Observable.error(exception.throwable)
            } else {
                if (exception.throwable is SocketTimeoutException) {
                    Observable.timer(1000, TimeUnit.MILLISECONDS)
                } else {
                    Observable.error(exception.throwable)
                }
            }
        }
    }
    //.retry{count: Int, _: Throwable -> count <= 3 }
}


fun <T> Observable<T>.applyRetry(): Observable<T> {
    val retryCount = 5
    return flatMap { response ->
        if (response is StatusResponse && response.status != 0) {
            Log.e("DEBUG", "[RetryTransformer] applyRetry flatMap error status : ${response.status}")
            Observable.error(Throwable())
        } else {
            Log.i("DEBUG", "[RetryTransformer] applyRetry flatMap just response : $response")
            Observable.just(response)
        }

    }.retryWhen { errors ->
        errors.zipWith(Observable.range(1, retryCount)) { _, count -> count }.flatMap { count ->
            Log.e("DEBUG", "[RetryTransformer] applyRetry retryWhen zipWith throwable : $count / $retryCount")
            if (count >= retryCount) {
                Observable.error(Throwable())
            } else {
                Observable.timer(1000, TimeUnit.MILLISECONDS)
            }
        }
    }
    //.retry{count: Int, _: Throwable -> count <= 3 }
}
/*
fun <T> Observable<T>.applyRetry(): Observable<T> {
    val retryCount = 5
    return flatMap { response ->
        Log.i("DEBUG", "[RetryTransformer] applyRetry flatMap just response : $response")
        Observable.just(response)

    }.retryWhen { errors ->
        errors.zipWith(Observable.range(1, retryCount)) { _, count -> count }.flatMap { count ->
            Log.e("DEBUG", "[RetryTransformer] applyRetry retryWhen zipWith throwable : $count / $retryCount")
            if (count >= retryCount) {
                Observable.error(Throwable())
            } else {
                Observable.timer(1000, TimeUnit.MILLISECONDS)
            }
        }
    }
}*/