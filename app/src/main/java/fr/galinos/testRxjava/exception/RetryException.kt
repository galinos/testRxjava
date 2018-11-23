package fr.galinos.testRxjava.exception

class RetryException(var throwable: Throwable, var count: Int) {
    override fun toString(): String {
        return "RetryException{ throwable : $throwable, count : $count }"
    }
}
