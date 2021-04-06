package net.praycloud.basebledemo.ble.scan_data

import androidx.lifecycle.LiveData

class ScanState: LiveData<ScanState> {
    private var scanningStarted = false
    private var hasRecords = false
    private var bluetoothEnabled = false
    private var locationEnabled = false

    /* package */
    constructor(
        bluetoothEnabled: Boolean,
        locationEnabled: Boolean
    ) {
        scanningStarted = false
        this.bluetoothEnabled = bluetoothEnabled
        this.locationEnabled = locationEnabled
        postValue(this)
    }

    /* package */
    fun refresh() {
        postValue(this)
    }

    /* package */
    fun scanningStarted() {
        scanningStarted = true
        postValue(this)
    }

    /* package */
    fun scanningStopped() {
        scanningStarted = false
        postValue(this)
    }

    /* package */
    fun bluetoothEnabled() {
        bluetoothEnabled = true
        postValue(this)
    }

    /* package */
    @Synchronized
    fun bluetoothDisabled() {
        bluetoothEnabled = false
        hasRecords = false
        postValue(this)
    }

    /* package */
    fun setLocationEnabled(enabled: Boolean) {
        locationEnabled = enabled
        postValue(this)
    }

    /* package */
    fun recordFound() {
        hasRecords = true
        postValue(this)
    }

    /**
     * Returns whether scanning is in progress.
     */
    fun isScanning(): Boolean {
        return scanningStarted
    }

    /**
     * Returns whether any records matching filter criteria has been found.
     */
    fun hasRecords(): Boolean {
        return hasRecords
    }

    /**
     * Returns whether Bluetooth adapter is enabled.
     */
    fun isBluetoothEnabled(): Boolean {
        return bluetoothEnabled
    }

    /**
     * Returns whether Location is enabled.
     */
    fun isLocationEnabled(): Boolean {
        return locationEnabled
    }

    /**
     * Notifies the observer that scanner has no records to show.
     */
    fun clearRecords() {
        hasRecords = false
        postValue(this)
    }
}