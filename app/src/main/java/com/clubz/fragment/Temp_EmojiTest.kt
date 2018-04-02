package com.clubz.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.R
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import kotlinx.android.synthetic.main.frag_test_emoji.*

/**
 * Created by mindiii on २/४/१८.
 */
class Temp_EmojiTest : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_test_emoji,null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        send__emoji.setOnClickListener(View.OnClickListener {
            sendEmoji()
        })
    }



    fun sendEmoji(){
        object : VolleyGetPost(activity , activity , "http://clubz.co/dev/service/send_emo" , false){
            override fun onVolleyResponse(response: String?) {
                Util.e("Response",response.toString())
                try {

                    Util.showToast(response.toString(),context);
                }catch (ex :Exception){

                }
            }

            override fun onVolleyError(error: VolleyError?) {

            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("title",name_tv.text.toString())
                params.put("name",message_tv.text.toString())
                Util.e("parms", params.toString())

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }

        }.execute()
    }
}