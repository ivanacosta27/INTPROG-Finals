package com.mab.buwisbuddyph.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Post
import com.mab.buwisbuddyph.forum.PostActivity

class PostListAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    class PostListAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapt
    init {
        itemView.findViewById<View>(R.id.upVotePost).setOnClickListener { onUpVoteClicked(adapterPosition) }
        itemView.findViewById<View>(R.id.downVotePost).setOnClickListener { onDownVoteClicked(adapterPosition) }
        itemView.findViewById<View>(R.id.commentButton).setOnClickListener { onCommentClicked(adapterPosition) }
    }

    private fun onCommentClicked(position: Int) {
        val post = posts[position]
        val intent = Intent(itemView.context, PostActivity::class.java)
        intent.putExtra("postId", post.postID)
        itemView.context.startActivity(intent)
    }

    private fun onUpVoteClicked(position: Int) {
        val post = posts[position]
        val userId = auth.currentUser?.uid ?: return

        if (userId !in post.upvotedBy) {
            post.upvotes++
            post.upvotedBy.add(userId)
            if (userId !in post.postUpVotedBy) {
                post.postUpVotes++
                post.postUpVotedBy.add(userId)
                notifyItemChanged(position)
                updateVotesInFirestore(post)
            }
            @@ -44,9 +54,9 @@ class PostListAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapt
            val post = posts[position]
            val userId = auth.currentUser?.uid ?: return

            if (userId !in post.downvotedBy) {
                post.downvotes++
                post.downvotedBy.add(userId)
                if (userId !in post.postDownVotedBy) {
                    post.postDownVotes++
                    post.postDownVotedBy.add(userId)
                    notifyItemChanged(position)
                    updateVotesInFirestore(post)
                }
                @@ -57,16 +67,14 @@ class PostListAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapt

                postRef
                    .update(
                        "upvotes", post.upvotes,
                        "downvotes", post.downvotes,
                        "upvotedBy", post.upvotedBy,
                        "downvotedBy", post.downvotedBy
                        "postUpVotes", post.postUpVotes,
                        "postDownVotes", post.postDownVotes,
                        "postUpVotedBy", post.postUpVotedBy,
                        "postDownVotedBy", post.postDownVotedBy
                    )
                    .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(tag, "Error updating document", e) }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            @@ -76,10 +84,16 @@ class PostListAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapt

            override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
                val currentItem = posts[position]
                holder.titleTextView.text = currentItem.content
                holder.descTextView.text = currentItem.content
                holder.upVotesTextView.text = currentItem.upvotes.toString()
                holder.downVotesTextView.text = currentItem.downvotes.toString()
                holder.titleTextView.text = currentItem.postTitle
                holder.descTextView.text = currentItem.postDescription
                holder.upVotesTextView.text = currentItem.postUpVotes.toString()
                holder.downVotesTextView.text = currentItem.postDownVotes.toString()

                holder.itemView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, PostActivity::class.java)
                    intent.putExtra("postId", currentItem.postID)
                    holder.itemView.context.startActivity(intent)
                }
            }

            fun setPosts(newPosts: List<Post>) {
                posts.clear()
                posts.addAll(newPosts)
                notifyDataSetChanged()
            }
            override fun getItemCount() = posts.size
        }