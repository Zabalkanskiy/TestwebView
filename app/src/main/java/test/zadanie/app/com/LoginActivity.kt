package test.zadanie.app.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        val submitButton: Button = findViewById(R.id.btn_submit)
        var et_user_name: EditText = findViewById(R.id.et_user_name)
        var et_password : EditText = findViewById(R.id.et_password)
        submitButton.setOnClickListener{
            //enter in Application code
            Toast.makeText(this, et_user_name.toString(), Toast.LENGTH_LONG).show()
        }
        val resetButton: Button = findViewById(R.id.btn_reset)
        resetButton.setOnClickListener {
            et_user_name.setText("")
            et_password.setText("")

        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // showDialog()
            finish()
            //showing dialog and then closing the application..

        }
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setTitle("are you sure?")
            setMessage("want to close the application ?")
            setPositiveButton("Yes") { _, _ -> finish() }
            setNegativeButton("No", null)
            show()
        }
    }

}