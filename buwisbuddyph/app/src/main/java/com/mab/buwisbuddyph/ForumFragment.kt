package com.mab.buwisbuddyph

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForumFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)
        Log.d("forumFragment", "forum fragment loaded")
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.forumPostList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(mutableListOf())
        recyclerView.adapter = postAdapter

        loadPosts()
        Log.d("forumFragment", "posts loaded")

        val createPost: ImageView = view.findViewById(R.id.createPost)
        createPost.setOnClickListener {
            onCreatePost(it)
        }

        return view
    }


    private fun loadPosts() {
        db.collection("posts")
            .orderBy("timestamp") // Order posts by timestamp in ascending order
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = mutableListOf<Post>()
                for (document in querySnapshot.documents) {
                    val post = document.toObject(Post::class.java)
                    post?.let {
                        posts.add(it)
                    }
                }
                postAdapter.setPosts(posts)
                Log.d(TAG, "Posts successfully loaded and set to adapter")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting posts", exception)
            }
    }

    companion object {
        private const val TAG = "ForumFragment"
    }

    private fun onCreatePost(view: View) {
        Log.d(TAG, "onCreatePost called")
        val postFragment = PostFragment()
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, postFragment)
            commit()
        }
        Log.d(TAG, "PostFragment has been committed")
    }

}
