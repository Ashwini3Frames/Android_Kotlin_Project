package com.example.androidkotlin
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao: UserDao = UserDB.getDatabase(application).userDao()
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    fun getUsersLiveData(): LiveData<List<User>> {
        return allUsers
    }
    fun deleteUsers(userIds: Set<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.deleteUsers(userIds)
        }
    }
    fun getUserById(userId: Int): LiveData<User> {
        return userDao.getUserById(userId)
    }

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.deleteUser(user)
        }
    }
}
