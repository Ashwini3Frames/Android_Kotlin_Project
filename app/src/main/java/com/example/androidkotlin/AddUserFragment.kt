package com.example.androidkotlin


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val PICK_IMAGE_REQUEST = 1

class AddUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var editTextFullName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var buttonAdd: Button
    private lateinit var spinnerRole:Spinner
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var userDao:UserDao
    private lateinit var imageView: ImageView
    private var selectedImage: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to HomeFragment
                findNavController().navigate(R.id.action_addUserFragment_to_homeFragment)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_add_user, container, false)
        // Initialize userDao
        userDao = Room.databaseBuilder(requireContext(), UserDB::class.java, "user-database").build().userDao()
        imageView = view.findViewById(R.id.imageView)
        val buttonSelectImage = view.findViewById<Button>(R.id.buttonSelectImage)
        buttonSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        editTextFullName = view.findViewById(R.id.editTextFullName)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        buttonAdd = view.findViewById(R.id.buttonAdd)
        spinnerRole = view.findViewById(R.id.spinnerRole)
        radioGroupGender = view.findViewById(R.id.radioGroupGender)
        buttonAdd.setOnClickListener {
            if (validateInputs()) {
                // Perform signup operation
                saveUserData()
                replaceWithHomeFragment()
                clearInputs()
                //Toast.makeText(requireContext(), "User Added successfully", Toast.LENGTH_SHORT).show()

            }
        }
        return view
    }
    private fun selectImageFromGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            imageView.setImageBitmap(selectedImage)
        }
    }
    private fun saveUserData() {
        val fullName =editTextFullName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val phoneNumber = editTextPhone.text.toString().trim()
        val role = spinnerRole.selectedItem.toString()
        val gender = when (radioGroupGender.checkedRadioButtonId) {
            R.id.radioButtonMale -> "Male"
            R.id.radioButtonFemale -> "Female"
            else -> ""
        }

        // Convert the Bitmap to a ByteArray
        val imageByteArray = selectedImage?.let { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
        // Create a User object with the image data
        val user = User(
            0,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender,
            role = role,
            profilePicture = imageByteArray
        )

        // Insert user into the database

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userDao.insertUser(user)
                }
                // Show success message on the main UI thread
                lifecycleScope.launchWhenResumed {
                    Toast.makeText(
                        requireContext(),
                        "User inserted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during user insertion: ${e.message}", e)
                // Show error message on the main UI thread
                lifecycleScope.launchWhenResumed {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
    private fun clearInputs() {
        editTextFullName.text.clear()
        editTextEmail.text.clear()
        editTextPhone.text.clear()
        radioGroupGender.clearCheck()
        spinnerRole.setSelection(0)
    }
    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        // Define your custom regular expression for phone number validation
        val regex = Regex("^\\d{10}\$") // Example: Validates 10-digit phone number

        // Check if the phone number matches the regular expression
        return regex.matches(phoneNumber)
    }

    private fun replaceWithHomeFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView6, HomeFragment.newInstance("",""))
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun validateInputs(): Boolean {
        val fullName = editTextFullName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val phoneNumber = editTextPhone.text.toString().trim()
        val role = spinnerRole.selectedItem.toString()
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

        if (!isPhoneNumberValid(phoneNumber)) {
            editTextPhone.error = "Invalid phone number"
            return false
        }

        if (phoneNumber.isEmpty()) {
            editTextPhone.error = "Phone number is required"
            return false
        }

        val genderSelected = view?.findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioButtonSelected = genderSelected?.findViewById<RadioButton>(genderSelected.checkedRadioButtonId)
        if (radioButtonSelected == null) {
            Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show()
            return false
        }
        if (role == "Select Role") {
            Toast.makeText(requireContext(), "Please select a role", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment AddUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val TAG = "AdduserFragment"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}