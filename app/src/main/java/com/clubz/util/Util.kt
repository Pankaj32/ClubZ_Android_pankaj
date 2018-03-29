package com.clubz.util

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
import android.widget.Toast
import com.clubz.R
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat

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

        fun showToast(s :String , context :Context){
            Toast.makeText(context,s,Toast.LENGTH_LONG).show()
        }

        fun showToast(i :Int , context :Context){
            Toast.makeText(context,i,Toast.LENGTH_LONG).show()
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

     public   val imageResources = intArrayOf(
             R.drawable.ad,
                     R.drawable.ae,
                     R.drawable.af,
                     R.drawable.ag,
                     R.drawable.ai,
                     R.drawable.al,
                     R.drawable.am,
                     R.drawable.ao,
                     R.drawable.ar,
                             R.drawable.as_,
             R.drawable.at,
        R.drawable.au,
        R.drawable.aw,
        R.drawable.az,
        R.drawable.ba,
        R.drawable.bb,
        R.drawable.bd,
        R.drawable.be,
        R.drawable.bf,
        R.drawable.bg,
        R.drawable.bh,
        R.drawable.bi,
        R.drawable.bj,
        R.drawable.bm,
        R.drawable.bn,
        R.drawable.bo,
        R.drawable.br,
        R.drawable.bs,
        R.drawable.bt,
        R.drawable.bw,
        R.drawable.by,
        R.drawable.bz,
        R.drawable.ca,
        R.drawable.cc,
        R.drawable.cd,
        R.drawable.cf,
        R.drawable.cg,
        R.drawable.ch,
        R.drawable.ci,
        R.drawable.ck,
        R.drawable.cl,
        R.drawable.cm,
        R.drawable.cn,
        R.drawable.co,
        R.drawable.cr,
        R.drawable.cu,
        R.drawable.cv,
        R.drawable.cw,
        R.drawable.cx,
        R.drawable.cy,
        R.drawable.cz,
        R.drawable.de,
        R.drawable.dj,
        R.drawable.dk,
        R.drawable.dm,
        R.drawable.do_,
        R.drawable.dz,
        R.drawable.ec,
        R.drawable.ee,
        R.drawable.eg,
        R.drawable.er,
        R.drawable.es,
        R.drawable.et,
        R.drawable.fi,
        R.drawable.fj,
        R.drawable.fk,
        R.drawable.fm,
        R.drawable.fo,
        R.drawable.fr,
        R.drawable.ga,
        R.drawable.gd,
        R.drawable.ge,
        R.drawable.gg,
        R.drawable.gh,
        R.drawable.gi,
        R.drawable.gl,
        R.drawable.gm,
        R.drawable.gn,
        R.drawable.gq,
        R.drawable.gr,
        R.drawable.gt,
        R.drawable.gu,
        R.drawable.gw,
        R.drawable.gy,
        R.drawable.hk,
        R.drawable.hn,
        R.drawable.hr,
        R.drawable.ht,
        R.drawable.hu,
        R.drawable.id,
        R.drawable.ie,
        R.drawable.il,
        R.drawable.im,
        R.drawable.in_,
        R.drawable.io,
        R.drawable.iq,
        R.drawable.ir,
        R.drawable.is_,
        R.drawable.it,
        R.drawable.je,
        R.drawable.jm,
        R.drawable.jo,
        R.drawable.jp,
        R.drawable.ke,
        R.drawable.kg,
        R.drawable.kh,
        R.drawable.ki,
        R.drawable.km,
        R.drawable.kn,
        R.drawable.kp,
        R.drawable.kr,
        R.drawable.ic_kosovo,
        R.drawable.kw,
        R.drawable.ky,
        R.drawable.kz,
        R.drawable.la,
        R.drawable.lb,
        R.drawable.lc,
        R.drawable.li,
        R.drawable.lk,
        R.drawable.lr,
        R.drawable.ls,
        R.drawable.lt,
        R.drawable.lu,
        R.drawable.lv,
        R.drawable.ly,
        R.drawable.ma,
        R.drawable.mc,
        R.drawable.md,
        R.drawable.me,
        R.drawable.mg,
        R.drawable.mh,
        R.drawable.mk,
        R.drawable.ml,
        R.drawable.mm,
        R.drawable.mn,
        R.drawable.mo,
        R.drawable.mp,
        R.drawable.mr,
        R.drawable.ms,
        R.drawable.mt,
        R.drawable.mu,
        R.drawable.mv,
        R.drawable.mw,
        R.drawable.mx,
        R.drawable.my,
        R.drawable.mz,
        R.drawable.na,
        R.drawable.ne,
        R.drawable.ng,
        R.drawable.ni,
        R.drawable.nl,
        R.drawable.no,
        R.drawable.np,
        R.drawable.nr,
        R.drawable.nu,
        R.drawable.nz,
        R.drawable.om,
        R.drawable.pa,
        R.drawable.pe,
        R.drawable.pf,
        R.drawable.pg,
        R.drawable.ph,
        R.drawable.pk,
        R.drawable.pl,
        R.drawable.pn,
        R.drawable.pr,
        R.drawable.ps,
        R.drawable.pt,
        R.drawable.pw,
        R.drawable.py,
        R.drawable.qa,
        R.drawable.ro,
        R.drawable.rs,
        R.drawable.ru,
        R.drawable.rw,
        R.drawable.sa,
        R.drawable.sb,
        R.drawable.sc,
        R.drawable.sd,
        R.drawable.se,
        R.drawable.sg,
        R.drawable.si,
        R.drawable.sk,
        R.drawable.sl,
        R.drawable.sm,
        R.drawable.sn,
        R.drawable.so,
        R.drawable.sr,
        R.drawable.ss,
        R.drawable.st,
        R.drawable.sv,
        R.drawable.sx,
        R.drawable.sy,
        R.drawable.sz,
        R.drawable.tc,
        R.drawable.td,
        R.drawable.tg,
        R.drawable.th,
        R.drawable.tj,
        R.drawable.tk,
        R.drawable.tl,
        R.drawable.tm,
        R.drawable.tn,
        R.drawable.to,
        R.drawable.tr,
        R.drawable.tt,
        R.drawable.tv,
        R.drawable.tw,
        R.drawable.tz,
        R.drawable.ua,
        R.drawable.ug,
        R.drawable.uk,
        R.drawable.us,
        R.drawable.uy,
        R.drawable.uz,
        R.drawable.va,
        R.drawable.vc,
        R.drawable.ve,
        R.drawable.vg,
        R.drawable.vi,
        R.drawable.vn,
        R.drawable.vu,
        R.drawable.ws,
        R.drawable.ye,
        R.drawable.za,
        R.drawable.zm,
        R.drawable.zw
        )




    }
}