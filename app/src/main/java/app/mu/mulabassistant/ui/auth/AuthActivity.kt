package app.mu.mulabassistant.ui.auth

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import app.mu.mulabassistant.MainActivity
import app.mu.mulabassistant.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
   private  lateinit var mAuth :FirebaseAuth
   private lateinit var etEmail:TextInputEditText
    private lateinit var etPassword:TextInputEditText
    private lateinit var btnLogin:ExtendedFloatingActionButton
    private lateinit var pd:ProgressDialog
    private lateinit var tvForgotPassword:MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()

        mAuth= FirebaseAuth.getInstance()
        etEmail = findViewById(R.id.tvEmail)
        etPassword = findViewById(R.id.tvpassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        pd = ProgressDialog(this)
        pd.setTitle("Login")
        pd.setMessage("Please wait as we log you in.")
        tvForgotPassword.setOnClickListener {
            val resetPasswordDialog = Dialog(this,R.style.Theme_MULabAssistant_AppBarOverlay)
            val window: Window? =resetPasswordDialog.window
            window?.setGravity(Gravity.CENTER)
            window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
            resetPasswordDialog.setContentView(R.layout.resetapassword_dialog)

            val btnReset = resetPasswordDialog.findViewById<ExtendedFloatingActionButton>(R.id.btnReset)
            val etResetEmail = resetPasswordDialog.findViewById<TextInputEditText>(R.id.etResetEmail)
            btnReset.setOnClickListener {
                val email = etResetEmail.text.toString().trim()
                if(!TextUtils.isEmpty(email)){
                    pd.setTitle("Reset password")
                    pd.setMessage("Please wait as we process your request")
                    pd.show()
                    //add implementation to reset password and dismiss.
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener { resetTask->
                        if(resetTask.isSuccessful){
                            pd.dismiss()
                            resetPasswordDialog.dismiss()
                            Toast.makeText(
                                this,
                                "Please check your email to reset password.",
                                Toast.LENGTH_LONG
                            ).show()
                        }else{
                            pd.dismiss()

                            Toast.makeText(this, resetTask.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }

                }else{
                    Toast.makeText(this, "Please add a valid email.", Toast.LENGTH_SHORT).show()
                }
            }

            resetPasswordDialog.show()
        }
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                pd.show()

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {loginRes->
                    pd.dismiss()

                    if (loginRes.isSuccessful){
                        //lead to main page
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setMessage(loginRes.exception?.message)
                        alertDialog.show()

                    }

                }

            }else{
                Snackbar.make(it,"Check your email or password",Snackbar.LENGTH_LONG).show()
            }

        }





    }


}