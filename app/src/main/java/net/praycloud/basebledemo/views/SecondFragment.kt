package net.praycloud.basebledemo.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import net.praycloud.basebledemo.R
import net.praycloud.basebledemo.ble.device_data.DeviceData
import net.praycloud.basebledemo.databinding.FragmentSecondBinding
import no.nordicsemi.android.ble.livedata.state.ConnectionState

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }
    private lateinit var main:MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main= activity as MainActivity
        main.getMyBleManager().state.observe(main,{
            when(it.state){
                ConnectionState.State.CONNECTING->binding.tvOnlineState.text = getString(R.string.connecting)
                ConnectionState.State.READY->binding.tvOnlineState.text = getString(R.string.online)
                ConnectionState.State.DISCONNECTED->binding.tvOnlineState.text = getString(R.string.offline)
            }
        })
        main.getMyBleManager().deviceStates.observe(main,{
            binding.ledSwitch.isChecked = it.LightState==0
            binding.soundSwitch.isChecked = it.SoundState==0
        })
        binding.ledSwitch.setOnCheckedChangeListener{
                buttonView: CompoundButton, isChecked: Boolean ->main.getMyBleManager().writeData(DeviceData.ledTurn(isChecked)) }
        binding.soundSwitch.setOnCheckedChangeListener{
                buttonView: CompoundButton, isChecked: Boolean ->main.getMyBleManager().writeData(DeviceData.soundTurn(isChecked)) }
        binding.buttonSecond.setOnClickListener {
            main.getMyBleManager().disconnectDevice()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}