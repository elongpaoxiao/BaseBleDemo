package net.praycloud.basebledemo.ble

import android.content.Context
import android.os.Handler
import no.nordicsemi.android.ble.BleManager

class MyBleManager : BleManager {

    constructor(context: Context) : super(context){

    }
    constructor(context: Context, handler: Handler) : super(context, handler){

    }

    override fun getGattCallback(): BleManagerGattCallback {
        TODO("Not yet implemented")
    }
}