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
import android.widget.Toast
import com.clubz.R

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

     public   val imageResources = intArrayOf(
                        R.drawable.af,
                        R.drawable.al,
                        R.drawable.dz,
                        R.drawable.ad,
                        R.drawable.ao,
                        R.drawable.aq,
                        R.drawable.ar,
                        R.drawable.am,
                        R.drawable.aw,
                        R.drawable.au,
                        R.drawable.at,
                        R.drawable.az,
                        R.drawable.bh,
                        R.drawable.bd,
                        R.drawable.by,
                        R.drawable.be,
                        R.drawable.bz,
                        R.drawable.bj,
                        R.drawable.bt,
                        R.drawable.bo,
                        R.drawable.ba,
                        R.drawable.bw,
                        R.drawable.br,
                        R.drawable.bn,
                        R.drawable.bg,
                        R.drawable.bf,
                        R.drawable.mm,
                        R.drawable.bi,
                        R.drawable.kh,
                        R.drawable.cm,
                        R.drawable.ca,
                        R.drawable.cv,
                        R.drawable.cf,
                        R.drawable.td,
                        R.drawable.cl,
                        R.drawable.cn,
                        R.drawable.cx,
                        R.drawable.cc,
                        R.drawable.co,
                        R.drawable.km,
                        R.drawable.cg,
                        R.drawable.cd,
                        R.drawable.ck,
                        R.drawable.cr,
                        R.drawable.hr,
                        R.drawable.cu,
                        R.drawable.cy,
                        R.drawable.cz,
                        R.drawable.dk,
                        R.drawable.dj,
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
                        R.drawable.gt,
                        R.drawable.gn,
                        R.drawable.gw,
                        R.drawable.gy,
                        R.drawable.ht,
                        R.drawable.hn,
                        R.drawable.hk,
                        R.drawable.hu,
                        R.drawable.in_,
                        R.drawable.id,
                        R.drawable.ir,
                        R.drawable.iq,
                        R.drawable.ie,
                        R.drawable.im,
                        R.drawable.il,
                        R.drawable.it,
                        R.drawable.ci,
                        R.drawable.jp,
                        R.drawable.jo,
                        R.drawable.kz,
                        R.drawable.ke,
                        R.drawable.ki,
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
                        R.drawable.yt,
                        R.drawable.mx,
                        R.drawable.fm,
                        R.drawable.md,
                        R.drawable.mc,
                        R.drawable.mn,
                        R.drawable.me,
                        R.drawable.ma,
                        R.drawable.mz,
                        R.drawable.na,
                        R.drawable.nr,
                        R.drawable.np,
                        R.drawable.nl,
                        R.drawable.nc,
                        R.drawable.nz,
                        R.drawable.ni,
                        R.drawable.ne,
                        R.drawable.ng,
                        R.drawable.nu,
                        R.drawable.kp,
                        R.drawable.no,
                        R.drawable.om,
                        R.drawable.pk,
                        R.drawable.pw,
                        R.drawable.pa,
                        R.drawable.pg,
                        R.drawable.py,
                        R.drawable.pe,
                        R.drawable.ph,
                        R.drawable.pn,
                        R.drawable.pl,
                        R.drawable.pt,
                        R.drawable.qa,
                        R.drawable.ro,
                        R.drawable.ru,
                        R.drawable.rw,
                        R.drawable.bl,
                        R.drawable.ws,
                        R.drawable.sm,
                        R.drawable.st,
                        R.drawable.sa,
                        R.drawable.sn,
                        R.drawable.rs,
                        R.drawable.sc,
                        R.drawable.sl,
                        R.drawable.sg,
                        R.drawable.sk,
                        R.drawable.si,
                        R.drawable.sb,
                        R.drawable.so,
                        R.drawable.za,
                        R.drawable.kr,
                        R.drawable.es,
                        R.drawable.lk,
                        R.drawable.sh,
                        R.drawable.pm,
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
                        R.drawable.tn,
                        R.drawable.tr,
                        R.drawable.tm,
                        R.drawable.tv,
                        R.drawable.ae,
                        R.drawable.ug,
                        R.drawable.gb,
                        R.drawable.ua,
                        R.drawable.uy,
                        R.drawable.us,
                        R.drawable.uz,
                        R.drawable.vu,
                        R.drawable.va,
                        R.drawable.ve,
                        R.drawable.vn,
                        R.drawable.wf,
                        R.drawable.ye,
                        R.drawable.zm,
                        R.drawable.zw
        )




    }
}