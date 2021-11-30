package net.praycloud.basebledemo.views

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import net.praycloud.basebledemo.R
import net.praycloud.basebledemo.ble.MyBleManager
import net.praycloud.basebledemo.ble.scan_data.ScanState
import net.praycloud.basebledemo.ble.utils.BleUtils
import net.praycloud.basebledemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val REQUEST_ACCESS_FINE_LOCATION:Int = 1086 // random number
    private val REQUEST_BLE_LOCATION:Int = 1088 // random number

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var myBleManager: MyBleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBleManager = MyBleManager(application)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        findViewById<Button>(R.id.bt_permission).setOnClickListener { onGrantLocationPermissionClicked() }
        findViewById<Button>(R.id.bt_ble_permission).setOnClickListener {  onGrantBlePermissionClicked() }
        findViewById<Button>(R.id.bt_bluetooth).setOnClickListener { onEnableBluetoothClicked() }
        findViewById<Button>(R.id.bt_location).setOnClickListener { onEnableLocationClicked() }
        getMyBleManager().getScanState().observe(this,{scanStateRefresh(it)})
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            getMyBleManager().refresh()
        }
        if (requestCode == REQUEST_BLE_LOCATION) {
            getMyBleManager().refresh()
        }
    }

    private fun scanStateRefresh(scanState: ScanState){
        if(BleUtils.isBluetoothPermissionsGranted(this)){
            if(BleUtils.isLocationPermissionsGranted(this)){
                binding.noLocationPermission.content.visibility = View.GONE
                if(scanState.isBluetoothEnabled()){
                    binding.noBluetooth.content.visibility = View.GONE
                    getMyBleManager().startScan()
                    if (!scanState.hasRecords()){
                        if(!BleUtils.isLocationRequired(this)||BleUtils.isLocationEnabled(this)){
                            binding.noLocation.content.visibility = View.GONE
                        }else{
                            binding.noLocation.content.visibility = View.VISIBLE
                        }
                    }
                }else{
                    binding.noBluetooth.content.visibility = View.VISIBLE
                }
            }else{
                binding.noLocationPermission.content.visibility = View.VISIBLE
                binding.noBluetooth.content.visibility = View.GONE
                binding.noLocation.content.visibility = View.GONE
            }
        }else{
            binding.noBlePermission.content.visibility = View.GONE
        }

    }

    private fun onEnableLocationClicked() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun onEnableBluetoothClicked() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivity(enableIntent)
    }
    private fun onGrantBlePermissionClicked() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            BleUtils.markBlePermissionRequested(this)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLE_LOCATION)
        }
    }
    private fun onGrantLocationPermissionClicked() {
        BleUtils.markLocationPermissionRequested(this)
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
    }

    fun onPermissionSettingsClicked() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }

    fun getMyBleManager(): MyBleManager{
        return myBleManager
    }

    override fun onDestroy() {
        super.onDestroy()
        getMyBleManager().close()
    }
}