package com.app.biometricattendence.register

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.app.biometricattendence.R
import com.app.biometricattendence.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    private var submitAlertPopup: android.app.AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.btnSubmit.setOnClickListener { showSubmitPopUp() }
    }

    private fun showSubmitPopUp() {
        if (submitAlertPopup?.isShowing == true) {
            submitAlertPopup?.hide()
        }
        val popUpView = LayoutInflater.from(this).inflate(R.layout.inflate_submit_popup, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(popUpView)
        submitAlertPopup = builder.create()
        submitAlertPopup?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        submitAlertPopup?.setCanceledOnTouchOutside(false)
        val txtPopupSave = popUpView.findViewById<View>(R.id.txtPopupSave)
        val txtPopupCancel = popUpView.findViewById<View>(R.id.txtPopupCancel)
        txtPopupCancel?.setOnClickListener {
            submitAlertPopup?.dismiss()
        }
        submitAlertPopup?.show()
    }
}