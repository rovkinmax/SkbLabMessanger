package ru.rovkinmax.skblabmessanger.rx


import io.reactivex.*
import io.reactivex.functions.Consumer
import org.reactivestreams.Publisher
import ru.rovkinmax.skblabmessanger.repository.ChatException
import ru.rovkinmax.skblabmessanger.view.EmptyView
import ru.rovkinmax.skblabmessanger.view.ErrorView
import ru.rovkinmax.skblabmessanger.view.LoadingView
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object RxDecor {

    fun <T> loading(view: LoadingView): LoadingViewTransformer<T, T> = LoadingViewTransformer(view)

    fun error(view: ErrorView): Consumer<Throwable> {
        return Consumer { e ->
            Timber.tag("RxDecor").d(e, "")
            when (e) {
                is ChatException -> dispatchChatException(e, view)
                is SocketTimeoutException -> view.showNetworkError()
                is UnknownHostException -> view.showNetworkError()
                else -> view.showUnexpectedError()
            }
        }
    }

    private fun dispatchChatException(e: ChatException, view: ErrorView) {
        if (e.message?.contains(ErrorMessages.UNABLE_RESOLVE_HOST, ignoreCase = true) ?: false) {
            view.showNetworkError()
            return
        }

        when (e.code) {
            ErrorCodes.INTERNAL_CLIENT_ERROR -> view.showUnexpectedError()
            else -> view.showErrorMessage(e.message ?: "", false)
        }
    }

    fun <T> emptyStub(view: EmptyView): EmptyStubTransformer<in T, out T> = EmptyStubTransformer(view)

    class EmptyStubTransformer<T : R, R>(private val view: EmptyView) : ObservableTransformer<T, R>, FlowableTransformer<T, R> {

        override fun apply(upstream: Flowable<T>): Publisher<R> {
            return upstream.doOnSubscribe { view.hideEmptyStub() }.switchIfEmpty(emptyFlowable(view)).map { it }
        }

        override fun apply(upstream: Observable<T>): ObservableSource<R> {
            return upstream.doOnSubscribe { view.hideEmptyStub() }.switchIfEmpty(emptyObservable(view)).map { it }
        }

        private fun <T> emptyObservable(view: EmptyView): Observable<T> {
            return Observable.create<T>({ it.onComplete() }).doOnComplete({ view.showEmptyStub() })
        }

        private fun <T> emptyFlowable(view: EmptyView): Flowable<T> {
            return Flowable.create<T>({ it.onComplete() }, BackpressureStrategy.ERROR).doOnComplete { view.showEmptyStub() }
        }
    }

    open class LoadingViewTransformer<T : R, R>(private val loadingView: LoadingView) : ObservableTransformer<T, R>,
            SingleTransformer<T, R>, FlowableTransformer<T, R>, CompletableTransformer, MaybeTransformer<T, R> {

        override fun apply(upstream: Completable): CompletableSource {
            return upstream
                    .doOnSubscribe { loadingView.showLoadingIndicator() }
                    .doFinally { loadingView.hideLoadingIndicator() }
        }

        override fun apply(upstream: Flowable<T>): Publisher<R> {
            return upstream
                    .doOnSubscribe { loadingView.showLoadingIndicator() }
                    .doFinally { loadingView.hideLoadingIndicator() }
                    .map { it }
        }

        override fun apply(upstream: Single<T>): SingleSource<R> {
            return upstream
                    .doOnSubscribe { loadingView.showLoadingIndicator() }
                    .doFinally { loadingView.hideLoadingIndicator() }
                    .map { it }
        }

        override fun apply(upstream: Observable<T>): ObservableSource<R> {
            return upstream
                    .doOnSubscribe { loadingView.showLoadingIndicator() }
                    .doFinally { loadingView.hideLoadingIndicator() }
                    .map { it }
        }

        override fun apply(upstream: Maybe<T>): MaybeSource<R> {
            return upstream
                    .doOnSubscribe { loadingView.showLoadingIndicator() }
                    .doFinally { loadingView.hideLoadingIndicator() }
                    .map { it }
        }
    }

    object ErrorMessages {
        const val UNABLE_RESOLVE_HOST = "Unable to resolve host"
    }

    object ErrorCodes {
        const val INTERNAL_CLIENT_ERROR = "Internal client exception"
    }
}