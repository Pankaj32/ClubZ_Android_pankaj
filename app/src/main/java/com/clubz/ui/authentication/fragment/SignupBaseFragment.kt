package com.clubz.ui.authentication.fragment

import android.content.Context
import android.view.View
import android.widget.ScrollView
import com.clubz.ui.authentication.SignupActivity
import com.clubz.ui.core.BaseFragment
import com.clubz.utils.LinearLayoutThatDetectsSoftKeyboard
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*

abstract class SignupBaseFragment : BaseFragment(), LinearLayoutThatDetectsSoftKeyboard.Listener{

    lateinit var signupActivity : SignupActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is SignupActivity) signupActivity = context
    }

    override fun onSoftKeyboardShown(isShowing: Boolean) {
        if(isShowing){
            getScrollView().postDelayed({
                run {
                    val lastChild = getScrollView().getChildAt(getScrollView().childCount - 1) as View
                    val bottom = lastChild.bottom + getScrollView().paddingBottom
                    val sy = scrollView.scrollY
                    val sh = scrollView.height
                    val delta = bottom - (sy + sh)
                    getScrollView().smoothScrollBy(0, delta)
                }
            }, 200)
        }
    }

    abstract fun getScrollView(): ScrollView

    interface Listner{
        fun showDialog()
        fun hideDialog()
    }
}