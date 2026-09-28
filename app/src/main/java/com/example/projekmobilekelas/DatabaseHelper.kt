package com.example.chatapp

import android.content.Context
import com.example.projekmobilekelas.ChatMessage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest

class DatabaseHelperFirebase(context: Context) {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val chatHistoryRef: DatabaseReference = database.getReference("chat_history")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Function to register user using Firebase Authentication
    fun registerUser(username: String, password: String, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Function to log in user using Firebase Authentication
    fun loginUser(username: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Function to save chat message to Firebase Realtime Database
    fun saveChatMessage(username: String, message: String, sender: String) {
        val chatMessage = ChatMessage(sender, message)
        val messageId = chatHistoryRef.push().key
        val hashedUsername = hashUsername(username)
        messageId?.let {
            chatHistoryRef.child(hashedUsername).child(it).setValue(chatMessage)
        }
    }

    // Function to load chat history from Firebase Realtime Database
    fun loadChatHistory(username: String, callback: (List<ChatMessage>) -> Unit) {
        val hashedUsername = hashUsername(username)
        chatHistoryRef.child(hashedUsername).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatHistory = mutableListOf<ChatMessage>()
                for (chatSnapshot in snapshot.children) {
                    val chatMessage = chatSnapshot.getValue(ChatMessage::class.java)
                    chatMessage?.let { chatHistory.add(it) }
                }
                callback(chatHistory)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // Helper function to hash username (email) to be used as a valid Firebase key
    private fun hashUsername(username: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(username.toByteArray())
        return digest.joinToString("") { String.format("%02x", it) }
    }
}