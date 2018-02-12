package com.clubz.helper

/**
 * Created by mindiii on 2/10/18.
 */
class WebService {
    companion object {
        private val base_Url = "http://clubz.co/dev/"
        val Generate_Otp    = base_Url+"service/generateOtp"
        val Verify_Otp      = base_Url+"service/otpVerify"
        val Registraion     = base_Url+"service/registration"
    }
}