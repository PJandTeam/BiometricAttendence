package com.app.biometricattendence.homescreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.app.biometricattendence.R
import com.app.biometricattendence.databinding.ActivityHomeScreenBinding
import com.app.biometricattendence.register.RegistrationActivity
import com.app.biometricattendence.roomdb.RegisterDatabase
import com.app.biometricattendence.roomdb.RegisterEntity
import com.app.biometricattendence.roomdb.RegisterRepository
import com.google.android.material.snackbar.Snackbar
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    private var statusPopup: android.app.AlertDialog? = null
    private var fullStatusPopup: android.app.AlertDialog? = null
    private var id: String? = null
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private var sundayCalender: Calendar? = null
    private var satDayCalender: Calendar? = null
    private var mondayCalender: Calendar? = null
    private var fridayCalender: Calendar? = null
    private var todayCalender: Calendar? = null
    private val timeNine = LocalTime.parse("09:00:00")
    private val timeTen = LocalTime.parse("10:00:00")
    private val timeTenFif = LocalTime.parse("10:15:00")
    private val timeOne = LocalTime.parse("13:00:00")
    private val timeTwo = LocalTime.parse("14:00:00")
    private val timeFour = LocalTime.parse("16:00:00")
    private val timeFourFif = LocalTime.parse("16:15:00")
    private val timeSix = LocalTime.parse("18:00:00")
    private var allowDateSave = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val application = requireNotNull(this).application
        val dao = RegisterDatabase.getInstance(application).registerDao
        val repository = RegisterRepository(dao)
        val factory = HomeScreenViewModelFactory(repository, application)
        homeScreenViewModel = ViewModelProvider(this, factory)[HomeScreenViewModel::class.java]
        binding.myHomeViewModel = homeScreenViewModel
        binding.lifecycleOwner = this
        initClickListeners()
        displayUserData()
    }

    private fun initClickListeners() {
        binding.cvNewEmployee.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            registerResult.launch(intent)
        }
        binding.llFingerHere.setOnClickListener {
            instanceOfBiometricPrompt(this, this, false)
                .authenticate(getPromptInfo())
        }
        binding.cvExistingEmployee.setOnClickListener {
            instanceOfBiometricPrompt(this, this, true)
                .authenticate(getPromptInfo())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showFullStatusPopup() {
        if (fullStatusPopup?.isShowing == true) {
            fullStatusPopup?.hide()
        }
        val popUpView = LayoutInflater.from(this).inflate(R.layout.inflate_full_status_popup, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(popUpView)
        fullStatusPopup = builder.create()
        fullStatusPopup?.setCanceledOnTouchOutside(true)
        mondayCalender = Calendar.getInstance()
        fridayCalender = Calendar.getInstance()
        sundayCalender = Calendar.getInstance()
        satDayCalender = Calendar.getInstance()
        todayCalender = Calendar.getInstance()
        sundayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        satDayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        mondayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        fridayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        todayCalender?.get(Calendar.DAY_OF_WEEK)
        val dateFormat: java.text.DateFormat? = DateFormat.getDateFormat(this)

        val saturday = satDayCalender?.time?.let { dateFormat?.format(it) }
        val sunday = sundayCalender?.time?.let { dateFormat?.format(it) }
        val monday = mondayCalender?.time?.let { dateFormat?.format(it) }
        val friday = fridayCalender?.time?.let { dateFormat?.format(it) }
        val today = todayCalender?.time?.let { dateFormat?.format(it) }

        val textCheckStatusName = popUpView.findViewById<TextView>(R.id.textCheckStatusName)
        val textDate = popUpView.findViewById<TextView>(R.id.textDate)
        val textDate2 = popUpView.findViewById<TextView>(R.id.textDate2)
        val textCurrentHours = popUpView.findViewById<TextView>(R.id.textCurrentHours)
        val textBalanceHours = popUpView.findViewById<TextView>(R.id.textBalanceHours)
        val txtTodayDate = popUpView.findViewById<TextView>(R.id.txtTodayDate)
        textCheckStatusName.text = "${homeScreenViewModel.dbName}, "
        textDate.text = monday
        textDate2.text = friday
        txtTodayDate.text = "($today)"
        val now = LocalTime.now(ZoneId.systemDefault())
        var workedHours: Int
        val totalWorkedHours = homeScreenViewModel.dbTotalWorkedHours.toLong()

        if (now.isAfter(timeNine) && now.isBefore(timeTwo)) {
            workedHours = (now.hour - 9)
            textCurrentHours.text = "$workedHours hrs"
            textBalanceHours.text = "${(40 - workedHours - totalWorkedHours)} hrs"
        } else if (now.isAfter(timeTwo) && now.isBefore(timeSix)) {
            workedHours = (now.hour - 9 - 1)
            textCurrentHours.text = "$workedHours hrs"
            textBalanceHours.text = "${(40 - workedHours - totalWorkedHours)} hrs"
        } else if (today == friday && now.isAfter(timeSix)) {
            val entity = RegisterEntity(homeScreenViewModel.dbName,
                homeScreenViewModel.dbEmpId,
                homeScreenViewModel.dbDoB,
                homeScreenViewModel.dbDoj,
                homeScreenViewModel.dbMobile,
                homeScreenViewModel.dbTeam,
                homeScreenViewModel.dbTime.toLong(),
                0)
            homeScreenViewModel.insert(entity)
            textCurrentHours.text = "8 hrs"
            textBalanceHours.text = "0 hrs"
        } else if (today == saturday || today == sunday) {
            textCurrentHours.text = "0 hrs"
            textBalanceHours.text = "${(40 - homeScreenViewModel.dbTotalWorkedHours.toInt())} hrs"
        } else if (now.isAfter(timeSix)) {
            if (allowDateSave) {
                workedHours = 8
                workedHours += homeScreenViewModel.dbTotalWorkedHours.toInt()
                val entity = RegisterEntity(homeScreenViewModel.dbName,
                    homeScreenViewModel.dbEmpId,
                    homeScreenViewModel.dbDoB,
                    homeScreenViewModel.dbDoj,
                    homeScreenViewModel.dbMobile,
                    homeScreenViewModel.dbTeam,
                    homeScreenViewModel.dbTime.toLong(),
                    workedHours)
                homeScreenViewModel.insert(entity)
                allowDateSave = false
            }
            textCurrentHours.text = "8 hrs"
            if (homeScreenViewModel.dbTotalWorkedHours.toInt() == 0) {
                textBalanceHours.text = "${(40 - 8)} hrs"
            } else {
                textBalanceHours.text =
                    "${(40 - homeScreenViewModel.dbTotalWorkedHours.toInt())} hrs"
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            fullStatusPopup?.show()
        }, 100)
        Handler(Looper.getMainLooper()).postDelayed({
            fullStatusPopup?.dismiss()
        }, 5100)
    }

    private var registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                id = data?.getStringExtra("EMP_ID")
                homeScreenViewModel.getUserData(id.toString())

                val editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit()
                editor.putString("id", id)
                editor.apply()
            }
        }

    private fun displayUserData() {
        homeScreenViewModel._navtoHomeScreen.observe(this) { hasFinished ->
            if (hasFinished == true) {
                Log.e("", "")
            }
        }
    }

    private fun instanceOfBiometricPrompt(
        context: Activity,
        frag: FragmentActivity, fromFullStatus: Boolean,
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            @SuppressLint("RestrictedApi")
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        Snackbar.make(binding.llFingerHere,
                            "No data found, please register",
                            Snackbar.LENGTH_SHORT).show()
                    }
                    BiometricPrompt.ERROR_CANCELED -> {
                        Snackbar.make(binding.llFingerHere,
                            "No data found, please register",
                            Snackbar.LENGTH_SHORT).show()
                    }
                    BiometricConstants.ERROR_USER_CANCELED -> {
                        Snackbar.make(binding.llFingerHere,
                            "No data found, please register",
                            Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE)
                id = prefs.getString("id", "")

                homeScreenViewModel.getUserData(id.toString())
                Handler(Looper.getMainLooper()).postDelayed({
                    if (id != "") {
                        if (fromFullStatus) {
                            showFullStatusPopup()
                        } else {
                            showStatusPopup()
                        }
                    } else {
                        Snackbar.make(binding.llFingerHere,
                            "No data found, please register",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }, 100)
            }
        }
        return BiometricPrompt(frag, executor, callback)
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Are you registered?")
            .setSubtitle("Please confirm your authentication to access the app")
            .setDeviceCredentialAllowed(true)
            .build()
    }

    @SuppressLint("SetTextI18n")
    fun showStatusPopup() {
        if (statusPopup?.isShowing == true) {
            statusPopup?.hide()
        }
        val popUpView = LayoutInflater.from(this).inflate(R.layout.inflate_status_popup, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(popUpView)
        statusPopup = builder.create()
        statusPopup?.setCanceledOnTouchOutside(true)
        val textGreetings = popUpView.findViewById<TextView>(R.id.textGreetings)
        val textName = popUpView.findViewById<TextView>(R.id.textName)
        val textStatus = popUpView.findViewById<TextView>(R.id.textStatus)
        val textEmployeeId = popUpView.findViewById<TextView>(R.id.textEmployeeId)
        val textTime = popUpView.findViewById<TextView>(R.id.textTime)

        val dateFormat: java.text.DateFormat? = DateFormat.getDateFormat(this)
        sundayCalender = Calendar.getInstance()
        satDayCalender = Calendar.getInstance()
        todayCalender = Calendar.getInstance()
        sundayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        satDayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        todayCalender?.get(Calendar.DAY_OF_WEEK)
        val today = todayCalender?.time?.let { dateFormat?.format(it) }
        val saturday = satDayCalender?.time?.let { dateFormat?.format(it) }
        val sunday = sundayCalender?.time?.let { dateFormat?.format(it) }
        val now = LocalTime.now(ZoneId.systemDefault())
        if (today == saturday || today == sunday) {
            textGreetings.text = ", Welcome "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "No working day"
        } else {
            //9am to 10:30am
            if (now.isAfter(timeNine) && now.isBefore(timeTen)) {
                textGreetings.text = "Good Morning, Welcome "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-IN"
                allowDateSave = true
            }//10:30am to 10:45am
            else if (now.isAfter(timeTen) && now.isBefore(timeTenFif)) {
                textGreetings.text = "Have a nice tea, "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "It's Tea Time"
            }//10:45am to 1pm
            else if (now.isAfter(timeTenFif) && now.isBefore(timeOne)) {
                textGreetings.text = "Good Morning, Welcome "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-IN"
            }//1pm to 2pm
            else if (now.isAfter(timeOne) && now.isBefore(timeTwo)) {
                textGreetings.text = "Have a great lunch, "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "LUNCH BREAK"
            }//2pm to 4pm
            else if (now.isAfter(timeTwo) && now.isBefore(timeFour)) {
                textGreetings.text = "Good Afternoon, Welcome "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-IN"
            }//4pm to 4:15pm
            else if (now.isAfter(timeFour) && now.isBefore(timeFourFif)) {
                textGreetings.text = "Have a delicious snacks, "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "SNACKS TIME"
            }//4:15pm to 6pm
            else if (now.isAfter(timeFourFif) && now.isBefore(timeSix)) {
                textGreetings.text = "Good Evening, Welcome "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-IN"
            }//after 6pm
            else if (now.isAfter(timeSix)) {
                textGreetings.text = "Thank you, "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-OUT"
            } else {
                textGreetings.text = "Thank you, "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-OUT"
            }
        }
        textEmployeeId.text = homeScreenViewModel.dbEmpId
        textTime.text = getCurrentTime()
        statusPopup?.show()
        Handler(Looper.getMainLooper()).postDelayed({
            statusPopup?.dismiss()
        }, 5000)
    }

    private fun getCurrentTime(): String {
        val delegate = "hh:mm aaa"
        return DateFormat.format(delegate, Calendar.getInstance().time).toString()
    }
}