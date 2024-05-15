package com.mab.buwisbuddyph.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Comment
import com.squareup.picasso.Picasso

class CommentListAdapter(private val commentList: List<Comment>) : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfileImage: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.userProfileImage)
        val conversationTV: TextView = itemView.findViewById(R.id.conversationTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentItem = commentList[position]
        holder.conversationTV.text = currentItem.commentUserComment

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentItem.commentUserID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userProfileImageURL = document.getString("userProfileImageURL")
                    Picasso.get().load(userProfileImageURL).into(holder.userProfileImage) // Load the image with Picasso
                } else {
                    Log.d("CommentListAdapter", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CommentListAdapter", "Error getting document", exception)
            }
    }


    override fun getItemCount() = commentList.size
}