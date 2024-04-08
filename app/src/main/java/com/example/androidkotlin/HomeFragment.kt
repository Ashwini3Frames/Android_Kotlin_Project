package com.example.androidkotlin
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.navigation.fragment.findNavController
class HomeFragment : Fragment() {
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        val fabAddUser = rootView.findViewById<FloatingActionButton>(R.id.fab)
        fabAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addUserFragment)
        }
        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbarmenu)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true) // Enable options menu
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish() // Exit the app when back button is pressed
            }
        })

        setupRecyclerView()

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.allUsers.observe(viewLifecycleOwner) { userList ->
            userList?.let {
                userListAdapter.submitList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selectedUsers = userListAdapter.getSelectedUsers()

        return when (item.itemId) {
            R.id.menu_view -> {
                if (selectedUsers.size == 1) {
                    navigateToViewScreen(selectedUsers.first())
                } else {
                    showSelectionAlert()
                }
                true
            }
            R.id.menu_update -> {
                if (selectedUsers.size == 1) {
                    navigateToUpdateScreen(selectedUsers.first())
                } else {
                    showSelectionAlert()
                }
                true
            }
            R.id.menu_delete -> {
                if (selectedUsers.isNotEmpty()) {
                    showDeleteConfirmationDialog(selectedUsers)
                }
                true
            }
            R.id.menu_logout -> {
                navigateToLoginScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSelectionAlert() {
        AlertDialog.Builder(requireContext())
            .setMessage("Please select exactly one user.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private fun navigateToViewScreen(userId: Int) {
        findNavController().navigate(R.id.viewUserFragment, bundleOf("userId" to userId))
    }

    private fun navigateToUpdateScreen(userId: Int) {
        findNavController().navigate(R.id.updateUserFragment, bundleOf("userId" to userId))
    }

    private fun setupRecyclerView() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
        userListAdapter = UserListAdapter()
        recyclerView.apply {
            adapter = userListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showDeleteConfirmationDialog(selectedUsers: Set<Int>) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete selected users?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSelectedItems(selectedUsers)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSelectedItems(selectedUsers: Set<Int>) {
        // Call ViewModel to handle delete operation
        userViewModel.deleteUsers(selectedUsers)
    }
    companion object {
        fun newInstance(param1: String, param2: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}
