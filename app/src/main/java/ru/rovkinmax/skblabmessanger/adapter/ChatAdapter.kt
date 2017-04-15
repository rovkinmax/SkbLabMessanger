package ru.rovkinmax.skblabmessanger.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.backendless.messaging.Message
import ru.rovkinmax.skblabmessanger.R
import ru.rovkinmax.skblabmessanger.util.find
import ru.rovkinmax.skblabmessanger.util.layoutInflater

class ChatAdapter(private val owner: String) : RecyclerView.Adapter<ChatAdapter.MessageHolder>() {
    private val messages = ArrayList<Message>()
    private val messagesIds = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = parent.layoutInflater
        return MessageHolder(inflater.inflate(viewType, parent, false))
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageHolder?, position: Int) {
        holder?.bind(messages[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].publisherId == owner)
            R.layout.item_message_owner
        else R.layout.item_message_alien
    }

    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameView by lazy { itemView.find<TextView>(R.id.username) }
        val messageView by lazy { itemView.find<TextView>(R.id.message) }

        fun bind(message: Message) {
            usernameView.text = message.publisherId
            messageView.text = message.data.toString()
        }

    }

    fun addMessages(list: List<Message>) {
        list.forEach { message ->
            if (messagesIds.contains(message.messageId).not())
                addMessage(message)
        }
        notifyDataSetChanged()
    }

    private fun addMessage(message: Message) {
        messages.add(0, message)
        messagesIds.add(0, message.messageId)
    }
}