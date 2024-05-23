package com.mab.buwisbuddyph.messages

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.messages.CreateMessageActivity

class MessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var btnInbox: Button
    private lateinit var btnTrash: Button
    private lateinit var btnArchive: Button
    private var inboxBadge: Int = 0
    private var trashBadge: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        btnInbox = view.findViewById(R.id.btnInbox)
        btnTrash = view.findViewById(R.id.btnTrash)
        btnArchive = view.findViewById(R.id.btnArchive)

        // Initialize badges
        btnInbox.text = "Inbox (${inboxBadge})"
        btnTrash.text = "Trash (${trashBadge})"

        // Set up click listeners for menu buttons
        view.findViewById<View>(R.id.btnNewMessage).setOnClickListener {
            val intent = Intent(requireContext(), CreateMessageActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<View>(R.id.btnArchive).setOnClickListener {
            // Navigate to Archive activity or fragment
        }

        btnInbox.setOnClickListener {
            val intent = Intent(requireContext(), InboxActivity::class.java)
            startActivity(intent)
        }

        btnTrash.setOnClickListener {
            val intent = Intent(requireContext(), TrashActivity::class.java)
            startActivity(intent)
        }
        btnArchive.setOnClickListener {
            val intent = Intent(requireContext(), ArchiveActivity::class.java)
            startActivity(intent)
        }

        // Fetch and update badge counts
//        updateBadgeCounts()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateBadgeCounts()
    }

    @SuppressLint("SetTextI18n")
    private fun updateBadgeCounts() {
        val currentUserID = auth.currentUser?.uid ?: return

        var unreadInInbox = 0
        var unreadInTrash = 0

        // First Firestore query for person_1
        db.collection("Chats")
            .whereEqualTo("person_1", currentUserID)
            .get()
            .addOnSuccessListener { chatDocuments ->

                for (document in chatDocuments) {
                    val isTrash = document.getBoolean("is_trashed") ?: false
                    if (!isTrash) {
                        unreadInInbox++
                    } else if (isTrash) {
                        unreadInTrash++
                    }
                }

                // Second Firestore query for person_2
                db.collection("Chats")
                    .whereEqualTo("person_2", currentUserID)
                    .get()
                    .addOnSuccessListener { chatDocuments ->

                        for (document in chatDocuments) {
                            val isTrash = document.getBoolean("is_trashed") ?: false
                            if (!isTrash) {
                                unreadInInbox++
                            } else if (isTrash) {
                                unreadInTrash++
                            }
                        }

                        // Update the button text after the first query is complete
                        btnInbox.text = "Inbox ($unreadInInbox)"
                        btnTrash.text = "Trash ($unreadInTrash)"
                    }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

}
