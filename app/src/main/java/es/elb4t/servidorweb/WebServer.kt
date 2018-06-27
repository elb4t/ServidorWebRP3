package es.elb4t.servidorweb

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader






class WebServer(port: Int, ctx: Context, listener: WebserverListener) : NanoHTTPD(port) {
    var ctx: Context? = ctx

    interface WebserverListener {
        //Boolean ledStatus = false;
        fun getLedStatus(): Boolean?

        fun switchLEDon()
        fun switchLEDoff()
    }

    private var listener: WebserverListener? = listener

    init {
        try {
            start()
            Log.i(TAG, "Webserver iniciado")
        } catch (ioe: IOException) {
            Log.e(TAG, "No ha sido posible iniciar el webserver", ioe)
        }
    }

    private fun readFile(): StringBuffer {
        var reader: BufferedReader? = null
        val buffer = StringBuffer()

        try {
            reader = BufferedReader(InputStreamReader(ctx?.assets?.open("home.html"), "UTF-8"))

            for (mLine in reader.readLines()) {
                buffer.append(mLine)
                buffer.append("\n")
            }
        } catch (ioe: IOException) {
            Log.e(TAG, "Error leyendo la página home", ioe)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error cerrando el reader", e)
                } finally {
                    reader = null
                }
            }
        }
        return buffer
    }

    override fun serve(session: IHTTPSession): Response {
        val parms = session.parameters
        // Analizamos los parámetros que ha modificado el usuario
        // Según estos parámetros, ejecutamos acciones en la RP3
        if (parms["on"] != null) {
            listener?.switchLEDon()
        } else if (parms["off"] != null) {
            listener?.switchLEDoff()
        }
        // Obtenemos la web original
        val preweb = readFile().toString()
        // Si queremos mostrar algún valor de salida, la modificamos
        // En este caso, sustituimos palabras clave por strings
        var postweb:String? = null
        if (listener?.getLedStatus()!!) {
            postweb = preweb.replace("#keytext", "ENCENDIDO")
            postweb = postweb.replace("#keycolor", "MediumSeaGreen")
            postweb = postweb.replace("#colorA", "#F2994A")
            postweb = postweb.replace("#colorB", "#F2C94C")
        } else {
            postweb = preweb.replace("#keytext", "APAGADO")
            postweb = postweb.replace("#keycolor", "Tomato")
            postweb = postweb.replace("#colorA", "#3e5151")
            postweb = postweb.replace("#colorB", "#decba4")
        }
        return newFixedLengthResponse(postweb)
    }
}