package net.praycloud.basebledemo.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import net.praycloud.basebledemo.R
import net.praycloud.basebledemo.ble.scan_data.DiscoveredBluetoothDevice
import net.praycloud.basebledemo.databinding.FragmentScanBinding
import net.praycloud.basebledemo.views.adapter.DevicesAdapter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }
    private lateinit var main:MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main= activity as MainActivity
        val adapter = DevicesAdapter(main,main.getMyBleManager().getDevices())
        adapter.setOnItemClickListener{onItemClick(it)}
        binding.recyclerViewBleDevices.adapter = adapter
        binding.recyclerViewBleDevices.layoutManager = LinearLayoutManager(main)
        binding.recyclerViewBleDevices.addItemDecoration(DividerItemDecoration(main, DividerItemDecoration.VERTICAL))
        val animator: ItemAnimator = binding.recyclerViewBleDevices.itemAnimator!!
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }
    private fun onItemClick(device: DiscoveredBluetoothDevice) {
        main.getMyBleManager().connectDevice(device)
        findNavController().navigate(R.id.action_connect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}