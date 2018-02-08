package com.clubz.fragment.signup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.clubz.R
import com.clubz.Sign_up_Activity
import com.clubz.util.Util
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_One_2 : Fragment()  , View.OnClickListener {




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one_2, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for( view in arrayOf(confirm)) view.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.confirm -> if(verfiy())(activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_Two())
     }
    }

    fun verfiy() :Boolean{

        if(confirmation_code.text.isBlank()){
            Util.showSnake(context,view!! ,R.string.a_confirmation_code )
            return false;
        }
        return true;
    }
}