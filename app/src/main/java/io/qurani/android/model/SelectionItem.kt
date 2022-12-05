package io.qurani.android.model

open class SelectionItem {
    constructor()

    constructor(title: String){
        this.title = title
    }

    open lateinit var title: String

    var img: Int? = null
}