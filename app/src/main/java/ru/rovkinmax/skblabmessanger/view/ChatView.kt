package ru.rovkinmax.skblabmessanger.view

import com.arellomobile.mvp.MvpView
import com.backendless.messaging.Message

interface ChatView : MvpView, ErrorView {

    fun addMessages(list: List<Message>)

    fun clearInput()

    fun addSendedMessage(message: Message)

    fun showSendingProgress()

    fun hideSendingProgress()
}