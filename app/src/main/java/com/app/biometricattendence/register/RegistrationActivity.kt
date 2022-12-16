package com.app.biometricattendence.register

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.app.biometricattendence.databinding.ActivityRegistrationBinding
import com.app.biometricattendence.homescreen.HomeScreenActivity
import com.app.biometricattendence.roomdb.RegisterDatabase
import com.app.biometricattendence.roomdb.RegisterRepository
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    private var submitAlertPopup: android.app.AlertDialog? = null
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var registrationViewModel: RegistrationViewModel
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        val application = requireNotNull(this).application

        val dao = RegisterDatabase.getInstance(application).registerDao

        val repository = RegisterRepository(dao)

        val factory = RegisterViewModelFactory(repository, application)
        registrationViewModel = ViewModelProvider(this, factory)[RegistrationViewModel::class.java]
        binding.myViewModel = registrationViewModel

        binding.lifecycleOwner = this
        initClicks()
        showSubmitPopUp()
//        showErrorMessages()
    }

    //Showing error toast and navigation
//    private fun showErrorMessages() {
//        registrationViewModel.navigateto.observe(this) { hasFinished ->
//            if (hasFinished == true) {
//                onBackPressed()
//                registrationViewModel.doneNavigating()
//            }
//        }
//
//        registrationViewModel.userDetailsLiveData.observe(this) {
//
//        }
//
//        registrationViewModel.errotoast.observe(this) { hasError ->
//            if (hasError == true) {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//                registrationViewModel.donetoast()
//            }
//        }
//
//        registrationViewModel.errotoastUsername.observe(this) { hasError ->
//            if (hasError == true) {
//                Toast.makeText(this, "Email already exist", Toast.LENGTH_SHORT).show()
//                registrationViewModel.donetoastUserName()
//            }
//        }
//    }

    //Button Clicks
    private fun initClicks() {
        binding.llRegisterButton.setOnClickListener {
            validateFields()
        }
        binding.edtDoB.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    binding.edtDoB.setText(dat)
                }, year, month, day
            )
            datePickerDialog.show()
        }
        binding.edtDoJ.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    binding.edtDoJ.setText(dat)
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    //Validate register button click
    private fun validateFields() {
        val name = binding.edtRegisterName.text?.trim()?.toString()
        val empId = binding.edtEmpId.text?.trim()?.toString()
        val dob = binding.edtDoB.text?.trim()?.toString()
        val doj = binding.edtDoJ.text?.trim()?.toString()
        val mobile = binding.edtMobileNumber.text?.trim()?.toString()
        val team = binding.edtTeam.text?.trim()?.toString()

        if ((name?.length ?: 0) < 1) {
            binding.edtRegisterName.requestFocus()
            binding.edtRegisterName.error = "Please enter your name"
        } else if ((empId?.length ?: 0) < 1) {
            binding.edtEmpId.requestFocus()
            binding.edtEmpId.error = "Please enter your Employee ID"
        } else if ((dob?.length ?: 0) < 1) {
            binding.edtDoB.requestFocus()
            binding.edtDoB.error = "Please select your Date of Birth"
        }
        else if ((doj?.length ?: 0) < 1) {
            binding.edtDoJ.requestFocus()
            binding.edtDoJ.error = "Please select you Date of Joining"
        }

        else if ((mobile?.length ?: 0) < 1) {
            binding.edtMobileNumber.requestFocus()
            binding.edtMobileNumber.error = "Please enter your Mobile Number"
        } else if ((team?.length ?: 0) < 1) {
            binding.edtTeam.requestFocus()
            binding.edtTeam.error = "Please enter your team name"
        } else {
            val time = System.currentTimeMillis()
            registrationViewModel.submitButton(time)
        }
        }

        fun showSubmitPopUp() {
            registrationViewModel._navigatePopupScreen.observe(this) { hasFinished ->
                if (hasFinished == true) {
//                    if (submitAlertPopup?.isShowing == true) {
//                        submitAlertPopup?.hide()
//                    }
//                    val popUpView =
//                        LayoutInflater.from(this).inflate(R.layout.inflate_submit_popup, null)
//                    val builder = android.app.AlertDialog.Builder(this)
//                    builder.setView(popUpView)
//                    submitAlertPopup = builder.create()
//                    submitAlertPopup?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    submitAlertPopup?.setCanceledOnTouchOutside(false)
//                    val txtPopupSave = popUpView.findViewById<View>(R.id.txtPopupSave)
//                    val txtPopupCancel = popUpView.findViewById<View>(R.id.txtPopupCancel)
//                    txtPopupCancel?.setOnClickListener {
//                        submitAlertPopup?.dismiss()
//                    }
//                    txtPopupSave?.setOnClickListener {
//                        val intent = Intent(this, HomeScreenActivity::class.java)
//                        intent.putExtra("EMP_ID", registrationViewModel.userdata)
//                        setResult(RESULT_OK, intent)
//                        finish()
//                    }
//                    submitAlertPopup?.show()
                    instanceOfBiometricPrompt(this,this).authenticate(getPromptInfo())
                }
            }
        }

    private fun instanceOfBiometricPrompt(
        context: Activity,
        frag: FragmentActivity,
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        Toast.makeText(applicationContext,"Authentication failed",Toast.LENGTH_SHORT).show()
                    }
                    BiometricPrompt.ERROR_CANCELED -> {
                        Toast.makeText(applicationContext,"Authentication failed",Toast.LENGTH_SHORT).show()
                    }
                    BiometricConstants.ERROR_USER_CANCELED -> {
                        Toast.makeText(applicationContext,"Authentication failed",Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val intent = Intent(applicationContext, HomeScreenActivity::class.java)
                        intent.putExtra("EMP_ID", registrationViewModel.userdata)
                        setResult(RESULT_OK, intent)
                        finish()
            }
        }
        return BiometricPrompt(frag, executor, callback)
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication")
            .setSubtitle("Please authenticate to save your data")
            .setDeviceCredentialAllowed(true)
            .build()
    }

    }