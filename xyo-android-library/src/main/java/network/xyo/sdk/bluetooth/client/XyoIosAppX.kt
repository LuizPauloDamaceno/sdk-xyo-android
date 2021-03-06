package network.xyo.sdk.bluetooth.client

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import network.xyo.ble.generic.devices.XYBluetoothDevice
import network.xyo.ble.generic.devices.XYCreator
import network.xyo.ble.generic.scanner.XYScanResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@kotlin.ExperimentalUnsignedTypes
open class XyoIosAppX : XyoBluetoothClient {

    constructor(context: Context, scanResult: XYScanResult, hash: String) : super(context, scanResult, hash)

    constructor(context: Context, scanResult: XYScanResult, hash: String, transport: Int) : super(context, scanResult, hash, transport)

    companion object : XYCreator() {

        fun enable(enable: Boolean) {
            if (enable) {
                xyoManufactureIdToCreator[XyoBluetoothClientDeviceType.IosAppX.raw] = this
            } else {
                xyoManufactureIdToCreator.remove(XyoBluetoothClientDeviceType.IosAppX.raw)
            }
        }

        override fun getDevicesFromScanResult(
                context: Context,
                scanResult: XYScanResult,
                globalDevices: ConcurrentHashMap<String, XYBluetoothDevice>,
                foundDevices: HashMap<String,
                        XYBluetoothDevice>
        ) {
            val hash = hashFromScanResult(scanResult)

            val existingDevice = globalDevices[hash]
            if (existingDevice != null) {
                existingDevice.rssi = scanResult.rssi
                existingDevice.updateBluetoothDevice(scanResult.device)
            } else {
                val createdDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    XyoIosAppX(context, scanResult, hash, BluetoothDevice.TRANSPORT_LE)
                } else {
                    XyoIosAppX(context, scanResult, hash)
                }
                foundDevices[hash] = createdDevice
                globalDevices[hash] = createdDevice
            }
        }
    }
}