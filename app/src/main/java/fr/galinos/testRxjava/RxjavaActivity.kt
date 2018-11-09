package fr.galinos.testRxjava

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import fr.galinos.testRxjava.transformer.applyRetry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rxjava.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import io.reactivex.ObservableTransformer



class RxjavaActivity : AppCompatActivity() {
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rxjava)
        animateLoader()

        //testObservableJust()
        //testObservableFrom()
        //testObservableFromIterable()
        //testObservableCreate()
        //testObservableDefer()
        //testObservableRange()
        //testObservableInterval()
        //testObservableTimer()
        //testObservable()
        //testObservableZip()
        //testObservableSwitchOnNext()
        testObservableRetry()
        //testObservableTransformation()
    }

    override fun onDestroy() {
        super.onDestroy()
        frameAnimation.stop()
    }

    private fun animateLoader() {
        loaderView.setBackgroundResource(R.drawable.loader_animation)
        frameAnimation = loaderView.background as AnimationDrawable
        frameAnimation.start()
    }

    private fun testObservableTransformation() {
        val mDisposable = CompositeDisposable()

        val list = ArrayList(Arrays.asList(" a ", " b ", " c ", " d ", " e ", " f "))


        mDisposable.add(Observable.fromIterable(list)
                .map {
                    // transform la value emit par l'observer et retourne un objet
                    "$it Map"
                }
                /*.flatMap{ s: String ->  // transform la value emit par l'observer et retourne un observable sans ce soucier de l'ordre
                    val delay: Long = Random().nextInt(1000).toLong()
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation flatMap $s : $delay")
                    Observable.just("$s FlatMap").delay(delay, TimeUnit.MILLISECONDS)
                }*/

                /*.switchMap{ s: String ->  // transform la value emit par l'observer, et retourne le dernier observable emis,  à chaque fois qu'un nouvel élément est émis par l'Observable source, il se désabonnera de l'Observable généré à partir de l'élément précédemment émis et arrêtera de le refléter, et ne commencera à refléter que l'élément en cours.
                    val delay: Long = Random().nextInt(1000).toLong()
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation switchMap $s : $delay")

                    Observable.just("$s SwitchMap").delay(delay, TimeUnit.MILLISECONDS)
                }*/
                .concatMap { s: String ->
                    // transform la value emit par l'observer et retourne un observable en conservant l'ordre et attend que chaque observable soit terminé avant d'en émettre un nouveau
                    val delay: Long = Random().nextInt(1000).toLong()
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation concatMap $s : $delay")

                    Observable.just("$s ConcatMap").delay(delay, TimeUnit.MILLISECONDS)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation list onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation list onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation list onComplete")
                })
        )
    }

    /*
    onNext it : 1
    onNext it : 2
    onNext it : 3
    onNext it : 4
    onNext it : 5
    onNext it : 6
    onNext it : 7
    onNext it : 8
    onNext it : 9
    onNext it : 10
    onComplete

    list onNext it : [1, 2, 3, 4, 5, 6, 7, 8]
    list onComplete
    */
    private fun testObservableJust() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableJust onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableJust onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableJust onComplete")
                })
        )

        val list = ArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8))

        mDisposable.add(Observable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableJust list onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableJust list onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableJust list onComplete")
                })
        )
    }

    /*
    onNext it : [1, 2, 3, 4, 5, 6, 7, 8]
    onComplete
     */
    private fun testObservableFrom() {
        val mDisposable = CompositeDisposable()

        val list = ArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8))

        mDisposable.add(Observable.fromArray(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableFrom onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableFrom onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableFrom onComplete")
                })
        )
    }

    /*
    onNext it : 1
    onNext it : 2
    onNext it : 3
    onNext it : 4
    onNext it : 5
    onNext it : 6
    onNext it : 7
    onNext it : 8
    onComplete
     */
    private fun testObservableFromIterable() {
        val mDisposable = CompositeDisposable()

        val list = ArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8))

        mDisposable.add(Observable.fromIterable(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableFromIterable onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableFromIterable onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableFromIterable onComplete")
                })
        )
    }

    /*
    onNext it : 5
    onComplete
     */
    private fun testObservableCreate() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.create<Int> { emitter ->
            Thread.sleep(1000)
            emitter.onNext(5)

            Thread.sleep(1000)
            emitter.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableCreate onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableCreate onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableCreate onComplete")
                })
        )
    }

    /*
    onNext it [1, 2]
    onComplete
     */
    private fun testObservableDefer() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.defer {
            Thread.sleep(1000)
            Observable.just(listOf(1, 2))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableDefer onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableDefer onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableDefer onComplete")
                })
        )
    }

    /*
    onNext it : value 1
    onNext it : value 2
    onNext it : value 3
    onNext it : value 4
    onNext it : value 5
    onNext it : value 6
    onNext it : value 7
    onNext it : value 8
    onNext it : value 9
    onComplete
    */
    private fun testObservableRange() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.range(0, 10)
                .map { index ->
                    "value $index"
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableRange onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableRange onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableRange onComplete")
                })
        )
    }

    /*
    onNext it 0
    onNext it 1
    onNext it 2
    onNext it 3
    onNext it 4
    onComplete
     */
    private fun testObservableInterval() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.interval(1, TimeUnit.SECONDS)
                .take(5) //emet juste 5 items
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableInterval onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableInterval onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableInterval onComplete")
                })
        )
    }

    private fun testObservableTimer() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.timer(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTimer onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTimer onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTimer onComplete")
                })
        )
    }

    private fun testObservable() {
        val mDisposable = CompositeDisposable()

        mDisposable.add(Observable.range(0, 10)
                //.repeat(3) repete 3 fois


                .filter {
                    it % 2 == 0
                }
                .startWith(100) //la premiere valeur renvoyée est 100
                .mergeWith(Observable.just(10, 11, 12)) // renvoie les valeurs 10, 11, 12 a la fin
                .map { index ->
                    "value $index"
                }
                .takeUntil { item -> item == "value 4" } //stop qd la valeur "Value 4" est atteinte
                .lastElement() // retourne seulement la derniere valeur dans le onNext
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservable onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservable onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservable onComplete")
                })
        )
    }

    private fun testObservableZip() {
        val mDisposable = CompositeDisposable()

        val firstNames = Observable.just("James", "Jean-Luc", "Benjamin")
        val lastNames = Observable.just("Kirk", "Picard", "Sisko")

        mDisposable.add(firstNames.zipWith(lastNames) { first: String, last: String -> "$first $last" }
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip onComplete")
                })
        )
    }

    private fun testObservableSwitchOnNext() {
        val mDisposable = CompositeDisposable()

        val timeIntervals = Observable.interval(1, TimeUnit.SECONDS)
                .map { ticks ->
                    Observable.interval(100, TimeUnit.MILLISECONDS)
                            .map<String> { innerInterval -> "outer: $ticks - inner: $innerInterval" }
                }

        mDisposable.add(Observable.switchOnNext<String>(timeIntervals)
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableSwitchOnNext onNext it $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableSwitchOnNext onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableSwitchOnNext onComplete")
                })
        )
    }

    /*
    retryWhen io.reactivex.subjects.SerializedSubject@3c33098
    onNext it Doing a network call!
    zipWith 1 / java.util.concurrent.TimeoutException
    onNext it Doing a network call!
    zipWith 2 / java.util.concurrent.TimeoutException
    onNext it Doing a network call!
    zipWith 3 / java.util.concurrent.TimeoutException
    onNext it Doing a network call!
    onComplete
     */
    private fun testObservableRetry() {
        val mDisposable = CompositeDisposable()

        val time = System.currentTimeMillis()

        val obs = Observable.fromPublisher<String> {
            it.onNext("Doing a network call!")
            Thread.sleep(1000)      // Long running process

            it.onError(TimeoutException()) // Some error thrown
        }

        mDisposable.add(obs.applyRetry()

                /*.retryWhen { errors ->

            errors.zipWith(Observable.range(1, 3)) { error, count ->
                    error

                }.flatMap { throwable ->
                    println("DEBUG testObservableRetry flatMap $throwable [${System.currentTimeMillis() - time}]")
                    if (throwable is TimeoutException) {
                        Observable.timer(10, TimeUnit.SECONDS)
                    }
                    // For anything else, don't retry
                    else {
                        Observable.error<Throwable>(throwable)
                    }
                }
            }*/


            /*
            range 1 [13]
            onNext it Doing a network call! [49]
            onNext it Doing a network call! [10014]
            range 2 [11015]
            onNext it Doing a network call! [21016]
            range 3 [22016]
            onNext it Doing a network call! [32017]
             onComplete [33019]

            Observable.range(1, 3).concatMap { count ->
                println("DEBUG testObservableRetry range $count [${System.currentTimeMillis() - time}]")
                Observable.timer(10, TimeUnit.SECONDS)
            }*/

            /*, {
        if (error is TimeoutException) {

        }
        // For anything else, don't retry
        else {

        }
        Observable.error<Throwable>(error)
    }*/


            //
                // For IOExceptions, we  retry
                   // Zip error observable with a range one

                /*
                errors.zipWith(Observable.range(1, 3)   // Zip error observable with a range one

                        .concatMap { retryCount ->

                            println("DEBUG concatMap $retryCount")
                            obs.delay(3, TimeUnit.SECONDS)
                        })*/
            //}
        /*.retryWhen { errors ->
            println("DEBUG retryWhen $errors")

            if(errors is IOException) {
                errors.zipWith(Observable.range(1, 3)   // Zip error observable with a range one
                        .concatMap { retryCount ->

                            println("DEBUG concatMap $retryCount")

                            Observable.timer(3, TimeUnit.SECONDS)
                        })
            }
            else {
                Observable.timer(3, TimeUnit.SECONDS)
            }
        }*/
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableRetry onNext it $it [${System.currentTimeMillis() - time}]")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableRetry onError $it [${System.currentTimeMillis() - time}]")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableRetry onComplete [${System.currentTimeMillis() - time}]")
                })
        )

    }

    /*
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
    }*/


    fun <T> Observable<T>.applySchedulers(): Observable<T> {
        return subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    // custom transformer
    fun <T> Observable<T>.applySchedulers(transformer: ThreadTransformer): Observable<T> {
        return compose(transformer.applySchedulers<T>())
    }

    /*override fun call(): Observable<MyType> {
        return Observable.just(getData()).applySchedulers()
    }*/

    interface ThreadTransformer {
        fun <T> applySchedulers(): ObservableTransformer<T, T>

    }

}