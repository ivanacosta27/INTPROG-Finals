package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.ChatAdapter
import com.mab.buwisbuddyph.dataclass.new_Message
import com.mab.buwisbuddyph.dataclass.ChatMessage
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var auth: FirebaseAuth
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var editText: EditText
    private lateinit var other_id: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity_chat)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        editText = findViewById(R.id.messageEditText)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatAdapter = ChatAdapter(chatMessages)

        val backButton: ImageView = findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            onBackPressed()
        }
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        val chatID = intent.getStringExtra("chatID")

        val avatar: CircleImageView = findViewById(R.id.avatar)
        val name: TextView = findViewById(R.id.name)
        val auth = FirebaseAuth.getInstance()
        if (chatID != null) {
            checkForArchivedText(chatID)
        }
        if (chatID != null) {
            db.collection("Chats").document(chatID).get().addOnSuccessListener { chatDocs ->
                if (chatDocs.get("person_1") == auth.uid) {
                    chatDocs.getString("person_2")?.let {
                        db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
//                        avatar.setImageDrawable("/${}userDocu.getString("userProfileImage") ?: "")
                            name.text = userDocu.getString("userFullName") ?: ""
                            other_id = userDocu.getString("userID").toString()
                        }
                    }
                } else {
                    chatDocs.getString("person_1")?.let {
                        db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
//                        avatar.setImageDrawable("/${}userDocu.getString("userProfileImage") ?: "")
                            name.text = userDocu.getString("userFullName") ?: ""
                            other_id = userDocu.getString("userID").toString()
                        }
                    }
                }
            }
        }

        loadMessages(chatID)

        val sendButton: Button = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            val message = editText.text.toString()
            if (message.isNotEmpty()) {
                if (chatID != null) {
                    sendMessage(message, chatID)
                }
                editText.text.clear() // Clear the input field after sending a message
            }
        }
    }

    private fun checkForArchivedText(chatID: String) {
        // Query the "archives" collection to check for archived text
        db.collection("archives")
            .whereEqualTo("my_id", auth.currentUser?.uid)
            .whereEqualTo("chat_id", chatID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Found archived text, pre-write it into the EditText
                    val archivedText = documents.documents[0].getString("text_left") ?: ""
                    editText.setText(archivedText)
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors
                // For now, let's just log the error
                Log.e("YourActivity", "Error checking for archived text", e)
            }
    }

    override fun onBackPressed() {
        // Check if the EditText is not blank
        val textLeft = editText.text.toString().trim()
        saveToArchives(textLeft)
        super.onBackPressed()
    }

    private fun saveToArchives(textLeft: String) {
        // Create a new document in the "archives" collection

        if (textLeft == "") {
            val archivesRef = db.collection("archives").document(other_id)
            archivesRef.delete()
        } else {
            val archivesRef = db.collection("archives").document(other_id)
            // Define the data to be saved
            val data = hashMapOf(
                "my_id" to auth.currentUser?.uid,
                "text_left" to textLeft,
                "chat_id" to intent.getStringExtra("chatID") // Assuming you pass chatID via Intent
            )

            // Save the data to Firestore
            archivesRef.set(data)
                .addOnSuccessListener {
                    // Document saved successfully
                    super.onBackPressed() // Proceed with the default back behavior
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    // You can choose to show a toast or log the error
                    // For now, let's just log the error
                    Log.e("YourActivity", "Error saving to archives", e)
                }
        }
    }


    private fun sendMessage(message: String, chatID: String) {
        val timestamp = System.currentTimeMillis()
        val chatMessage = FirebaseAuth.getInstance().uid?.let {
            ChatMessage(
                senderId = it, // Replace with actual user ID
                message = message,
                timestamp = timestamp
            )
        }

        // Update last_message field in the main document
        val chatRef = db.collection("Chats").document(chatID)
        chatRef.update("last_message", message, "is_trashed", false)
            .addOnSuccessListener {
                Log.d("ChatActivity", "Last message updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w("ChatActivity", "Error updating last message", e)
            }

        // Add message to the Messages subcollection
        if (chatMessage != null) {
            chatRef.collection("Messages")
                .add(chatMessage)
                .addOnSuccessListener {
                    Log.d("ChatActivity", "Message sent successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("ChatActivity", "Error sending message", e)
                }
        }
    }


    private fun loadMessages(chatID: String?) {
        if (chatID != null) {
            db.collection("Chats").document(chatID).collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("ChatActivity", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        chatMessages.clear()
                        for (doc in snapshots) {
                            val message = doc.toObject(ChatMessage::class.java)
                            chatMessages.add(message)
                        }
                        chatAdapter.notifyDataSetChanged()
                    }
                }
        }
    }
}
