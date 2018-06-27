package es.elb4t.servidorweb

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity(), WebServer.WebserverListener {

    private var server: WebServer? = null
    private val PIN_LED = "BCM18"
    var mLedGpio: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        server = WebServer(8180, this, this)
        val service = PeripheralManager.getInstance()
        try {
            mLedGpio = service.openGpio(PIN_LED)
            mLedGpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        } catch (e: IOException) {
            Log.e(TAG, "Error en el API PeripheralIO", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
        if (mLedGpio != null) {
            try {
                mLedGpio?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error en el API PeripheralIO", e)
            } finally {
                mLedGpio = null
            }
        }
    }

    override fun getLedStatus(): Boolean? {
        try {
            return mLedGpio?.value
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
            return false
        }

    }

    override fun switchLEDon() {
        try {
            mLedGpio?.value = true
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }

    override fun switchLEDoff() {
        try {
            mLedGpio?.value = false
            Log.i(TAG, "LED switched OFF")
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }

    }
}
