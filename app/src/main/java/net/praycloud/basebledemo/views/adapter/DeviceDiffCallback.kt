package net.praycloud.basebledemo.views.adapter

import androidx.recyclerview.widget.DiffUtil
import net.praycloud.basebledemo.ble.scan_data.DiscoveredBluetoothDevice

class DeviceDiffCallback  : DiffUtil.Callback {
    private val oldList: List<DiscoveredBluetoothDevice>?
    private val newList: List<DiscoveredBluetoothDevice>?

    constructor(oldList: List<DiscoveredBluetoothDevice>?, newList: List<DiscoveredBluetoothDevice>?) {
        this.oldList = oldList
        this.newList = newList
    }

    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList!![oldItemPosition] === newList!![newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val device: DiscoveredBluetoothDevice = oldList!![oldItemPosition]
        return device.hasRssiLevelChanged()
    }

}