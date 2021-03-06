package net.praycloud.basebledemo.ble

import android.os.ParcelUuid
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*
import kotlin.collections.ArrayList

object BleConfig {
    /**
     * 服务UUID
     * */
    const val SERVICE_UUID = "1086FFF0-5C0D-47E1-8710-6C407FD27865"
    /**
     * 写指令UUID
     * */
    const val READ_WRITE_UUID = "1086FFF1-5C0D-47E1-8710-6C407FD27865"
    /**
     * noti，UUID
     * */
    const val NOTIFICATION_UUID = "1086FFF2-5C0D-47E1-8710-6C407FD27865"

    /**
     * 重连尝试次数
     * */
    const val connectRetryCount = 3

    /**
     * 重连延迟时间
     * */
    const val connectRetryDelay = 100

    const val autoConnect = false

    /**
     * 扫描结果反馈延迟时间(ms)
     * */
    const val scanReportDelay:Long = 500
    /**
     * 扫描模式
     * */
    const val scanMode = ScanSettings.SCAN_MODE_LOW_LATENCY

    const val useHardwareBatchingIfSupported = false
    /**
     * 扫描过滤条件,若不过滤则设置为null
     * */
    var scanFilters: ArrayList<ScanFilter>? = arrayListOf(ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(SERVICE_UUID)).build())


}