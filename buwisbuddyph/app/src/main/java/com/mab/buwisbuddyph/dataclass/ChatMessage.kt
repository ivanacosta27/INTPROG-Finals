<<<<<<< HEAD
package com.mab.buwisbuddyph.dataclass
=======
package com.mab.buwisbuddyph.models
>>>>>>> 06eba06f8f144f744a001cb4713e07cdaf4a620a

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
