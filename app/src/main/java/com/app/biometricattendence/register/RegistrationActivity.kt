package com.example.myapplication.authentication.register

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.Utils.Companion.showErrorSnack
import com.example.myapplication.Utils.Companion.showKeyboard
import com.example.myapplication.databinding.ActivityRegistrationBinding
import com.example.myapplication.authentication.roomdb.RegisterDatabase
import com.example.myapplication.authentication.roomdb.RegisterRepository
import com.google.android.material.snackbar.Snackbar

class RegistrationActivity : AppCompatActivity() {
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
        initSpannableClick()
        showErrorMessages()
        passwordVisibility()
    }

    private fun passwordVisibility() {
        binding.imgPassVisibility.setOnClickListener {
            if (binding.edtRegisterPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.imgPassVisibility.setImageResource(R.drawable.ic_baseline_visibility_24)
                binding.edtRegisterPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.imgPassVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                binding.edtRegisterPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
            binding.edtRegisterPassword.setSelection(binding.edtRegisterPassword.text.toString().length)
        }

        binding.imgConPassVisibility.setOnClickListener {
            if (binding.edtRegisterConPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.imgConPassVisibility.setImageResource(R.drawable.ic_baseline_visibility_24)
                binding.edtRegisterConPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.imgConPassVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                binding.edtRegisterConPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
            binding.edtRegisterConPassword.setSelection(binding.edtRegisterConPassword.text.toString().length)
        }
    }

    //Showing error toast and navigation
    private fun showErrorMessages() {
        registrationViewModel.navigateto.observe(this) { hasFinished ->
            if (hasFinished == true) {
                onBackPressed()
                registrationViewModel.doneNavigating()
            }
        }

        registrationViewModel.userDetailsLiveData.observe(this) {

        }

        registrationViewModel.errotoast.observe(this) { hasError ->
            if (hasError == true) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                registrationViewModel.donetoast()
            }
        }

        registrationViewModel.errotoastUsername.observe(this) { hasError ->
            if (hasError == true) {
                Toast.makeText(this, "Email already exist", Toast.LENGTH_SHORT).show()
                registrationViewModel.donetoastUserName()
            }
        }
    }

    //Button Clicks
    private fun initClicks() {
        binding.imgRegisterBack.setOnClickListener {
            onBackPressed()
        }
        binding.llRegisterButton.setOnClickListener {
            validateFields()
        }
    }

    //Validate register button click
    private fun validateFields() {
        val firstName = binding.edtRegisterFirstName.text?.trim()?.toString()
        val lastName = binding.edtRegisterLastName.text?.trim()?.toString()
        val emailAddress = binding.edtRegisterEmail.text?.trim()?.toString()
        val password = binding.edtRegisterPassword.text?.trim()?.toString()
        val conPassword = binding.edtRegisterConPassword.text?.trim()?.toString()

        if ((firstName?.length ?: 0) < 1) {
            showErrorSnack("First Name is Required", binding.root, this)
            binding.edtRegisterFirstName.requestFocus()
            showKeyboard(this)
        } else if ((lastName?.length ?: 0) < 1) {
            showErrorSnack("Last Name is Required", binding.root, this)
            binding.edtRegisterLastName.requestFocus()
            showKeyboard(this)
        } else if (!Utils.isEmailValid(emailAddress ?: "")) {
            showErrorSnack("Email Address Is Invalid", binding.root, this)
            binding.edtRegisterEmail.requestFocus()
            showKeyboard(this)
        } else if ((password?.length ?: 0) < 8 || (password?.length ?: 0) > 15) {
            showErrorSnack("Password must contains 8-15 characters", binding.root, this)
            binding.edtRegisterPassword.requestFocus()
            showKeyboard(this)
        } else if (!(password.equals(conPassword, ignoreCase = true))) {
            binding.edtRegisterConPassword.requestFocus()
            showKeyboard(this)
            showErrorSnack("Password and Confirm Password does not match", binding.root, this)
        } else if (!Utils.isPassValid(password ?: "")) {
            binding.edtRegisterPassword.requestFocus()
            showKeyboard(this)
            showErrorSnack(
                "Password must contain a number, a lowercase letter and a uppercase letter",
                binding.root,
                this
            )
        } else {
            registrationViewModel.submitButton()
        }
    }

    //Displaying spannable text
    private fun initSpannableClick() {
        val span = SpannableString("Already have a account? Sign in")
        val cs: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                onBackPressed()
            }
        }
        span.setSpan(cs, 24, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            24,
            31,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.txtDontHaveAcc.let {
            it.text = span
            it.setTextColor(ContextCompat.getColor(this, R.color.grey))
            it.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}