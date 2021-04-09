package net.praycloud.basebledemo.ble.device_data

import no.nordicsemi.android.ble.data.Data

object DeviceData {
    private val LED_STATE_OFF: ByteArray = byteArrayOf(0x01,0x00)
    private val LED_STATE_ON: ByteArray = byteArrayOf(0x01,0x01)
    private val SOUND_STATE_OFF: ByteArray = byteArrayOf(0x02,0x00)
    private val SOUND_STATE_ON: ByteArray = byteArrayOf(0x02,0x01)

    fun ledTurn(on: Boolean): Data {
        return if(on){
            Data(LED_STATE_ON)
        }else{
            Data(LED_STATE_OFF)
        }
    }

    fun soundTurn(on: Boolean): Data {
        return if(on){
            Data(SOUND_STATE_ON)
        }else{
            Data(SOUND_STATE_OFF)
        }
    }


    fun ledDataWriteCallback(deviceState: DeviceState,data: Data):DeviceState{
        when (data.getIntValue(Data.FORMAT_UINT8, 0)){
            0x01->deviceState.LightState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            0x02->deviceState.SoundState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
        }
        return deviceState
    }

    fun ledDataWriteCallback(deviceId: String,data: Data):DeviceState{
        var deviceState = DeviceState(deviceId,0,0)
        when (data.getIntValue(Data.FORMAT_UINT8, 0)){
            0x01->deviceState.LightState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            0x02->deviceState.SoundState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
        }
        return deviceState
    }

    fun ledDataNotificationCallback(deviceState: DeviceState,data: Data):DeviceState{
        when (data.getIntValue(Data.FORMAT_UINT8, 0)){
            0x01->deviceState.LightState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            0x02->deviceState.SoundState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
        }
        return deviceState
    }

    fun ledDataNotificationCallback(deviceId: String,data: Data):DeviceState{
        var deviceState = DeviceState(deviceId,0,0)
        when (data.getIntValue(Data.FORMAT_UINT8, 0)){
            0x01->deviceState.LightState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            0x02->deviceState.SoundState = data.getIntValue(Data.FORMAT_UINT8, 1)!!
        }
        return deviceState
    }
}