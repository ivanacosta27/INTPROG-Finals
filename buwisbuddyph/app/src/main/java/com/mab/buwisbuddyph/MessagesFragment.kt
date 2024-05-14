package com.mab.buwisbuddyph

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val createMessage : ImageView = view.findViewById(R.id.createMessage)
        createMessage.setOnClickListener{
            onCreateMessage(it)
        }

        return view
    }

    private fun onCreateMessage(view: View) {
        Log.d("MessageFragment", "onCreatePost called")
        val createMessageFragment = CreateMessageFragment()
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, createMessageFragment)
            commit()
        }
        Log.d("MessageFragment", "MessageFragment has been committed")
    }
}
