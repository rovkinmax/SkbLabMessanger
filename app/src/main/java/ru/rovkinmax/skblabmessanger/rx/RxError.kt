package ru.rovkinmax.skblabmessanger.rx

import android.app.Fragment
import android.app.FragmentManager
import io.reactivex.functions.Consumer
import ru.rovkinmax.skblabmessanger.dialog.ErrorMessageDialog
import ru.rovkinmax.skblabmessanger.dialog.NetworkErrorDialog
import ru.rovkinmax.skblabmessanger.dialog.UnexpectedErrorDialog
import ru.rovkinmax.skblabmessanger.view.ErrorView
import timber.log.Timber

object RxError {

    fun view(fragment: Fragment): ErrorView {
        return view(fragment.fragmentManager)
    }

    fun view(fm: FragmentManager, func: (() -> Unit)? = null): ErrorView {
        return object : ErrorView {
            override fun showNetworkError() {
                NetworkErrorDialog.show(fm, func)
            }

            override fun showUnexpectedError() {
                UnexpectedErrorDialog.show(fm, func)
            }

            override fun showErrorMessage(message: String, needCallback: Boolean) {
                ErrorMessageDialog.show(fm, message, if (needCallback) func else null)
            }
        }
    }

    fun doNothing(): Consumer<Throwable> = Consumer { Timber.tag(RxError::class.java.name).d(it, "Something went wrong") }

}