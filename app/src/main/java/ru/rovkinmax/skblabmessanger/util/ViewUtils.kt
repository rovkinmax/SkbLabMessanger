package ru.rovkinmax.skblabmessanger.util

import android.view.LayoutInflater
import android.view.View

@Suppress("UNCHECKED_CAST")
fun <T : View> View.find(id: Int): T = findViewById(id) as T

fun View.changeEnabled(isEnabled: Boolean): Unit {
    alpha = if (isEnabled) 1f else 0.5f
    this.isEnabled = isEnabled
    this.isClickable = isEnabled
}

val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)

fun View.hide(): Unit {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}