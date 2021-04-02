package net.praycloud.basebledemo.ble

import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*

object BleConfig {
    /**
     * 服务UUID
     * */
    val SERVICE_UUID = UUID.fromString("7BE6FFF0-5C0D-47E1-8710-6C407FD27865")
    /**
     * 写指令UUID
     * */
    val WRITE_CHAR = UUID.fromString("7BE6FFF1-5C0D-47E1-8710-6C407FD27865")
    /**
     * noti，UUID
     * */
    val NOTIFICATION_CHAR = UUID.fromString("7BE6FFF2-5C0D-47E1-8710-6C407FD27865")

    /**
     * 扫描结果反馈延迟时间(ms)
     * */
    val scanReportDelay = 500
    /**
     * 扫描模式
     * */
    val scanMode = ScanSettings.SCAN_MODE_LOW_LATENCY
    /**
     * 扫描过滤条件
     * */
    var scanFilters = ArrayList<ScanFilter>()


}