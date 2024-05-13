package com.mab.buwisbuddyph

import java.util.Date

data class Post(
    val userId: String = "",
    val content: String = "",
    val postID: String = "",
    val timestamp: Date = Date(),
    var upvotes: Int = 0,
    var downvotes: Int = 0,
    val upvotedBy: MutableList<String> = mutableListOf(),
    val downvotedBy: MutableList<String> = mutableListOf(),
    var comments: Int = 0
)







