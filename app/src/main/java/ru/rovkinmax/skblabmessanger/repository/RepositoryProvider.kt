package ru.rovkinmax.skblabmessanger.repository

object RepositoryProvider {

    private var chatRepository: ChatRepository? = null

    fun provideChatRepo(): ChatRepository {
        if (chatRepository == null)
            chatRepository = ChatRepository()
        return chatRepository!!
    }
}