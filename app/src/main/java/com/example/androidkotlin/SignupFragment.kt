package com.example.androidkotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import android.util.Patterns
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment() {
    private lateinit var editTextFullName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var buttonsignup: Button
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userDao: UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        // Initialize Room database and UserDao
        val db = Room.databaseBuilder(requireContext(), UserDB::class.java, "user-database").build()
        userDao = db.userDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_signup, container, false)
        editTextFullName=view.findViewById(R.id.editTextFullName)
        editTextEmail =view.findViewById(R.id.editTextEmail)
        editTextPhone=view.findViewById(R.id.editTextPhone)
        editTextPassword =view.findViewById(R.id.editTextPassword)
        buttonsignup=view.findViewById(R.id.buttonSignUp)
        buttonsignup.setOnClickListener {
            if (validateInputs()) {
                // Perform signup operation
                val fullName = editTextFullName.text.toString().trim()
                val email = editTextEmail.text.toString().trim()
                val phoneNumber = editTextPhone.text.toString().trim()
                val password = editTextPassword.text.toString().trim()
                lifecycleScope.launch {    try {
                       val e =  withContext(Dispatchers.IO) {userDao.getUserByEmailOrPhone(email)}
                       val p =  withContext(Dispatchers.IO) {userDao.getUserByEmailOrPhone(phoneNumber)}
                       if (e == null && p == null) {

                               userDao.insertLoginUser(
                                   LoginUser(
                                       0,
                                       fullName,
                                       email,
                                       phoneNumber,
                                       password
                                   )
                               )
                               Toast.makeText(
                                   requireContext(),
                                   "Signup successful",
                                   Toast.LENGTH_SHORT
                               ).show()
                               Navigation.findNavController(requireView())
                                   .navigate(R.id.action_signupFragment_to_loginFragment)
                       } else {
                               Toast.makeText(activity, "email or phone exist", Toast.LENGTH_SHORT)
                                   .show()

                       }
                   }
                   catch (e: Exception) {
                       Log.e(TAG, "Error during signup: ${e.message}", e)
                       withContext(Dispatchers.Main) {
                           Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
                       }
                   }
                }
            }

            }

        return view
    }

    private fun validateInputs(): Boolean {
        val fullName = editTextFullName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val phoneNumber = editTextPhone.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            editTextFullName.error = "Full name is required"
            return false
        }

        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Invalid email address"
            return false
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            editTextPhone.error = "Invalid phone number"
            return false
        }

        if (phoneNumber.isEmpty()) {
            editTextPhone.error = "Phone number is required"
            return false
        }

        if (password.isEmpty()) {
            editTextPassword.error = "Password is required"
            return false
        }

        if (password.length < 8) {
            editTextPassword.error = "Minimum 8 characters required for creating password"
            return false
        }

        if (!password.matches(".*[A-Z].*".toRegex())) {
            editTextPassword.error = "Must Contain at least 1 Upper-Case Character for creating password"
            return false
        }

        if (!password.matches(".*[a-z].*".toRegex())) {
            editTextPassword.error = "Must Contain at least 1 Lower-Case Character for creating password"
            return false
        }

        val genderSelected = view?.findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioButtonSelected = genderSelected?.findViewById<RadioButton>(genderSelected.checkedRadioButtonId)
        if (radioButtonSelected == null) {
            Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignupFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val TAG = "SignupFragment"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}