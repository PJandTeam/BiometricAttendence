package com.app.biometricattendence.register

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.*
import androidx.databinding.Observable
import com.app.biometricattendence.roomdb.RegisterEntity
import com.app.biometricattendence.roomdb.RegisterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegistrationViewModel(private val repository: RegisterRepository, application: Application) :
    AndroidViewModel(application), Observable {

    init {
        Log.i("MYTAG", "init")
    }

     var userdata: String? = null

    var userDetailsLiveData = MutableLiveData<Array<RegisterEntity>>()
    val _navigatePopupScreen = MutableLiveData<Boolean>()

    @Bindable
    val inputName = MutableLiveData<String?>()

    @Bindable
    val inputEmpID = MutableLiveData<String?>()

    @Bindable
    val inputDoB = MutableLiveData<String?>()

    @Bindable
    val inputDoJ = MutableLiveData<String?>()

    @Bindable
    val inputMobileNumber = MutableLiveData<String?>()

    @Bindable
    val inputTeam = MutableLiveData<String?>()

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


    fun submitButton(time:Long) {

            uiScope.launch {
//            withContext(Dispatchers.IO) {
                val usersNames = repository.getEmpID(inputEmpID.value!!)
                Log.i("MYTAG", usersNames.toString() + "------------------")
                if (usersNames != null) {
                    _errorToastUsername.value = true
                    Log.i("MYTAG", "Inside if Not null")
                } else {
                    Log.i("MYTAG", userDetailsLiveData.value.toString() + "ASDFASDFASDFASDF")
                    Log.i("MYTAG", "OK im in")
                    val name = inputName.value!!
                    val empId = inputEmpID.value!!
                    val dob = inputDoB.value!!
                    val doj = inputDoJ.value!!
                    val mobile = inputMobileNumber.value!!
                    val team = inputTeam.value!!
                    Log.i("MYTAG", "insidi Sumbit")
                    insert(RegisterEntity(name,empId,dob,doj,mobile,team,time))
                    inputName.value = null
                    inputEmpID.value = null
                    inputDoB.value = null
                    inputDoJ.value = null
                    inputMobileNumber.value = null
                    inputTeam.value = null
                    _navigateto.value = true
                    _navigatePopupScreen.value = true
                    userdata = empId
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

    private fun getEmpID(empId: String): Job = viewModelScope.launch {
        repository.getEmpID(empId)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}