package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.MessageListAdapter
import com.mab.buwisbuddyph.dataclass.new_Message

class InboxActivity : AppCompatActivity(), MessageListAdapter.OnRefreshListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageListAdapter
    private var messages: MutableList<new_Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_fragment_inbox)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        val backButton: ImageView = findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            finish() // This will close the current activity and return to the previous one
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageListAdapter(messages, this)
        recyclerView.adapter = messageAdapter
    }

    override fun onResume() {
        super.onResume()
        fetchInboxMessages() // Refresh messages when the activity is resumed
    }

    override fun onRequestRefresh() {
        fetchInboxMessages()
    }

    private fun fetchInboxMessages() {
        val currentUserID = auth.currentUser?.uid ?: return

        // Fetch messages where the user is involved and is_trash is false
        db.collection("Chats")
            .whereEqualTo("person_1", currentUserID)
            .whereEqualTo("is_trashed", false)
            .get()
            .addOnSuccessListener { chatDocuments ->
                messages.clear()
                for (document in chatDocuments) {
                    document.getString("person_2")?.let { db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
                        val avatarImage = userDocu.getString("userProfileImage") ?: ""
                        val fullName = userDocu.getString("userFullName") ?: ""
                        val lastMessage = document.getString("last_message") ?: ""
                        val chatId = document.id
                        val newMessage = new_Message(avatarImage,fullName, lastMessage, chatId)
                        messages.add(newMessage)
                        messageAdapter.notifyDataSetChanged()
                    } }
                }
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
            }

        // Fetch messages where the user is involved and is_trash is false
        db.collection("Chats")
            .whereEqualTo("person_2", currentUserID)
            .whereEqualTo("is_trashed", false)
            .get()
            .addOnSuccessListener { chatDocuments ->
                messages.clear()
                for (document in chatDocuments) {
                    document.getString("person_1")?.let { db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
                        val avatarImage = userDocu.getString("userProfileImage") ?: ""
                        val fullName = userDocu.getString("userFullName") ?: ""
                        val lastMessage = document.getString("last_message") ?: ""
                        val chatId = document.id
                        val newMessage = new_Message(avatarImage,fullName, lastMessage, chatId)
                        messages.add(newMessage)
                        messageAdapter.notifyDataSetChanged()
                    } }
                }
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}