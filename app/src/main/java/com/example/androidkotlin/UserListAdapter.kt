
package com.example.androidkotlin
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidkotlin.UserListAdapter.UserViewHolder

class UserListAdapter : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    private var isSelectionModeEnabled: Boolean = false
    private var selectedUsers = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser, isSelectionModeEnabled, selectedUsers)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fullNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.userPhoneNumberTextView)
        private val roleTextView: TextView = itemView.findViewById(R.id.userRoleTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.userCheckBox)

        init {
            itemView.setOnLongClickListener {
                toggleSelectionMode()
                true // Consume the long click event
            }

            checkBox.setOnClickListener {
                val user = getItem(adapterPosition)
                if (checkBox.isChecked) {
                    selectedUsers.add(user.id)
                } else {
                    selectedUsers.remove(user.id)
                }
            }
        }

        fun bind(user: User?, selectionModeEnabled: Boolean, selectedUsers: Set<Int>) {
            user?.let {
                fullNameTextView.text = it.fullName
                phoneNumberTextView.text = it.phoneNumber
                roleTextView.text = it.role
                checkBox.isChecked = selectedUsers.contains(user.id)
                checkBox.visibility = if (selectionModeEnabled) View.VISIBLE else View.GONE
            }
        }
    }

    private fun toggleSelectionMode() {
        isSelectionModeEnabled = !isSelectionModeEnabled
        notifyDataSetChanged()
    }

    fun getSelectedUsers(): Set<Int> {
        return selectedUsers
    }
}
