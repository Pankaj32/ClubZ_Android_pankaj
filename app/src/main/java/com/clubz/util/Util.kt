package com.clubz.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.View
import java.io.IOException
import java.nio.charset.Charset
import android.support.design.widget.Snackbar;

/**
 * Created by mindiii on 2/6/18.
 */
class Util {

    companion object {
        fun e(tag: String, response: String) { Log.e(tag,response);}

        /**
         *
         * @param context
         * @param filename like "currency.json"
         * @return
         */
        fun loadJSONFromAsset(context: Context, filename: String): String? {
            var json: String? = null
            try {
                val ips = context.assets.open(filename)
                val size = ips.available()
                val buffer = ByteArray(size)
                ips.read(buffer)
                ips.close()
                json = String(buffer, Charset.forName("UTF-8"))
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }

        fun checklaunage(activity :Activity) {
            val userselectedlanguage = "en"// AppSharedPreference.getStringPreference(this@Sign_In_Activity, Constants.Language, "")

            if (userselectedlanguage == "en") {
                Language.SetLanguage(activity, "en")
            } else {
                Language.SetLanguage(activity, "es")
            }

        }

        fun showSnake(context: Context,view : View ,  int :Int=0 , message :String = ""){
            val snackbar =if(int!= 0){ Snackbar.make(view, int, Snackbar.LENGTH_LONG)} else Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.show()
        }

        fun isConnectingToInternet(context: Context): Boolean {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null)
                    for (i in info.indices)
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
            }
            return false
        }




    }
}