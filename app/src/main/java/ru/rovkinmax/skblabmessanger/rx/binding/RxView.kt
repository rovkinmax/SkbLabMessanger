package ru.rovkinmax.skblabmessanger.rx.binding

import android.view.View
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.MainThreadDisposable

fun mainThreadDisposable(func: () -> Unit) = object : MainThreadDisposable() {
    override fun onDispose() = func()
}

fun verifyMainThread() = MainThreadDisposable.verifyMainThread()

fun View.focusChanges() = Observable.create(ViewFocusChangeOnSubscribe(this))

private class ViewFocusChangeOnSubscribe(private val view: View) : ObservableOnSubscribe<Boolean> {
    override fun subscribe(emitter: ObservableEmitter<Boolean>) {
        verifyMainThread()
        emitter.setDisposable(mainThreadDisposable { view.onFocusChangeListener = null })
        view.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (emitter.isDisposed.not())
                emitter.onNext(hasFocus)
        }
        emitter.onNext(view.hasFocus())
    }
}
