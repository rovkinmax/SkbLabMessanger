package ru.rovkinmax.skblabmessanger.view

interface ErrorView {
    fun showErrorMessage(message: String, needCallback: Boolean = false)

    fun showNetworkError()

    fun showUnexpectedError()
}