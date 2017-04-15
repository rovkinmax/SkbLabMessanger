package ru.rovkinmax.skblabmessanger.repository


import com.backendless.Backendless
import com.backendless.Subscription
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.messaging.Message
import com.backendless.messaging.PublishOptions
import com.backendless.messaging.PublishStatusEnum
import com.backendless.messaging.SubscriptionOptions
import io.reactivex.*
import io.reactivex.disposables.Disposable
import java.lang.Exception

class ChatRepository {
    companion object {
        private const val CHANNEL_NAME = "general"
    }

    fun fetchMessages(): Flowable<List<Message>> {
        return Flowable.create({ emitter ->
            val subscriptionOption = SubscriptionOptions()
            Backendless.Messaging.subscribe(CHANNEL_NAME,
                    defaultCallback(defaultFaultFlowableEmitter(emitter), emitter::onNext),
                    subscriptionOption,
                    defaultCallback(defaultFaultFlowableEmitter(emitter)) { subscription ->
                        emitter.setDisposable(SubscriptionDisposable(subscription))
                    })

        }, BackpressureStrategy.BUFFER)
    }

    fun sendMessage(username: String, message: String): Single<Message> {
        return Single.create { emitter ->
            val options = PublishOptions()
            options.publisherId = username
            Backendless.Messaging
                    .publish(CHANNEL_NAME, message, options,
                            defaultCallback(defaultFaultSingleEmitter(emitter)) { messageStatus ->
                                if (messageStatus.status == PublishStatusEnum.SCHEDULED) {
                                    val createdMessage = Message()
                                    createdMessage.data = message
                                    createdMessage.messageId = messageStatus.messageId
                                    createdMessage.publisherId = username
                                    createdMessage.timestamp = System.currentTimeMillis()
                                    emitter.onSuccess(createdMessage)
                                } else {
                                    emitter.onError(ChatException(messageStatus.errorMessage, ""))
                                }
                            })
        }
    }

    private fun <T> defaultFaultFlowableEmitter(emitter: FlowableEmitter<T>): ((BackendlessFault) -> Unit) {
        return { fault -> emitter.onError(ChatException(fault.message, fault.code)) }
    }

    private fun <T> defaultFaultSingleEmitter(emitter: SingleEmitter<T>): ((BackendlessFault) -> Unit) {
        return { fault -> emitter.onError(ChatException(fault.message, fault.code)) }
    }

    private fun <T> defaultCallback(error: ((BackendlessFault) -> Unit), response: ((T) -> Unit)): AsyncCallback<T> {

        return object : AsyncCallback<T> {
            override fun handleResponse(response: T) {
                response(response)
            }

            override fun handleFault(fault: BackendlessFault) {
                error(fault)
            }
        }
    }
}

class ChatException(val fauilMessage: String, val code: String) : Exception("$fauilMessage $code")

class SubscriptionDisposable(private val subscription: Subscription) : Disposable {
    private var disposed = false
    override fun isDisposed(): Boolean = disposed

    override fun dispose() {
        disposed = subscription.cancelSubscription()
    }
}

