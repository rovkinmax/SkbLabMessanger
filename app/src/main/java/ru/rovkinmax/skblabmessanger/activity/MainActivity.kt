package ru.rovkinmax.skblabmessanger.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.rovkinmax.skblabmessanger.R
import ru.rovkinmax.skblabmessanger.rx.binding.textChanges
import ru.rovkinmax.skblabmessanger.util.changeEnabled

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etUserName.textChanges()
                .map { it.trim().isEmpty() }
                .subscribe({ isEmpty -> btnGoToChat.changeEnabled(isEmpty.not()) })

        btnGoToChat.setOnClickListener {
            startActivity(ChatActivity.makeIntent(this, etUserName.text.toString().trim()))
        }
    }
}
