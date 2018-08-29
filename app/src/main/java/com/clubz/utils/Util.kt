package com.clubz.utils

import android.app.Activity
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.View
import java.io.IOException
import java.nio.charset.Charset
import android.support.design.widget.Snackbar;
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import com.clubz.R
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

        fun showSnake(context: Context?,view : View ,  int :Int=0 , message :String = ""){
            val snackbar =if(int!= 0){ Snackbar.make(view, int, Snackbar.LENGTH_LONG)} else Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.show()
        }

        fun showToast(s :String?="" , context :Context?){
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
        }

        fun showToast(i :Int , context :Context){
            Toast.makeText(context,i,Toast.LENGTH_SHORT).show()
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

        fun convertDate(date: String): String {
            val format = SimpleDateFormat("yyyy-MM-dd")
            try {
                val date1 = format.parse(date)
                return SimpleDateFormat("dd MMM yyyy ").format(date1)
            } catch (e: ParseException) {
                Util.e("Error time ", e.toString())
                return date
            }

        }

        /**
         * March 3 2018
         */
        fun convertDate2(date: String): String {
            val format = SimpleDateFormat("yyyy-MM-dd")
            try {
                val date1 = format.parse(date)
                return SimpleDateFormat("MMMM dd , yyyy ").format(date1)
            } catch (e: ParseException) {
                Util.e("Error time ", e.toString())
                return date
            }

        }
        private val INITIAL_POSITION = 0.0f
        private val ROTATED_POSITION = 180f
        public fun setRotation(imgView: ImageView, expanded: Boolean) {
            val rotateAnimation: RotateAnimation
            if (expanded) { // rotate clockwise
                rotateAnimation = RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f)
            } else { // rotate counterclockwise
                rotateAnimation = RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f)
            }

            rotateAnimation.duration = 200
            rotateAnimation.fillAfter = true
            imgView.startAnimation(rotateAnimation)
        }


        fun getDistanceinKm( lat1 : Float,  lng1 :Float,  lat2 :Float, lng2 :Float) : Float{
            val earthRadius = 6371000.0 //meters
            Util.e("latlong","$lat1 : $lng2 : $lat2 : $lng2")
            val dLat = Math.toRadians((lat2 - lat1).toDouble())
            val dLng = Math.toRadians((lng2 - lng1).toDouble())
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(Math.toRadians(lat2.toDouble())) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            //NOT WORKING AS REQUIRED
            return (earthRadius * c).toFloat()

        }

        fun setTimeFormat(time: String): String {
            var formatedTime = ""
            val TimeList = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val hourSt = TimeList[0]
            val minute = TimeList[1]
            var hour = Integer.parseInt(hourSt)
            val format: String
            if (hour == 0) {
                hour += 12
                format = "A.M."
            } else if (hour == 12) {
                format = "P.M."
            } else if (hour > 12) {
                hour -= 12
                format = "P.M."
            } else {
                format = "A.M."
            }
           val  hr= if (hour < 10) "0$hour" else "$hour"
            formatedTime = "$hr:$minute $format"
            return formatedTime
        }
        fun getDistanceMile(LL: Array<Double>): Double {
            Util.e("LAT LONG ", LL[0].toString() + " " + LL[1] + " " + LL[2] + " " + LL[3])

            val startPoint = Location("locationA")
            startPoint.setLatitude(LL[0])
            startPoint.setLongitude(LL[1])

            val endPoint = Location("locationA")
            endPoint.setLatitude(LL[2])
            endPoint.setLongitude(LL[3])

            val distance = startPoint.distanceTo(endPoint) * 0.00062137
            return BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        }

        fun getEmojiByUnicode(unicode: Int): String {
            return String(Character.toChars(unicode))
        }

        fun toSentenceCase(inputString: String): String {
            var result = ""
            if (inputString.length == 0) {
                return result
            }
            val firstChar = inputString[0]
            val firstCharToUpperCase = Character.toUpperCase(firstChar)
            result = result + firstCharToUpperCase
            var terminalCharacterEncountered = false
            val terminalCharacters = charArrayOf('.', '?', '!')
            for (i in 1 until inputString.length) {
                val currentChar = inputString[i]
                if (terminalCharacterEncountered) {
                    if (currentChar == ' ') {
                        result = result + currentChar
                    } else {
                        val currentCharToUpperCase = Character.toUpperCase(currentChar)
                        result = result + currentCharToUpperCase
                        terminalCharacterEncountered = false
                    }
                } else {
                    val currentCharToLowerCase = Character.toLowerCase(currentChar)
                    result = result + currentCharToLowerCase
                }
                for (j in terminalCharacters.indices) {
                    if (currentChar == terminalCharacters[j]) {
                        terminalCharacterEncountered = true
                        break
                    }
                }
            }
            return result
        }
        private fun stringToDate(string: String) : Date {
            // yyyy-mm-dd hh:mm:ss
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val myDate = simpleDateFormat.parse(string)
            return myDate
        }

        fun getCurrentDate(): String {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val month1 = month + 1
            return ""+year + "-" + month1 + "-" + day
        }

        fun getCurrentTime(): String {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val munite = c.get(Calendar.MINUTE)
            val sec = c.get(Calendar.SECOND)
            return ""+hour + ":" + munite + ":" + sec
        }

        val imageResources = intArrayOf(
        R.drawable.af,
        R.drawable.al,
        R.drawable.dz,
        R.drawable.as_,
        R.drawable.ad,
        R.drawable.ao,
        R.drawable.ai,
        R.drawable.aq,
        R.drawable.ag,
        R.drawable.ar,
        R.drawable.am,
        R.drawable.aw,
        R.drawable.au,
        R.drawable.at,
        R.drawable.az,
        R.drawable.bs,
        R.drawable.bh,
        R.drawable.bd,
        R.drawable.bb,
        R.drawable.by,
        R.drawable.be,
        R.drawable.bz,
        R.drawable.bj,
        R.drawable.bm,
        R.drawable.bt,
        R.drawable.bo,
        R.drawable.ba,
        R.drawable.bw,
        R.drawable.br,
        R.drawable.io,
        R.drawable.vg,
        R.drawable.bn,
        R.drawable.bg,
        R.drawable.bf,
        R.drawable.bi,
        R.drawable.kh,
        R.drawable.cm,
        R.drawable.ca,
        R.drawable.cv,
        R.drawable.ky,
        R.drawable.cf,
        R.drawable.td,
        R.drawable.cl,
        R.drawable.cn,
        R.drawable.cx,
        R.drawable.cc,
        R.drawable.co,
        R.drawable.km,
        R.drawable.ck,
        R.drawable.cr,
        R.drawable.hr,
        R.drawable.cu,
        R.drawable.cw,
        R.drawable.cy,
        R.drawable.cz,
        R.drawable.cd,
        R.drawable.dk,
        R.drawable.dj,
        R.drawable.dm,
        R.drawable.do_,
        R.drawable.tl,
        R.drawable.ec,
        R.drawable.eg,
        R.drawable.sv,
        R.drawable.gq,
        R.drawable.er,
        R.drawable.ee,
        R.drawable.et,
        R.drawable.fk,
        R.drawable.fo,
        R.drawable.fj,
        R.drawable.fi,
        R.drawable.fr,
        R.drawable.pf,
        R.drawable.ga,
        R.drawable.gm,
        R.drawable.ge,
        R.drawable.de,
        R.drawable.gh,
        R.drawable.gi,
        R.drawable.gr,
        R.drawable.gl,
        R.drawable.gd,
        R.drawable.gu,
        R.drawable.gt,
        R.drawable.gg,
        R.drawable.gn,
        R.drawable.gw,
        R.drawable.gy,
        R.drawable.ht,
        R.drawable.hn,
        R.drawable.hk,
        R.drawable.hu,
        R.drawable.is_,
        R.drawable.in_,
        R.drawable.id,
        R.drawable.ir,
        R.drawable.iq,
        R.drawable.ie,
        R.drawable.im,
        R.drawable.il,
        R.drawable.it,
        R.drawable.ci,
        R.drawable.jm,
        R.drawable.jp,
        R.drawable.je,
        R.drawable.jo,
        R.drawable.kz,
        R.drawable.ke,
        R.drawable.ki,
        R.drawable.ic_kosovo_xk,
        R.drawable.kw,
        R.drawable.kg,
        R.drawable.la,
        R.drawable.lv,
        R.drawable.lb,
        R.drawable.ls,
        R.drawable.lr,
        R.drawable.ly,
        R.drawable.li,
        R.drawable.lt,
        R.drawable.lu,
        R.drawable.mo,
        R.drawable.mk,
        R.drawable.mg,
        R.drawable.mw,
        R.drawable.my,
        R.drawable.mv,
        R.drawable.ml,
        R.drawable.mt,
        R.drawable.mh,
        R.drawable.mr,
        R.drawable.mu,
        R.drawable.mx,
        R.drawable.fm,
        R.drawable.md,
        R.drawable.mc,
        R.drawable.mn,
        R.drawable.me,
        R.drawable.ms,
        R.drawable.ma,
        R.drawable.mz,
        R.drawable.mm,
        R.drawable.na,
        R.drawable.nr,
        R.drawable.np,
        R.drawable.nl,
        R.drawable.nz,
        R.drawable.ni,
        R.drawable.ne,
        R.drawable.ng,
        R.drawable.nu,
        R.drawable.kp,
        R.drawable.mp,
        R.drawable.no,
        R.drawable.om,
        R.drawable.pk,
        R.drawable.pw,
        R.drawable.ps,
        R.drawable.pa,
        R.drawable.pg,
        R.drawable.py,
        R.drawable.pe,
        R.drawable.ph,
        R.drawable.pn,
        R.drawable.pl,
        R.drawable.pt,
        R.drawable.pr,
        R.drawable.qa,
        R.drawable.cg,
        R.drawable.ro,
        R.drawable.ru,
        R.drawable.rw,
        R.drawable.kn,
        R.drawable.lc,
        R.drawable.vc,
        R.drawable.ws,
        R.drawable.sm,
        R.drawable.st,
        R.drawable.sa,
        R.drawable.sn,
        R.drawable.rs,
        R.drawable.sc,
        R.drawable.sl,
        R.drawable.sg,
        R.drawable.sx,
        R.drawable.sk,
        R.drawable.si,
        R.drawable.sb,
        R.drawable.so,
        R.drawable.za,
        R.drawable.kr,
        R.drawable.ss,
        R.drawable.es,
        R.drawable.lk,
        R.drawable.sd,
        R.drawable.sr,
        R.drawable.sz,
        R.drawable.se,
        R.drawable.ch,
        R.drawable.sy,
        R.drawable.tw,
        R.drawable.tj,
        R.drawable.tz,
        R.drawable.th,
        R.drawable.tg,
        R.drawable.tk,
        R.drawable.to,
        R.drawable.tt,
        R.drawable.tn,
        R.drawable.tr,
        R.drawable.tm,
        R.drawable.tc,
        R.drawable.tv,
        R.drawable.vi,
        R.drawable.ug,
        R.drawable.ua,
        R.drawable.ae,
        R.drawable.uk,
        R.drawable.us,
        R.drawable.uy,
        R.drawable.uz,
        R.drawable.vu,
        R.drawable.va,
        R.drawable.ve,
        R.drawable.vn,
        R.drawable.ye,
        R.drawable.zm,
        R.drawable.zw
        ) }
}