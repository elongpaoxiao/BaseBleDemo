package net.praycloud.basebledemo.ble

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import net.praycloud.basebledemo.ble.device_data.DeviceData
import net.praycloud.basebledemo.ble.device_data.DeviceState
import net.praycloud.basebledemo.ble.scan_data.DevicesLiveData
import net.praycloud.basebledemo.ble.scan_data.DiscoveredBluetoothDevice
import net.praycloud.basebledemo.ble.scan_data.ScanState
import net.praycloud.basebledemo.ble.utils.BleUtils
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.callback.DataSentCallback
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*

class MyBleManager(application: Application): BleManager(application) {
    val serviceUUID: UUID = UUID.fromString(BleConfig.SERVICE_UUID)
    val readWriteUUID: UUID = UUID.fromString(BleConfig.READ_WRITE_UUID)
    val notificationUUID: UUID = UUID.fromString(BleConfig.NOTIFICATION_UUID)
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notificationCharacteristic:BluetoothGattCharacteristic? = null

    val deviceStates: MutableLiveData<DeviceState> = MutableLiveData<DeviceState>()
    private var myApplication: Application = application

    /**
     * 初始化操作
     * */
    init{
        registerBroadcastReceivers(myApplication)
    }

    /**
     * 注销
     * */
    override fun close() {
        super.close()
        unregisterBroadcastReceivers(myApplication)
    }

    private var supported = false
    override fun getGattCallback(): BleManagerGattCallback {
        return MyBleManagerGattCallback()
    }

    private var device: BluetoothDevice? = null
    /**
     * 蓝牙连接接口
     * @param target the target device.
     */
    fun connectDevice(target: DiscoveredBluetoothDevice) {
        // Prevent from calling again when called again (screen orientation changed).
        if (device == null) {
            device = target.device
            reconnectDevice()
        }
    }

    /**
     * 重连接口.
     */
    fun reconnectDevice() {
        if (device != null) {
            connect(device!!)
                .retry(BleConfig.connectRetryCount, BleConfig.connectRetryDelay)
                .useAutoConnect(BleConfig.autoConnect)
                .enqueue()
        }
    }

    /**
     * 断开蓝牙设备接口
     */
    fun disconnectDevice() {
        device = null
        disconnect().enqueue()
    }

    /**
     * BluetoothGatt callbacks object.
     */
    private inner class MyBleManagerGattCallback : BleManagerGattCallback() {
        override fun initialize() {
            setNotificationCallback(notificationCharacteristic).with(notificationDataCallback)
            enableNotifications(notificationCharacteristic).enqueue()
        }

        public override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service = gatt.getService(serviceUUID)
            if (service != null) {
                writeCharacteristic = service.getCharacteristic(readWriteUUID)
                notificationCharacteristic = service.getCharacteristic(notificationUUID)
            }
            var writeRequest = false
            if (writeCharacteristic != null) {
                val rxProperties: Int = writeCharacteristic!!.properties
                writeRequest = rxProperties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0
            }
            supported = writeCharacteristic != null && notificationCharacteristic != null && writeRequest
            return supported
        }

        override fun onDeviceDisconnected() {
            device = null
            writeCharacteristic = null
            notificationCharacteristic = null
            //            notiStates.setValue(0);
        }

