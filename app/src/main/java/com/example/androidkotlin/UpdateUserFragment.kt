package com.example.androidkotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class UpdateUserFragment : Fragment() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnUpdate: Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var gender: String
    private lateinit var role: String
    private var profilePic: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFullName = view.findViewById(R.id.etFullName)
        etEmail = view.findViewById(R.id.etEmail)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        val userId = arguments?.getInt("userId") ?: 0
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getUserById(userId).observe(viewLifecycleOwner, { user ->
            user?.let {
                etFullName.setText(it.fullName)
                etEmail.setText(it.email)
                etPhoneNumber.setText(it.phoneNumber)
                gender = it.gender
                role = it.role
                profilePic = it.profilePicture
            }
        })

        btnUpdate.setOnClickListener {
            updateUserDetails(userId)
            replaceWithHomeFragment()
        }

    }
    private fun replaceWithHomeFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView6, HomeFragment.newInstance("",""))
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun updateUserDetails(userId: Int) {
        val fullName = etFullName.text.toString()
        val email = etEmail.text.toString()
        val phoneNumber = etPhoneNumber.text.toString()

        if (fullName.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank()) {
            val updatedUser = User(
                userId,
                fullName,
                email,
                phoneNumber,
                gender,
                role,
                profilePic
            )
            userViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_updateUserFragment_to_homeFragment)
    }
}
