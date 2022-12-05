package io.qurani.android.manager.listener

interface ListCallback<T> {
    fun onMapReceived(items: List<T>)
}