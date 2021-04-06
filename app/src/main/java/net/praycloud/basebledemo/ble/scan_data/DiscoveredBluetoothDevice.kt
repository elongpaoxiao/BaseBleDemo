package net.praycloud.basebledemo.ble.scan_data

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import no.nordicsemi.android.support.v18.scanner.ScanResult

class DiscoveredBluetoothDevice : Parcelable {
    val device: BluetoothDevice
    private var lastScanResult: ScanResult? = null
    var name: String? = null
        private set
    var rssi = 0
        private set
    private var previousRssi = 0

    /**
     * Returns the highest recorded RSSI value during the scan.
     *
     * @return Highest RSSI value.
     */
    var highestRssi = -128
        private set

    constructor(scanResult: ScanResult) {
        device = scanResult.device
        update(scanResult)
    }
    val address: String
        get() = device.address
    val scanResult: ScanResult
        get() = lastScanResult!!

    /**
     * This method returns true if the RSSI range has changed. The RSSI range depends on drawable
     * levels from [no.nordicsemi.android.blinky.R.drawable.ic_signal_bar].
     *
     * @return True, if the RSSI range has changed.
     */
    /* package */
    fun hasRssiLevelChanged(): Boolean {
        val newLevel =
            if (rssi <= 10) 0 else if (rssi <= 28) 1 else if (rssi <= 45) 2 else if (rssi <= 65) 3 else 4
        val oldLevel =
            if (previousRssi <= 10) 0 else if (previousRssi <= 28) 1 else if (previousRssi <= 45) 2 else if (previousRssi <= 65) 3 else 4
        return newLevel != oldLevel
    }

    /**
     * Updates the device values based on the scan result.
     *
     * @param scanResult the new received scan result.
     */
    fun update(scanResult: ScanResult) {
        lastScanResult = scanResult
        name = if (scanResult.scanRecord != null) scanResult.scanRecord!!.deviceName else null
        previousRssi = rssi
        rssi = scanResult.rssi
        if (highestRssi < rssi) highestRssi = rssi
    }

    fun matches(scanResult: ScanResult): Boolean {
        return device.address == scanResult.device.address
    }

    override fun hashCode(): Int {
        return device.hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (o is DiscoveredBluetoothDevice) {
            return device.address == o.device.address
        }
        return super.equals(o)
    }

    // Parcelable implementation
    private constructor(`in`: Parcel) {
        device = `in`.readParcelable(BluetoothDevice::class.java.classLoader)!!
        lastScanResult = `in`.readParcelable(ScanResult::class.java.classLoader)
        name = `in`.readString()
        rssi = `in`.readInt()
        previousRssi = `in`.readInt()
        highestRssi = `in`.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, flags)
        parcel.writeParcelable(lastScanResult, flags)
        parcel.writeString(name)
        parcel.writeInt(rssi)
        parcel.writeInt(previousRssi)
        parcel.writeInt(highestRssi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR: Creator<DiscoveredBluetoothDevice?> =
            object : Creator<DiscoveredBluetoothDevice?> {
                override fun createFromParcel(source: Parcel): DiscoveredBluetoothDevice? {
                    return DiscoveredBluetoothDevice(source)
                }

                override fun newArray(size: Int): Array<DiscoveredBluetoothDevice?> {
                    return arrayOfNulls(size)
                }
            }
    }
}