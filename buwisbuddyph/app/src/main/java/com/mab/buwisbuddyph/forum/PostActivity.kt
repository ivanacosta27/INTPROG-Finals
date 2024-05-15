package com.mab.buwisbuddyph.forum

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.CommentListAdapter
import com.mab.buwisbuddyph.dataclass.Comment
import com.mab.buwisbuddyph.dataclass.Post

class PostActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var postTitleTV: TextView
    private lateinit var postContentTV: TextView
    private lateinit var commentEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var commentListRV: RecyclerView
    private lateinit var commentListAdapter: CommentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        db = FirebaseFirestore.getInstance()
        postTitleTV = findViewById(R.id.postTitleTV)
        postContentTV = findViewById(R.id.postContentTV)
        commentEditText = findViewById(R.id.commentET)
        sendButton = findViewById(R.id.commentButton)

        commentListRV = findViewById(R.id.commentListRV)
        commentListRV.layoutManager = LinearLayoutManager(this)
        commentListAdapter = CommentListAdapter(mutableListOf())
        commentListRV.adapter = commentListAdapter

        val postId = intent.getStringExtra("postId")
        if (postId != null) {
            loadPost(postId)
        }

        sendButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                val comment = Comment(
                    commentID = "",
                    commentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    commentUserComment = commentText,
                    commentTimestamp = Timestamp.now()
                )

                if (postId != null) {
                    db.collection("posts").document(postId).collection("comments").add(comment)
                        .addOnSuccessListener { documentReference ->
                            documentReference.update("commentID", documentReference.id)
                            println("Comment successfully written!")
                        }
                        .addOnFailureListener { e ->
                            println("Error writing comment: $e")
                        }
                }

                commentEditText.text.clear()
            }
        }
    }

    private fun loadPost(postId: String) {
        db.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                val post = document.toObject(Post::class.java)
                if (post != null) {
                    postTitleTV.text = post.postTitle
                    postContentTV.text = post.postContent

                    db.collection("posts").document(postId).collection("comments")
                        .orderBy("commentTimestamp")
                        .get()
                        .addOnSuccessListener { commentDocuments ->
                            val comments = commentDocuments.map { commentDocument ->
                                commentDocument.toObject(Comment::class.java)
                            }
                            val adapter = CommentListAdapter(comments)
                            commentListRV.adapter = adapter
                            commentListRV.scrollToPosition(adapter.itemCount - 1)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("PostActivity", "Error getting comments", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("PostActivity","Error getting post", exception)
            }
    }


}
