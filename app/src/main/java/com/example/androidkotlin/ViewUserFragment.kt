package com.example.androidkotlin
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class ViewUserFragment : Fragment() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getInt("userId") ?: 0

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getUserById(userId).observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                view.findViewById<TextView>(R.id.fullNameTextView).text = it.fullName
                view.findViewById<TextView>(R.id.emailTextView).text = it.email
                view.findViewById<TextView>(R.id.phoneNumberTextView).text = it.phoneNumber
                view.findViewById<TextView>(R.id.genderTextView).text = it.gender
                view.findViewById<TextView>(R.id.roleTextView).text = it.role
                // Find the ImageView in your layout file
                val imageView = view.findViewById<ImageView>(R.id.imageView)

                // Load and display the image
//                if (it.profilePicture != null) {
//                    val bitmap =
//                        BitmapFactory.decodeByteArray(it.profilePicture, 0, it.profilePicture.size)
//                    imageView.setImageBitmap(bitmap)
//                }
                try {
                    // Load and display the image directly from the byte array
                    if (it.profilePicture != null) {
                        val bitmap = BitmapFactory.decodeByteArray(
                            it.profilePicture,
                            0,
                            it.profilePicture.size
                        )
                        imageView.setImageBitmap(bitmap)
                    } else {
                        // If profile picture is null, set a placeholder image or handle the case accordingly
                        imageView?.setImageResource(R.drawable.profile_pic)
                    }
                } catch (e: Exception) {
                    // Log any exceptions that occur during image loading
                    Log.e("ViewUserFragment", "Error loading image: ${e.message}", e)
                }
            }
        })
    }
    }
