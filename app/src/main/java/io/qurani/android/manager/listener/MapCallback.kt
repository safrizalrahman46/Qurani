package io.qurani.android.manager.listener

interface MapCallback<T, U> {
    fun onMapReceived(map: Map<T, U>)
}