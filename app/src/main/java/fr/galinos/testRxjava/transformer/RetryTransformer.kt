package fr.galinos.testRxjava.transformer

import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun <T> Observable<T>.applyRetry(): Observable<T> {
    return retryWhen{ errors ->
        errors.zipWith(Observable.range(1, 3)) { error, count -> error }.flatMap { throwable ->
            println("DEBUG testObservableRetry flatMap $throwable")
            if (throwable is TimeoutException) {
                Observable.timer(1, TimeUnit.SECONDS)
            }
            // For anything else, don't retry
            else {
                Observable.error<Throwable>(throwable)
            }
        }
    }
}

//class RetryTransformer {



    /*fun <T> Observable<T>.applyRetry(transformer: RetryTransformer): Observable<T> {
        return compose(transformer.applyRetry<T>())
    }

    interface RetryTransformer {
        fun <T> applyRetry(): ObservableTransformer<T, T>

    }*/
//}