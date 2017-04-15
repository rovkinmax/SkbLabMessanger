package ru.rovkinmax.skblabmessanger.rx.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

fun TextView.textChanges() = Observable.create(TextViewTextOnSubscribe(this))

fun TextView.beforeTextChangeEvents() = Observable.create(TextViewBeforeTextChangeEventOnSubscribe(this))

fun TextView.afterTextChangeEvents() = Observable.create(TextViewAfterTextChangeEventOnSubscribe(this))

private class TextViewTextOnSubscribe(private val textView: TextView) : ObservableOnSubscribe<CharSequence> {
    override fun subscribe(emitter: ObservableEmitter<CharSequence>) {
        verifyMainThread()

        val textWatcher = textWatcher { s, start, before, count ->
            if (emitter.isDisposed.not())
                emitter.onNext(s)
        }
        textView.addTextChangedListener(textWatcher)
        emitter.setDisposable(mainThreadDisposable { textView.removeTextChangedListener(textWatcher) })
        emitter.onNext(textView.text)
    }
}

private class TextViewBeforeTextChangeEventOnSubscribe(private val textView: TextView) : ObservableOnSubscribe<TextViewBeforeTextChangeEvent> {
    override fun subscribe(emitter: ObservableEmitter<TextViewBeforeTextChangeEvent>) {
        verifyMainThread()

        val textWatcher = textWatcher(beforeChanged = { s, start, count, after ->
            if (emitter.isDisposed.not())
                emitter.onNext(TextViewBeforeTextChangeEvent(textView, s, start, count, after))
        })
        textView.addTextChangedListener(textWatcher)
        emitter.setDisposable(mainThreadDisposable { textView.removeTextChangedListener(textWatcher) })
        emitter.onNext(TextViewBeforeTextChangeEvent(textView, textView.text, 0, 0, 0))
    }
}

class TextViewBeforeTextChangeEvent(val textView: TextView, val s: CharSequence?, val start: Int, val count: Int, val after: Int)


private class TextViewAfterTextChangeEventOnSubscribe(private val textView: TextView) : ObservableOnSubscribe<TextViewAfterTextChangeEvent> {
    override fun subscribe(emitter: ObservableEmitter<TextViewAfterTextChangeEvent>) {
        verifyMainThread()

        val textWatcher = textWatcher(afterChanged = { s ->
            if (emitter.isDisposed.not())
                emitter.onNext(TextViewAfterTextChangeEvent(textView, s))
        })
        textView.addTextChangedListener(textWatcher)
        emitter.setDisposable(mainThreadDisposable { textView.removeTextChangedListener(textWatcher) })
        emitter.onNext(TextViewAfterTextChangeEvent(textView, textView.editableText))
    }
}

class TextViewAfterTextChangeEvent(val textView: TextView, val editable: Editable?)

fun textWatcher(beforeChanged: (CharSequence?, Int, Int, Int) -> Unit = { s, start, count, after -> },
                afterChanged: (s: Editable?) -> Unit = { s -> },
                textChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit = { s, start, before, count -> }): TextWatcher {
    return object : TextWatcher {

        override fun afterTextChanged(s: Editable?) = afterChanged(s)

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = beforeChanged(s, start, count, after)

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = textChanged(s, start, before, count)
    }
}