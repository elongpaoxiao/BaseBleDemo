package net.praycloud.basebledemo.views

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import net.praycloud.basebledemo.R
import net.praycloud.basebledemo.ble.device_data.DeviceData
import net.praycloud.basebledemo.databinding.FragmentDeviceBinding
import no.nordicsemi.android.ble.livedata.state.ConnectionState
import no.nordicsemi.android.ble.observer.ConnectionObserver

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root

    }
    private lateinit var main:MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main= activity as MainActivity
        main.getMyBleManager().setConnectionObserver(object:ConnectionObserver{
            override fun onDeviceConnecting(device: BluetoothDevice) {
                binding.tvOnlineState.text = getString(R.string.connecting)
            }

            override fun onDeviceConnected(device: BluetoothDevice) {
            }

            override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
            }

            override fun onDeviceReady(device: BluetoothDevice) {
                binding.tvOnlineState.text = getString(R.string.online)
            }

            override fun onDeviceDisconnecting(device: BluetoothDevice) {
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
                binding.tvOnlineState.text = getString(R.string.offline)
            }

        })
        main.getMyBleManager().deviceStates.observe(main,{
            if(!isHidden&&isVisible){
                binding.ledSwitch.isChecked = it.LightState==0
                binding.soundSwitch.isChecked = it.SoundState==0
            }
        })
        binding.ledSwitch.setOnCheckedChangeListener{
                buttonView: CompoundButton, isChecked: Boolean ->main.getMyBleManager().writeData(DeviceData.ledTurn(isChecked)) }
        binding.soundSwitch.setOnCheckedChangeListener{
                buttonView: CompoundButton, isChecked: Boolean ->main.getMyBleManager().writeData(DeviceData.soundTurn(isChecked)) }
        binding.buttonSecond.setOnClickListener {
            main.getMyBleManager().disconnectDevice()
            findNavController().navigate(R.id.action_disconnect)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}