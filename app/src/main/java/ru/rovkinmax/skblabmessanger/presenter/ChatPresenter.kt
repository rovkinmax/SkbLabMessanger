package ru.rovkinmax.skblabmessanger.presenter

import com.arellomobile.mvp.InjectViewState
import com.backendless.messaging.Message
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import ru.rovkinmax.skblabmessanger.repository.RepositoryProvider
import ru.rovkinmax.skblabmessanger.rx.RxDecor
import ru.rovkinmax.skblabmessanger.util.async
import ru.rovkinmax.skblabmessanger.view.ChatView

@InjectViewState
class ChatPresenter(private val currentUser: String) : BasePresenter<ChatView>() {

    fun connect() {
        provideMessages()
                .map { it.sortedByDescending { it.timestamp } }
                .async()
                .compose(lifecycle())
                .subscribe(Consumer { list -> viewState.addMessages(list) }, RxDecor.error(viewState))
    }

    private fun provideMessages(): Flowable<List<Message>> {
        return RepositoryProvider.provideChatRepo()
                .fetchMessages()
    }

    fun sendMessage(message: String) {
        RepositoryProvider.provideChatRepo()
                .sendMessage(currentUser, message)
                .async()
                .compose(lifecycle())
                .doOnSubscribe { viewState.showSendingProgress() }
                .doFinally { viewState.hideSendingProgress() }
                .subscribe(Consumer { dispatchMessageStatus(it) }, RxDecor.error(viewState))
    }

    private fun dispatchMessageStatus(message: Message) {
        viewState.clearInput()
        viewState.addSendedMessage(message)
    }
}