        override fun onServicesInvalidated() {

        }
    }

    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return !supported
    }
    /**
     * 读写数据回调接口
     * */
    private val writeDataCallback = DataSentCallback{ bluetoothDevice: BluetoothDevice, data: Data ->
        if(deviceStates.value==null){
            deviceStates.value = DeviceData.ledDataWriteCallback(bluetoothDevice.address,data)
        }else{
            deviceStates.value = DeviceData.ledDataWriteCallback(deviceStates.value!!,data)
        }
    }

    /**
     * 蓝牙设备主动上传数据监听接口
     * */
    private val notificationDataCallback = ProfileDataCallback{ bluetoothDevice: BluetoothDevice, data: Data ->
        if(deviceStates.value==null){
            deviceStates.value = DeviceData.ledDataNotificationCallback(bluetoothDevice.address,data)
        }else{
            deviceStates.value = DeviceData.ledDataNotificationCallback(deviceStates.value!!,data)
        }
    }

    /**
     * 写入数据
     * */
    fun writeData(data: Data){
        if (writeCharacteristic != null) {
            writeCharacteristic(writeCharacteristic, data).with(writeDataCallback).enqueue()
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////扫描相关逻辑/////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 扫描获取到的蓝牙对象数据
     */
    private var devicesLiveData: DevicesLiveData = DevicesLiveData()

    /**
     * 扫描状态
     */
    private var scannerStateLiveData:ScanState = ScanState(BleUtils.isBleEnabled(), BleUtils.isLocationEnabled(application))

    fun getDevices(): DevicesLiveData {
        return devicesLiveData
    }

    fun getScanState():ScanState{
        return scannerStateLiveData
    }

    fun refresh() {
        scannerStateLiveData.refresh()
    }
    /**
     * 开始扫描
     */
    fun startScan() {
        Log.i("startScan", scannerStateLiveData.isScanning().toString())
        if (scannerStateLiveData.isScanning()) {
            return
        }
        val settings = ScanSettings.Builder()
            .setScanMode(BleConfig.scanMode)
            .setReportDelay(BleConfig.scanReportDelay)
            .setUseHardwareBatchingIfSupported(BleConfig.useHardwareBatchingIfSupported)
            .build()
        BluetoothLeScannerCompat.getScanner().startScan(BleConfig.scanFilters, settings, scanCallback)
        scannerStateLiveData.scanningStarted()
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        if (scannerStateLiveData.isScanning() && scannerStateLiveData.isBluetoothEnabled()) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            scannerStateLiveData.scanningStopped()
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.i("onScanResult",result.toString())
            result.device
            if (devicesLiveData.deviceDiscovered(result)) {
                devicesLiveData.applyFilter()
                scannerStateLiveData.recordFound()
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            // This callback will be called only if the report delay set above is greater then 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required


            // This callback will be called only if the report delay set above is greater then 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if(results.size>=0){
                for (result in results)
                    devicesLiveData.deviceDiscovered(result)

                devicesLiveData.applyFilter()
                scannerStateLiveData.recordFound()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // TODO This should be handled
            scannerStateLiveData.scanningStopped()
        }
    }

    /**
     * 注册广播监听蓝牙开关与定位开关
     */
    private fun registerBroadcastReceivers(application: Application) {
        application.registerReceiver(
            bluetoothStateBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        if (BleUtils.isMarshmallowOrAbove()) {
            application.registerReceiver(
                locationProviderChangedReceiver,
                IntentFilter(LocationManager.MODE_CHANGED_ACTION)
            )
        }
    }
    /**
     * 注销广播监听
     */
    private fun unregisterBroadcastReceivers(application: Application) {
        application.unregisterReceiver(bluetoothStateBroadcastReceiver)
        if (BleUtils.isMarshmallowOrAbove()) {
            application.unregisterReceiver(locationProviderChangedReceiver)
        }
    }
    /**
     * Broadcast receiver to monitor the changes in the location provider.
     */
    private val locationProviderChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val enabled: Boolean = BleUtils.isLocationEnabled(context)
            scannerStateLiveData.setLocationEnabled(enabled)
        }
    }

    /**
     * Broadcast receiver to monitor the changes in the bluetooth adapter.
     */
    private val bluetoothStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            val previousState = intent.getIntExtra(
                BluetoothAdapter.EXTRA_PREVIOUS_STATE,
                BluetoothAdapter.STATE_OFF
            )
            when (state) {
                BluetoothAdapter.STATE_ON -> scannerStateLiveData.bluetoothEnabled()
                BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                    stopScan()
                    scannerStateLiveData.bluetoothDisabled()
                }
            }
        }
    }
}