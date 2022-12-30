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
import com.mu.labassistant.studentapp.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var studentsDatabase: DatabaseReference
    private lateinit var pd: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        pd= ProgressDialog(binding.root.context)
        pd.setTitle("Creating Account")
        pd.setMessage("Please wait as we create an account for you...")

        mAuth = FirebaseAuth.getInstance()
        studentsDatabase = FirebaseDatabase.getInstance().reference.child("MUAPP/MUSTUDENTS")

        binding.btnSignUp.setOnClickListener {
            val name = binding.tvName.text.toString()

            val email = binding.tvEmail.text.toString()
            val password = binding.tvpassword.text.toString()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(
                    binding.root.context,
                    "Please add a valid email.",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(TextUtils.isEmpty(name)){
                Toast.makeText(
                    binding.root.context,
                    "Please add a valid name.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else if(TextUtils.isEmpty(password)){
                Toast.makeText(
                    binding.root.context,
                    "Please add a valid password.",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                //we can go ahead and login this user.
                pd.show()
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { registerResult->
                    if(registerResult.isSuccessful){

                        //continue with adding this user to the db.


                        //prepare data.
                        val user = HashMap<String,Any>()
                        user["name"] = name
                        user["email"] = email
                        user["datejoined"] = System.currentTimeMillis()
                        user["uid"] = mAuth.currentUser!!.uid
                        studentsDatabase.child(mAuth.currentUser!!.uid).updateChildren(user).addOnCompleteListener { databaseOperation->
                            if (databaseOperation.isSuccessful){
                                pd.dismiss()

                                //take to Home.
                                startActivity(Intent(binding.root.context,HomeActivity::class.java))
                                requireActivity().finish()

                            }else{
                                pd.dismiss()
                                Toast.makeText(
                                    binding.root.context,
                                    databaseOperation.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                pd.dismiss()
                            }
                        }



                    }else{
                        Toast.makeText(
                            binding.root.context,
                            registerResult.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        pd.dismiss()
                    }
                }
            }



        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}