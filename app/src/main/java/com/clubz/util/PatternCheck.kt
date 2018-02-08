package com.clubz.util

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by mindiii on 15/11/16.
 */
class PatternCheck private constructor() {
    internal var emailpatt = "^[a-zA-Z0-9_.]+@[a-zA-Z]+\\.[a-zA-Z]+$"
    internal var passpatt = "[0-9a-zA-Z@#$%^&*]{6,15}"
    internal var userpatt = "^[a-zA-Z0-9][a-zA-Z0-9 ]{2,20}"
    internal var name = "^[a-zA-Z][a-zA-Z ]{2,100}"
    internal var contact = "[0-9]{7,20}"
    internal var price = "[0-9]{1,20}"


    fun check(type: Byte?, tocheck: String): Boolean {
        var result = true
        when (type) {
            _email -> result = ismatch(tocheck, emailpatt)
            _pass -> result = ismatch(tocheck, passpatt)
            _user -> result = ismatch(tocheck, userpatt)
            _contact -> result = ismatch(tocheck, contact)
            _name -> result = ismatch(tocheck, name)
            _price -> result = ismatch(tocheck, price)
        }

        return result
    }

    internal fun ismatch(s: String, pa: String): Boolean {
        val p = Pattern.compile(pa)
        val m = p.matcher(s)
        return m.matches()
    }

    companion object {
        val instance = PatternCheck()
        val _name: Byte = 1
        val _email: Byte = 2
        val _contact: Byte = 3
        val _pass: Byte = 4
        val _user: Byte = 5
        val _price: Byte = 6
    }

}
