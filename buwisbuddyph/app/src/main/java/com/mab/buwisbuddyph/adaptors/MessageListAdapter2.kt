package com.mab.buwisbuddyph.adaptors

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.activities.ChatActivity
import com.mab.buwisbuddyph.messages.InboxActivity
import com.mab.buwisbuddyph.messages.TrashActivity
import com.mab.buwisbuddyph.model.new_Message
import de.hdodenhof.circleimageview.CircleImageView

class MessageListAdapter2(private val messages: List<new_Message>) : RecyclerView.Adapter<MessageListAdapter2.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.circleImageView)
        val userFullName: TextView = itemView.findViewById(R.id.fullNameTextView)
        val lastMessage: TextView = itemView.findViewById(R.id.lastChatTextView)
        val layout: LinearLayout = itemView.findViewById(R.id.layoutMain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // Set data to views
        holder.userFullName.text = message.fullName
        holder.lastMessage.text = message.last_chat
        holder.layout.setOnClickListener {
            // Open ChatActivity and pass chatId as an argument
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chatID", message.chatId)
            context.startActivity(intent)
        }
        holder.layout.setOnLongClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, message)
            true // Return true to consume the long click event
        }

        // Set profile image (you need to load the image here if you have a URL or resource ID)
        // holder.profileImage.setImageDrawable(...)
    }

    override fun getItemCount() = messages.size

    private fun showDeleteConfirmationDialog(context: Context, newnew_Message: new_Message) {
        val updateData = hashMapOf<String, Any>(
            "is_trashed" to false
        )
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Retrieve Message")
        alertDialogBuilder.setMessage("Are you sure you want to retrieve this message?")
        alertDialogBuilder.setPositiveButton("Retrieve") { dialogInterface: DialogInterface, _: Int ->
            // Perform action when "Trash" button is clicked
            // Add your logic here to move the message to trash
            FirebaseFirestore.getInstance().collection("Chats").document(newnew_Message.chatId).update(updateData) .addOnSuccessListener {
                Toast.makeText(context, "Message retrieved", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
                val intent = Intent(context, TrashActivity::class.java)
                context.startActivity(intent)

            }

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            // Dismiss the dialog when "Cancel" button is clicked
            dialogInterface.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}