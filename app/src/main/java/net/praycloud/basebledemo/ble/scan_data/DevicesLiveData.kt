package net.praycloud.basebledemo.ble.scan_data

import androidx.lifecycle.LiveData
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*
import kotlin.collections.ArrayList

class DevicesLiveData : LiveData<MutableList<DiscoveredBluetoothDevice>> {
    private val devices: MutableList<DiscoveredBluetoothDevice> = ArrayList()
    private var filteredDevices: MutableList<DiscoveredBluetoothDevice>? = null

    /* package */
    constructor(){}

    /* package */
    @Synchronized
    fun bluetoothDisabled() {
        devices.clear()
        filteredDevices = null
        postValue(null)
    }

    /* package */
    @Synchronized
    fun deviceDiscovered(result: ScanResult): Boolean {
        val device: DiscoveredBluetoothDevice

        // Check if it's a new device.
        val index = indexOf(result)
        if (index == -1) {
            device = DiscoveredBluetoothDevice(result)
            devices.add(device)
        } else {
            device = devices[index]
        }

        // Update RSSI and name.
        device.update(result)

        // Return true if the device was on the filtered list or is to be added.
        return (filteredDevices != null && filteredDevices!!.contains(device))
    }

    /**
     * Clears the list of devices.
     */
    @Synchronized
    fun clear() {
        devices.clear()
        filteredDevices = null
        postValue(null)
    }

    /**
     * Refreshes the filtered device list based on the filter flags.
     */
    /* package */
    @Synchronized
    fun applyFilter(): Boolean {
        val tmp: MutableList<DiscoveredBluetoothDevice> = ArrayList()
        for (device in devices) {
            val result: ScanResult = device.scanResult
            tmp.add(device)
        }
        filteredDevices = tmp
        postValue(filteredDevices)
        return filteredDevices!!.isNotEmpty()
    }

    /**
     * Finds the index of existing devices on the device list.
     *
     * @param result scan result.
     * @return Index of -1 if not found.
     */
    private fun indexOf(result: ScanResult): Int {
        for ((i, device) in devices.withIndex()) {
            if (device.matches(result)) return i
        }
        return -1
    }
}