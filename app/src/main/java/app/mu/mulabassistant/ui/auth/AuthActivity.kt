package app.mu.mulabassistant.ui.auth

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import app.mu.mulabassistant.MainActivity
import app.mu.mulabassistant.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
   private  lateinit var mAuth :FirebaseAuth
   private lateinit var etEmail:TextInputEditText
    private lateinit var etPassword:TextInputEditText
    private lateinit var btnLogin:ExtendedFloatingActionButton
    private lateinit var pd:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()

        mAuth= FirebaseAuth.getInstance()
        etEmail = findViewById(R.id.tvEmail)
        etPassword = findViewById(R.id.tvpassword)
        btnLogin = findViewById(R.id.btnLogin)

        pd = ProgressDialog(this)
        pd.setTitle("Login")
        pd.setMessage("Please wait as we log you in.")
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