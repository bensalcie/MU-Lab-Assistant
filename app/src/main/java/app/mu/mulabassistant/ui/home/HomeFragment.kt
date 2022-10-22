package app.mu.mulabassistant.ui.home

import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.mu.mulabassistant.R
import app.mu.mulabassistant.databinding.FragmentHomeBinding
import app.mu.mulabassistant.ui.home.adapters.EquipmentAdapter
import app.mu.mulabassistant.ui.home.models.Equipment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var equipmentDb: DatabaseReference
    private lateinit var equipmentAdapter:EquipmentAdapter
    private lateinit var equipmentsProgress:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        equipmentDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENT")
        equipmentsProgress = binding.equipmentsProgress


        val root: View = binding.root
        binding.addEquipmentFragment.setOnClickListener {
            findNavController().navigate(R.id.addEquipmentFragment)
        }
        attachEquipments()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun attachEquipments() {
        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.recyclergroupchat)
        val lmanager= LinearLayoutManager(binding.root.context)

        recyclerView.layoutManager = lmanager

        val options = FirebaseRecyclerOptions.Builder<Equipment>()
            .setQuery(equipmentDb.limitToLast(200), Equipment::class.java).build()
        equipmentAdapter = EquipmentAdapter(options)
        recyclerView.adapter = equipmentAdapter
        equipmentsProgress.visibility = View.GONE
        equipmentAdapter.startListening()


    }
}