package com.app.biometricattendence.homescreen

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.biometricattendence.roomdb.RegisterEntity
import com.app.biometricattendence.roomdb.RegisterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val repository: RegisterRepository, application: Application) :
    AndroidViewModel(application), Observable {

    var dbName = ""
    var dbEmpId = ""
    var dbDoB = ""
    var dbDoj = ""
    var dbMobile = ""
    var dbTeam = ""
    var dbTime = ""
    var dbTotalWorkedHours = ""


    private val viewModelJob = Job()
    private val myScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigatetoHomeScreen = MutableLiveData<Boolean>()

    val _navtoHomeScreen: LiveData<Boolean>
        get() = _navigatetoHomeScreen

    private val _errorToastUsername = MutableLiveData<Boolean>()

    fun getUserData(emp_id: String) {
        myScope.launch {
            val usersNames = repository.getEmpID(emp_id)

            if (usersNames != null) {
                dbName = usersNames.name
                dbEmpId = usersNames.empId
                dbDoB = usersNames.dob
                dbDoj = usersNames.doj
                dbMobile = usersNames.mobile
                dbTeam = usersNames.team
                dbTime = usersNames.time.toString()
                dbTotalWorkedHours = usersNames.total_worked_hours.toString()
                _navigatetoHomeScreen.value = true

            } else {
                _errorToastUsername.value = true
            }
        }
    }

    fun insert(user: RegisterEntity): Job = viewModelScope.launch {
        repository.insert(user)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }


}