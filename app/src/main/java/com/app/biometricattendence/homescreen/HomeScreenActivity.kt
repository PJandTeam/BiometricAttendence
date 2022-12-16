package com.app.biometricattendence.homescreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
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
import com.app.biometricattendence.roomdb.RegisterRepository
import java.util.*

class HomeScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeScreenBinding
    private var statusPopup: android.app.AlertDialog? = null
    private var fullStatusPopup: android.app.AlertDialog? = null
    var id: String? = null
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private var mondayCalender: Calendar? = null
    var fridayCalender: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mondayCalender = Calendar.getInstance()
        fridayCalender = Calendar.getInstance()
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

    private fun showFullStatusPopup() {
        if (fullStatusPopup?.isShowing == true) {
            fullStatusPopup?.hide()
        }
        val popUpView = LayoutInflater.from(this).inflate(R.layout.inflate_full_status_popup, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(popUpView)
        fullStatusPopup = builder.create()
        fullStatusPopup?.setCanceledOnTouchOutside(true)
        mondayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        fridayCalender?.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        val dateFormat: java.text.DateFormat? = DateFormat.getDateFormat(this)
        val monday = mondayCalender?.time?.let { dateFormat?.format(it) }
        val friday = fridayCalender?.time?.let { dateFormat?.format(it) }
        val time = System.currentTimeMillis()
        val textCheckStatusName = popUpView.findViewById<TextView>(R.id.textCheckStatusName)
        val textDate = popUpView.findViewById<TextView>(R.id.textDate)
        val textDate2 = popUpView.findViewById<TextView>(R.id.textDate2)
        val textCurrentHours = popUpView.findViewById<TextView>(R.id.textCurrentHours)
        val textBalanceHours = popUpView.findViewById<TextView>(R.id.textBalanceHours)
        textCheckStatusName.text = "${homeScreenViewModel.dbName}, "
        textDate.text = monday
        textDate2.text = friday
        var workedHours: Long
        if (time in 1671161406025..1671175805108) {
            workedHours = (time / (1000 * 60 * 60) % 24) - 3
            textCurrentHours.text = workedHours.toString()
            textBalanceHours.text = (40 - workedHours).toString()
        } else if (time in 1671179407303..1671193806545) {
            workedHours = (time / (1000 * 60 * 60) % 24) - 4
            textCurrentHours.text = workedHours.toString()
            textBalanceHours.text = (40 - workedHours).toString()
        } else {
            textCurrentHours.text = "0"
            textBalanceHours.text = "40"
        }
        fullStatusPopup?.show()
        Handler(Looper.getMainLooper()).postDelayed({
            fullStatusPopup?.dismiss()
        }, 3000)
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
            }
        }
    }

    private fun instanceOfBiometricPrompt(
        context: Activity,
        frag: FragmentActivity, fromFullStatus: Boolean,
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        finishAffinity()
                    }
                    BiometricPrompt.ERROR_CANCELED -> {
                        finishAffinity()
                    }
                    BiometricConstants.ERROR_USER_CANCELED -> {
                        finishAffinity()
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
                    if (fromFullStatus) {
                        showFullStatusPopup()
                    } else {
                        showStatusPopup()
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
        val time = System.currentTimeMillis()
        //9am to 10:30am
        if (time in 1671161406025..1671166810730) {
            textGreetings.text = "Good Morning, Welcome "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "CHECK-IN"
        }//10:30am to 10:45am
        else if (time in 1671166810730..1671167709451) {
            textGreetings.text = "Have a nice tea, "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "It's Tea Time"
        }//10:45am to 1pm
        else if (time in 1671167709451..1671175805108) {
            textGreetings.text = "Good Morning, Welcome "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "CHECK-IN"
        }//1pm to 2pm
        else if (time in 1671175805108..1671179407303) {
            textGreetings.text = "Have a great lunch, "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "LUNCH BREAK"
        }//2pm to 4pm
        else if (time in 1671179407303..1671186606363) {
            textGreetings.text = "Good Afternoon, Welcome "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "CHECK-IN"
        }//4pm to 4:15pm
        else if (time in 1671186606363..1671187506691) {
            textGreetings.text = "Have a delicious snacks, "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "SNACKS TIME"
        }//4:15pm to 6pm
        else if (time in 1671187506691..1671193806545) {
            textGreetings.text = "Good Evening, Welcome "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "CHECK-IN"
        }//after 6pm
        else if (time > 1671193806545) {
            textGreetings.text = "Thank you, "
            textName.text = homeScreenViewModel.dbName
            textStatus.text = "CHECK-OUT"
        }else{
                textGreetings.text = "Thank you, "
                textName.text = homeScreenViewModel.dbName
                textStatus.text = "CHECK-OUT"

        }
        textEmployeeId.text = homeScreenViewModel.dbEmpId
        textTime.text = getCurrentTime()
        statusPopup?.show()
        Handler(Looper.getMainLooper()).postDelayed({
            statusPopup?.dismiss()
        }, 3000)
    }

    private fun getCurrentTime(): String {
        val delegate = "hh:mm aaa"
        return DateFormat.format(delegate, Calendar.getInstance().time).toString()
    }
}