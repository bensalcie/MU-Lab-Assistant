package com.mu.labassistant.studentapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mu.labassistant.studentapp.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var studentsDatabase:DatabaseReference
    private lateinit var pd:ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        pd= ProgressDialog(binding.root.context)
        pd.setTitle("Login in")
        pd.setMessage("Please wait as we log you in...")

        mAuth = FirebaseAuth.getInstance()
        studentsDatabase = FirebaseDatabase.getInstance().reference.child("MUSTUDENTS")

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.tvEmail.text.toString()
            val password = binding.tvpassword.text.toString()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(
                    binding.root.context,
                    "Please add a valid email.",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(TextUtils.isEmpty(password)){
                Toast.makeText(
                    binding.root.context,
                    "Please add a valid password.",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                //we can go ahead and login this user.
                pd.show()
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { loginResult->
                    if(loginResult.isSuccessful){
                        pd.dismiss()
                        //take to Home.
                        startActivity(Intent(binding.root.context,HomeActivity::class.java))
                        requireActivity().finish()

                    }else{
                        Toast.makeText(
                            binding.root.context,
                            loginResult.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        pd.dismiss()
                    }
                }
            }



        }
    }

    override fun onStart() {

        var currentuser = mAuth.currentUser
        if(currentuser!=null){
            //take to Home.
            startActivity(Intent(binding.root.context,HomeActivity::class.java))
            requireActivity().finish()
        }
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}