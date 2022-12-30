package app.mu.mulabassistant.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.mu.mulabassistant.databinding.FragmentNotificationsBinding
import app.mu.mulabassistant.ui.notifications.adapters.NotificationAdapter
import app.mu.mulabassistant.ui.notifications.models.EquipmentRequest
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var notificationsDb: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var notificationsAdapter: NotificationAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        mAuth = FirebaseAuth.getInstance()
        notificationsDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENTREQUESTS").child(mAuth.currentUser!!.uid)

       // attachNotifications()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}