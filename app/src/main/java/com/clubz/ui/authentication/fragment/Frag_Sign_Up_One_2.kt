package com.clubz.ui.authentication.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.ui.authentication.SignupActivity
import com.clubz.data.remote.WebService
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.Toast
import com.clubz.ClubZ
import com.clubz.ui.main.HomeActivity
import com.clubz.helper.sms_reciver.OnSmsCatchListener
import com.clubz.helper.sms_reciver.SmsVerifyCatcher
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.User
import com.clubz.utils.Constants
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_One_2 : SignupBaseFragment(), View.OnClickListener, OnSmsCatchListener<String> {

    lateinit var _otp: String
    lateinit var _contact: String
    lateinit var _code: String
    var _isnewuser: Boolean = false
    private lateinit var smsverify: SmsVerifyCatcher
    //ClubZ- Your PIN for registration is: 4567

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smsverify = SmsVerifyCatcher(activity as SignupActivity, this, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one_2, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirm.setOnClickListener(this)
        resend.setOnClickListener(this)
        Util.showToast(_otp + " : This message is Temporary ", context)

        // confirmation_code.setText(_otp)
        confirmation_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length == 4) {
                    confirm.callOnClick()
                }
            }
        })

        confirmation_code.setOnEditorActionListener { p0, p1, p2 ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                confirm.callOnClick()
                true
            } else true
        }

        mainLayout.setListener(this)
    }

    override fun getScrollView(): ScrollView {
        return scrollView
    }

    override fun onSmsCatch(message: String?) {
        if (!message.isNullOrBlank() && message!!.contains(resources.getString(R.string.app_name))) {
            val otpCode = parseCode(message)
            if (otpCode.isNotEmpty()) {
                confirmation_code.setText(otpCode)
                confirm.callOnClick()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsverify.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        smsverify.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsverify.onStop()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.confirm -> {
                listner.hideKeyBoard()
                if (verfiy())
                    if (_isnewuser)
                        listner.replaceFragment(FragSignUp2().setData(_contact, _code))
                    else {
                        verify_otp()
                    }
            }

            R.id.resend -> {
                reSendOtp()
            }
        }
    }

    fun setData(otp: String, contact: String, code: String, isnewuser: String): Frag_Sign_Up_One_2 {
        _otp = otp
        _contact = contact
        _code = code
        _isnewuser = isnewuser == "1"
        return this
    }

    fun verfiy(): Boolean {

        if (confirmation_code.text.isBlank()) {
            Util.showSnake(context, view!!, R.string.a_confirmation_code)
            return false
        }
        if (!confirmation_code.text.toString().trim().equals(_otp)) {
            Util.showSnake(context, view!!, R.string.a_confirmation_code2)
            return false
        }
        return true
    }

    private fun verify_otp() {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, activity, WebService.Login, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                        ClubZ.currentUser = SessionManager.getObj().user
                        startActivity(Intent(activity, HomeActivity::class.java))
                        signupActivity.finish()
                    } else {
                        listner.showToast(obj.getString("message"))
                    }

                } catch (ex: Exception) {
                    if (context != null) listner.showToast(getString(R.string.swr))
                }

                if (context != null) dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["country_code"] = "+$_code"
                params["contact_no"] = _contact
                params.put("device_token" , FirebaseInstanceId.getInstance().getToken()!!)
                params.put("device_type" , Constants.DEVICE_TYPE)
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                return params

            }
        }.execute()
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private fun parseCode(message: String): String {
        val p = Pattern.compile("\\b\\d{4}\\b")
        val m = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        return code
    }


    private fun reSendOtp() {
        val activity = activity as SignupActivity
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, context, WebService.Generate_Otp, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        _otp = obj.getString("otp")
                        Toast.makeText(context, _otp, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (ex: Exception) {
                    Toast.makeText(context, R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["country_code"] = "+$_code"
                params["contact_no"] = _contact
                Util.e("params", params.toString())
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }
}