package com.example.myapplication.authentication.register

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.Bindable
import androidx.lifecycle.*
import androidx.databinding.Observable
import com.example.myapplication.Utils
import com.example.myapplication.authentication.roomdb.RegisterEntity
import com.example.myapplication.authentication.roomdb.RegisterRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegistrationViewModel(private val repository: RegisterRepository, application: Application) :
    AndroidViewModel(application), Observable {

    init {
        Log.i("MYTAG", "init")
    }

    private var userdata: String? = null

    var userDetailsLiveData = MutableLiveData<Array<RegisterEntity>>()

    @Bindable
    val inputFirstName = MutableLiveData<String?>()

    @Bindable
    val inputLastName = MutableLiveData<String?>()

    @Bindable
    val inputEmailAddress = MutableLiveData<String?>()

    @Bindable
    val inputPassword = MutableLiveData<String?>()

    @Bindable
    val inputConPassword = MutableLiveData<String?>()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private val _navigateto = MutableLiveData<Boolean>()

    val navigateto: LiveData<Boolean>
        get() = _navigateto

    private val _errorToast = MutableLiveData<Boolean>()

    val errotoast: LiveData<Boolean>
        get() = _errorToast

    private val _errorToastUsername = MutableLiveData<Boolean>()

    val errotoastUsername: LiveData<Boolean>
        get() = _errorToastUsername


    fun submitButton() {

            uiScope.launch {
//            withContext(Dispatchers.IO) {
                val usersNames = repository.getEmailAddress(inputEmailAddress.value!!)
                Log.i("MYTAG", usersNames.toString() + "------------------")
                if (usersNames != null) {
                    _errorToastUsername.value = true
                    Log.i("MYTAG", "Inside if Not null")
                } else {
                    Log.i("MYTAG", userDetailsLiveData.value.toString() + "ASDFASDFASDFASDF")
                    Log.i("MYTAG", "OK im in")
                    val firstName = inputFirstName.value!!
                    val lastName = inputLastName.value!!
                    val email = inputEmailAddress.value!!
                    val password = inputPassword.value!!
                    Log.i("MYTAG", "insidi Sumbit")
                    insert(RegisterEntity(email, firstName, lastName, password))
                    inputFirstName.value = null
                    inputLastName.value = null
                    inputEmailAddress.value = null
                    inputPassword.value = null
                    _navigateto.value = true
                    Log.i("Registerrrrr", email)
                    Log.i("Registerrrrr", password)
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
    }

    fun doneNavigating() {
        _navigateto.value = false
        Log.i("MYTAG", "Done navigating ")
    }

    fun donetoast() {
        _errorToast.value = false
        Log.i("MYTAG", "Done taoasting ")
    }

    fun donetoastUserName() {
        _errorToast.value = false
        Log.i("MYTAG", "Done taoasting  username")
    }

    private fun insert(user: RegisterEntity): Job = viewModelScope.launch {
        repository.insert(user)
    }

    private fun getEmail(email: String): Job = viewModelScope.launch {
        repository.getEmailAddress(email)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}