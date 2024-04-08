package com.example.androidkotlin
import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.example.androidkotlin.User

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        // Check if the item identifiers are the same
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        // Check if the item contents are the same
        return oldItem == newItem
    }

    // If your User entity has properties that might change but are not used for
    // determining if two items are the same, you can override this method to
    // check if those properties are the same.
    override fun getChangePayload(oldItem: User, newItem: User): Any? {
        return if (oldItem.email != newItem.email) {
            // Example: Return a payload containing the updated name
            Bundle().apply {
                putString("email", newItem.email)
            }
        } else {
            null
        }
    }
}
