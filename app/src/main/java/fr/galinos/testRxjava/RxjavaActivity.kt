package fr.galinos.testRxjava

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import fr.galinos.testRxjava.model.StatusResponse
import fr.galinos.testRxjava.transformer.applyRetry
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.*
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_rxjava.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class RxjavaActivity : AppCompatActivity() {
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rxjava)
        animateLoader()

        //testObservableJust()
        //testObservableFromArray()
        //testObservableFromIterable()
        //testObservableCreate()
        //testObservableDefer()
        //testObservableRange()
        //testObservableInterval()
        //testObservableTimer()
        //testObservable()
        //testObservableZipWith()
        //testObservableSwitchOnNext()
        //testObservableRetry()
        //testObservableTransformation()
        //testFlowable()
        //testMergeDelayError()
        //testConcat()
        //testCombineLatestDelayError()
        //testZipSingle()
        //testMixedObservable()
        //testObservableZip()
        //testCallableAndAction()
        testDelay()
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

    private fun testDelay() {
        Log.d("DEBUG", "[RxjavaActivity] testDelay")
        Observable.just("TEST").map {
            Log.d("DEBUG", "[RxjavaActivity] testDelay map $it")
            it
        }.delay(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testDelay onNext $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testDelay onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testDelay onComplete")
                })
    }


    private fun testCallableAndAction() {
        Log.d("DEBUG", "[RxjavaActivity] testCallableAndAction")

        val singleCall = Single.fromCallable { Thread.sleep(2000) }.map { true }

        val completableAction = Completable.fromAction { Thread.sleep(2000) }

        val completableCallable = Completable.fromCallable { Thread.sleep(2000) }

        singleCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testCallableAndAction onComplete $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testCallableAndAction onError $it")
                })

    }

    private fun testObservableZip() {
        Log.d("DEBUG", "[RxjavaActivity] testObservableZip")

        val observable1 = Observable.create<String> { emitter ->
            Thread.sleep(3000)
            emitter.onNext("observable1")

            emitter.onComplete()
        }

        val observable2 = Observable.create<String> { emitter ->
            Thread.sleep(3000)
            emitter.onNext("observable2")

            emitter.onComplete()
        }

        val observable3 = Observable.create<String> { emitter ->
            Thread.sleep(4000)
            emitter.onNext("observable3")

            emitter.onComplete()
        }

        val single = Single.create<String> {
            Thread.sleep(3000)
            it.onSuccess("single")
        }

        val completable = Completable.fromAction { Thread.sleep(5000) }

        Observable.zip(single.map {
            Log.d("DEBUG", "[RxjavaActivity] testObservableZip single map $it")
            it
        }.toObservable().subscribeOn(Schedulers.io()),
                completable.andThen(Observable.just(true)).subscribeOn(Schedulers.io()),
                observable2.flatMap {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip observable2 flatMap $it")
                    Thread.sleep(4000)
                    Observable.just(it)
                }.subscribeOn(Schedulers.io()), observable3, Function4<String, Boolean, String, String, String>
                { sing, comp, obs2 , obs3 ->
                    convert(sing, comp, obs2, obs3)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip onNext $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testObservableZip onComplete")
                })
    }

    private fun convert(sing: String, comp: Boolean, obs2: String, obs3: String): String {
        Log.d("DEBUG", "[RxjavaActivity] testObservableZip zip : $sing - $comp - $obs2 - $obs3")
        return "--> $sing - $comp - $obs2 - $obs3"
    }

    private fun testMixedObservable() {
        Log.d("DEBUG", "[RxjavaActivity] testMixedObservable")
        val single = Single.create<Int> {
            Thread.sleep(2000)
            it.onSuccess(100)
            //it.onError(Throwable())
        }

        var observable = Observable.create<String> { emitter ->
            Thread.sleep(1000)
            emitter.onNext("testMixedObservable observable")

            Thread.sleep(1000)
            emitter.onComplete()
        }


        single.toObservable().flatMap {

            Observable.just(it)

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testMixedObservable onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testMixedObservable onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testMixedObservable onComplete")
                })
    }

    private fun testZipSingle() {
        Log.d("DEBUG", "[RxjavaActivity] testZipSingle")
        var single1 = Single.create<Int> {
            Thread.sleep(2000)
            it.onSuccess(100)
            //it.onError(Throwable())
        }

        var single2 = Single.create<Int> {
            Thread.sleep(2000)
            it.onSuccess(200)
        }
        single1.toObservable().zipWith(single2.toObservable()).subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testZipSingle onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testZipSingle onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testZipSingle onComplete")
                })
    }

    private fun testMergeDelayError() {
        Log.d("DEBUG", "[RxjavaActivity] testMergeDelayError")
        val single1 = Single.create<Int> {
            Thread.sleep(2000)
            it.onSuccess(2000)
            //it.onError(Throwable())
        }

        val single2 = Single.create<Int> {
            Thread.sleep(4000)
            it.onSuccess(4000)
        }

        val single3 = Single.create<Int> {
            Thread.sleep(3000)
            it.onSuccess(3000)
        }

        Observable.mergeDelayError(single1.toObservable().subscribeOn(Schedulers.io()), single2.toObservable().subscribeOn(Schedulers.io()), single3.toObservable()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testMergeDelayError onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testMergeDelayError onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testMergeDelayError onComplete")
                })

    }

    private fun testConcat() {
        Log.d("DEBUG", "[RxjavaActivity] testConcat")
        val single1 = Single.create<Int> {
            Log.d("DEBUG", "[RxjavaActivity] testConcat single1")
            Thread.sleep(2000)
            it.onSuccess(2000)
            //it.onError(Throwable())
        }

        val single2 = Single.create<Int> {
            Log.d("DEBUG", "[RxjavaActivity] testConcat single2")
            Thread.sleep(4000)
            it.onSuccess(4000)
        }

        val single3 = Single.create<String> {
            Log.d("DEBUG", "[RxjavaActivity] testConcat single3")
            Thread.sleep(3000)
            it.onSuccess("3000")
        }

        val maybe = Maybe.create<String> {
            Log.d("DEBUG", "[RxjavaActivity] testConcat maybe")
            Thread.sleep(1000)
            it.onSuccess("1000")
        }

        val list = ArrayList(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"))
        val flowable = Flowable.create<List<String>> ({
            Log.d("DEBUG", "[RxjavaActivity] testConcat flowable")
            Thread.sleep(2000)

            it.onNext(list)

            it.onComplete()


        }, BackpressureStrategy.BUFFER
        )

        Observable.just(9999).flatMap {
            Log.d("DEBUG", "[RxjavaActivity] testConcat flatMap it : $it")

            Observable.concat<Any>(single1.toObservable(), maybe.toObservable(), flowable.toObservable(), single2.toObservable())
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.d("DEBUG", "[RxjavaActivity] testConcat onNext it : $it")
        }, {
            Log.d("DEBUG", "[RxjavaActivity] testConcat onError $it")
        }, {
            Log.d("DEBUG", "[RxjavaActivity] testConcat onComplete")
        })
    }

    private fun testCombineLatestDelayError() {
        Log.d("DEBUG", "[RxjavaActivity] testCombineLatestDelayError")
        val single1 = Single.create<Int> {
            Thread.sleep(2000)
            it.onSuccess(2000)
            //it.onError(Throwable())
        }

        val single2 = Single.create<Int> {
            Thread.sleep(4000)
            it.onSuccess(4000)
        }

        val single3 = Single.create<String> {
            Thread.sleep(3000)
            it.onSuccess("3000")
        }

        Observable.combineLatestDelayError(Arrays.asList(single1.toObservable(), single2.toObservable(), single3.toObservable())) { result ->
            Log.d("DEBUG", "[RxjavaActivity] testCombineLatestDelayError combine : $result")
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testCombineLatestDelayError onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testCombineLatestDelayError onError $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testCombineLatestDelayError onComplete")
                })
    }

    private fun testFlowable() {
        Log.d("DEBUG", "[RxjavaActivity] testFlowable")
        //val list = ArrayList(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"))
        val list = ArrayList<String>()

        /*.flatMap { results ->
            Flowable.fromIterable(results)
        }.map {

            Thread.sleep(1000)
            "----> $it <----"
        }*/

        /*val observable = PublishSubject.create<Int>()
        observable.observeOn(Schedulers.computation())
                .subscribeBy (
                        onNext ={
                            println("number: ${it}")
                        },onError = {t->
                    print(t.message)
                }
                )
        for (i in 0..1000000){
            observable.onNext(i)
        }*/

        val observable = PublishSubject.create<Int>()
        observable
                .toFlowable(BackpressureStrategy.MISSING)
                .observeOn(Schedulers.computation())
                .subscribeBy (
                        onNext ={
                            println("number: ${it}")
                        },onError = {t->
                    print("error : " + t.message)
                }
                )
        for (i in 0..1000000){
            observable.onNext(i)
        }

/*
        Flowable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("DEBUG", "[RxjavaActivity] testFlowable list onNext it : $it")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testFlowable list onError")
                }, {
                    Log.d("DEBUG", "[RxjavaActivity] testFlowable list onComplete")
                })*/
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
                .doOnComplete {

                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation doOnComplete start")
                    Observable.timer(10000, TimeUnit.MILLISECONDS)
                    Log.d("DEBUG", "[RxjavaActivity] testObservableTransformation doOnComplete end")

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
    private fun testObservableFromArray() {
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

    private fun testObservableZipWith() {
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
        var inc: Int = 0

        mDisposable.add(
                Observable.create<StatusResponse> {
                    Log.d("DEBUG", "[RxjavaActivity] Observable.create [${System.currentTimeMillis() - time}]")
                    inc++
                    Thread.sleep(1000)
                    if (inc < 3) {
                        it.onNext(StatusResponse(400, "Network Error"))
                    } else if (inc < 4) {
                        it.onError(TimeoutException())
                    } else {
                        it.onNext(StatusResponse(0, "Network Ok"))
                        it.onComplete()
                    }
                }
                        .applyRetry()
                        .map {
                            it.statusMsg = "Map this message OK"
                            it
                        }
                        .flatMap {
                            Log.d("DEBUG", "[RxjavaActivity] testObservableRetry flatMap [${System.currentTimeMillis() - time}]")
                            Thread.sleep(2000)
                            Observable.just(it).delay(2000, TimeUnit.MILLISECONDS)
                        }
                        .doOnNext {
                            Log.d("DEBUG", "[RxjavaActivity] testObservableRetry doOnNext [${System.currentTimeMillis() - time}]")
                            //Thread.sleep(5000)
                            Observable.just(it).delay(5000, TimeUnit.MILLISECONDS)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d("DEBUG", "[RxjavaActivity] testObservableRetry onNext it $it [${System.currentTimeMillis() - time}]")
                        }, {
                            Log.d("DEBUG", "[RxjavaActivity] testObservableRetry onError $it [${System.currentTimeMillis() - time}]")
                        }, {
                            Log.d("DEBUG", "[RxjavaActivity] testObservableRetry onComplete [${System.currentTimeMillis() - time}]")
                        })

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
        )

    }
}