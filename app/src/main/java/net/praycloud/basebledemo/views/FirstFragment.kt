package net.praycloud.basebledemo.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import net.praycloud.basebledemo.R
import net.praycloud.basebledemo.ble.scan_data.DiscoveredBluetoothDevice
import net.praycloud.basebledemo.databinding.FragmentFirstBinding
import net.praycloud.basebledemo.views.adapter.DevicesAdapter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
    private lateinit var main:MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main= activity as MainActivity
        val adapter = DevicesAdapter(main,main.getMyBleManager().getDevices())
        adapter.setOnItemClickListener{onItemClick(it)}
        binding.recyclerViewBleDevices.adapter = adapter
        val animator: ItemAnimator = binding.recyclerViewBleDevices.itemAnimator!!
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        main.getMyBleManager().startScan()
    }
    private fun onItemClick(device: DiscoveredBluetoothDevice) {
        main.getMyBleManager().stopScan()
        main.getMyBleManager().connectDevice(device)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}