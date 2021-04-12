package net.praycloud.basebledemo.views.adapter

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.praycloud.basebledemo.R
import net.praycloud.basebledemo.ble.scan_data.DevicesLiveData
import net.praycloud.basebledemo.ble.scan_data.DiscoveredBluetoothDevice
import net.praycloud.basebledemo.views.MainActivity
import java.util.*

class DevicesAdapter : RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private var devices: List<DiscoveredBluetoothDevice> = listOf()
    private lateinit var onItemClickListener: OnItemClickListener

    fun interface OnItemClickListener {
        fun onItemClick(device: DiscoveredBluetoothDevice)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutView = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return ViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices!![position]
        val deviceName: String? = device.name
        if (!TextUtils.isEmpty(deviceName)) holder.deviceName.text =
            deviceName else holder.deviceName.setText(R.string.unknown_device)
        holder.deviceAddress.setText(device.address)
        val rssiPercent = (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f))
        holder.rssi.setImageLevel(rssiPercent.toInt())
    }

    override fun getItemId(position: Int): Long {
        return devices[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return devices.size ?: 0
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    inner class ViewHolder  : RecyclerView.ViewHolder {

        var deviceAddress: TextView
        var deviceName: TextView
        var rssi: ImageView

        constructor(view: View):super(view) {
            deviceAddress = view.findViewById(R.id.device_address)
            deviceName = view.findViewById(R.id.device_name)
            rssi = view.findViewById(R.id.rssi)
            view.findViewById<View>(R.id.device_container).setOnClickListener { v: View? ->
                if (onItemClickListener != null) {
                    onItemClickListener!!.onItemClick(devices!![adapterPosition])
                }
            }
        }
    }

    constructor(
        activity: MainActivity,
        devicesLiveData: DevicesLiveData
    ){
        setHasStableIds(true)
        devicesLiveData.observe(activity) { newDevices ->
            val result = DiffUtil.calculateDiff(
                DeviceDiffCallback(devices, newDevices), false
            )
            devices = newDevices
            Log.i("deviceRe","devices:"+devices.size)
            result.dispatchUpdatesTo(this)
        }
    }
}