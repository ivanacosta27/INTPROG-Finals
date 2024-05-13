package com.mab.buwisbuddyph

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity



class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toHome()

        val forumIcon = findViewById<ImageView>(R.id.forumIcon)
        forumIcon.setOnClickListener{
            Log.d("forum", "forum icon clicked")
            toForum()
        }

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener{
            toHome()
        }

        val messageIcon = findViewById<ImageView>(R.id.messagesIcon)
        messageIcon.setOnClickListener{
            toMessages()
        }
    }

    private fun toForum(){
        val forumFragment = ForumFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, forumFragment)
            commit()
        }
    }

    private fun toHome(){
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, homeFragment)
            commit()
        }
    }

    private fun toMessages(){
        val messagesFragment = MessagesFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, messagesFragment)
            commit()
        }
    }
}