package app.mu.mulabassistant.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.mu.mulabassistant.R
import app.mu.mulabassistant.databinding.FragmentDashboardBinding
import app.mu.mulabassistant.ui.notifications.adapters.NotificationAdapter
import app.mu.mulabassistant.ui.notifications.models.EquipmentRequest
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    private lateinit var notificationsDb: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var notificationsAdapter: NotificationAdapter
    private lateinit var root:View



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)



        mAuth = FirebaseAuth.getInstance()
        notificationsDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENTREQUESTS").child(mAuth.uid!!)

        attachNotifications()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun attachNotifications() {


        val gmanager= LinearLayoutManager(root.context)
        val recyclernotifications = root.findViewById<RecyclerView>(R.id.recyclernotifications)
        recyclernotifications.layoutManager = gmanager



        val options = FirebaseRecyclerOptions.Builder<EquipmentRequest>()
            .setQuery(notificationsDb.limitToLast(200), EquipmentRequest::class.java).build()
        notificationsAdapter = NotificationAdapter(options)
        recyclernotifications.adapter = notificationsAdapter
        notificationsAdapter.startListening()








    }
}