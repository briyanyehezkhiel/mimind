package com.example.projekmobilekelas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter untuk RecyclerView yang menampilkan pesan chat
class ChatAdapter(private val chatList: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // ViewHolder yang digunakan untuk menampung setiap item chat
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.senderTextView)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Inflate layout untuk item chat
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        // Bind data ke ViewHolder
        val chatMessage = chatList[position]
        holder.senderTextView.text = chatMessage.sender
        holder.messageTextView.text = chatMessage.message
    }

    override fun getItemCount(): Int {
        // Mengembalikan jumlah item dalam list chat
        return chatList.size
    }
}
