package ua.feo.app.inf

interface IteratorInf<T> {

    fun reset() : T

    fun current() : T

    fun next() : T

    fun hasNext() : Boolean

}