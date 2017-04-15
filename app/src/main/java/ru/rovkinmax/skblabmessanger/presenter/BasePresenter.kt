package ru.rovkinmax.skblabmessanger.presenter

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import ru.rovkinmax.skblabmessanger.rx.LifecycleProvider

abstract class BasePresenter<V : MvpView> : MvpPresenter<V>() {
    private val lifecyler = LifecycleProvider()

    protected fun <T> lifecycle() = lifecyler.lifecycle<T>()


    override fun onDestroy() {
        lifecyler.unsubscribe()
        super.onDestroy()
    }
}