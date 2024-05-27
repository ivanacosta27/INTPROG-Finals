package com.mab.buwisbuddyph.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.User
import de.hdodenhof.circleimageview.CircleImageView

class AccountantAdapter(
    private val userList: List<User>,
    private val onItemClick: (String) -> Unit // Pass necessary values to AccountantProfileActivity
) : RecyclerView.Adapter<AccountantAdapter.AccountantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.accountants_list_item, parent, false)
        return AccountantViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountantViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClick(user.userID) // Pass necessary values to AccountantProfileActivity
        }
    }

    override fun getItemCount() = userList.size

    inner class AccountantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val accountantProfileImage: CircleImageView = itemView.findViewById(R.id.accountantProfileImage)
        private val accountantName: TextView = itemView.findViewById(R.id.accountantName)

        fun bind(user: User) {
            accountantName.text = user.userFullName
            Glide.with(itemView.context)
                .load(user.userProfileImage)
                .placeholder(R.drawable.default_profile_img)
                .error(R.drawable.default_profile_img)
                .into(accountantProfileImage)
        }
    }
}
