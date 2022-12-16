package com.example.myapplication.authentication.login

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.authentication.roomdb.RegisterRepository
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: RegisterRepository, application: Application) :
    AndroidViewModel(application), Observable {

    val users = repository.users

    @Bindable
    val inputUsername = MutableLiveData<String?>()

    @Bindable
    val inputPassword = MutableLiveData<String?>()

    private val viewModelJob = Job()
    private val myScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigatetoRegister = MutableLiveData<Boolean>()

    val navigatetoRegister: LiveData<Boolean>
        get() = _navigatetoRegister

    private val _navigatetoHomeScreen = MutableLiveData<Boolean>()

    val _navtoHomeScreen: LiveData<Boolean>
        get() = _navigatetoHomeScreen

    private val _errorToast = MutableLiveData<Boolean>()

    val errotoast: LiveData<Boolean>
        get() = _errorToast

    private val _errorToastUsername = MutableLiveData<Boolean>()

    val errotoastUsername: LiveData<Boolean>
        get() = _errorToastUsername

    private val _errorToastInvalidPassword = MutableLiveData<Boolean>()

    val errorToastInvalidPassword: LiveData<Boolean>
        get() = _errorToastInvalidPassword


    fun loginButton() {
        myScope.launch {
            val usersNames = repository.getEmailAddress(inputUsername.value!!)

            if (usersNames != null) {
                if (usersNames.password == inputPassword.value) {
                    inputUsername.value = null
                    inputPassword.value = null
                    _navigatetoHomeScreen.value = true
                    Log.e("ETTSTS","CLICK")

                } else {
                    _errorToastInvalidPassword.value = true
                }
            } else {
                _errorToastUsername.value = true
            }
        }

    }


    fun doneNavigatingUserDetails() {
        _navigatetoHomeScreen.value = false
    }


    fun donetoast() {
        _errorToast.value = false
        Log.i("MYTAG", "Done taoasting ")
    }


    fun donetoastErrorUsername() {
        _errorToastUsername.value = false
        Log.i("MYTAG", "Done taoasting ")
    }

    fun donetoastInvalidPassword() {
        _errorToastInvalidPassword.value = false
        Log.i("MYTAG", "Done taoasting ")
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }


}