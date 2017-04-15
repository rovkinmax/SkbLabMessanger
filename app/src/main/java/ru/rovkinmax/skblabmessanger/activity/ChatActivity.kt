package ru.rovkinmax.skblabmessanger.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.backendless.messaging.Message
import kotlinx.android.synthetic.main.ac_chat.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.rovkinmax.skblabmessanger.R
import ru.rovkinmax.skblabmessanger.adapter.ChatAdapter
import ru.rovkinmax.skblabmessanger.presenter.ChatPresenter
import ru.rovkinmax.skblabmessanger.rx.RxError
import ru.rovkinmax.skblabmessanger.rx.binding.textChanges
import ru.rovkinmax.skblabmessanger.util.changeEnabled
import ru.rovkinmax.skblabmessanger.util.hide
import ru.rovkinmax.skblabmessanger.util.show
import ru.rovkinmax.skblabmessanger.view.ChatView

class ChatActivity : MvpAppCompatActivity(), ChatView {

    companion object {
        private const val KEY_USERNAME = "USERNAME"

        fun makeIntent(context: Context, username: String): Intent {
            return Intent(context, ChatActivity::class.java)
                    .putExtra(KEY_USERNAME, username)
        }
    }

    @InjectPresenter
    lateinit var presenter: ChatPresenter
    private val username by lazy { intent.getStringExtra(KEY_USERNAME) }
    private lateinit var adapter: ChatAdapter
    private val errorView by lazy { RxError.view(fragmentManager) { openMainScreen() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_chat)
        setSupportActionBar(toolbar)
        title = username
        adapter = ChatAdapter(username)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        etMessage.textChanges()
                .map { it.isNotBlank() }
                .subscribe { isEnabled -> btnSend.changeEnabled(isEnabled) }
        btnSend.setOnClickListener { presenter.sendMessage(etMessage.text.toString()) }
    }

    @ProvidePresenter
    fun providePresenter(): ChatPresenter {
        return ChatPresenter(username).apply { connect() }
    }

    override fun addMessages(list: List<Message>) {
        adapter.addMessages(list)
        recyclerView.scrollToPosition(adapter.itemCount)
    }

    override fun addSendedMessage(message: Message) {
        adapter.addMessages(arrayListOf(message))
    }

    override fun clearInput() {
        etMessage.setText("")
    }

    override fun showSendingProgress() {
        sendingProgress.show()
    }

    override fun hideSendingProgress() {
        sendingProgress.hide()
    }

    override fun onBackPressed() {
        //disable back pressed
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_exit) {
            openMainScreen()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun showErrorMessage(message: String, needCallback: Boolean) {
        errorView.showErrorMessage(message, needCallback)
    }

    override fun showNetworkError() {
        errorView.showNetworkError()
    }

    override fun showUnexpectedError() {
        errorView.showUnexpectedError()
    }
}