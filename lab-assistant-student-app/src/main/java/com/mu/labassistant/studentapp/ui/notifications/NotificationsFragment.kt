package com.mu.labassistant.studentapp.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mu.labassistant.studentapp.databinding.FragmentNotificationsBinding
import com.mu.labassistant.studentapp.ui.home.adapters.EquipmentAdapter
import com.mu.labassistant.studentapp.ui.home.adapters.NotificationAdapter
import com.mu.labassistant.studentapp.ui.home.models.Equipment
import com.mu.labassistant.studentapp.ui.home.models.EquipmentRequest

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var notificationsDb: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var notificationsAdapter: NotificationAdapter




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mAuth = FirebaseAuth.getInstance()
        notificationsDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENTREQUESTS").child(mAuth.currentUser!!.uid)

        attachNotifications()

        // val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//
//        }
//

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notificationsAdapter.stopListening()
       // _binding = null
    }


    private fun attachNotifications() {


        val gmanager= LinearLayoutManager(binding.root.context)

        binding.recyclernotifications.layoutManager = gmanager

        val options = FirebaseRecyclerOptions.Builder<EquipmentRequest>()
            .setQuery(notificationsDb.limitToLast(200), EquipmentRequest::class.java).build()
        notificationsAdapter = NotificationAdapter(options)
        binding.recyclernotifications.adapter = notificationsAdapter
        notificationsAdapter.startListening()








    }
}