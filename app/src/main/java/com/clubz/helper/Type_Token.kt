package com.clubz.helper

import com.clubz.model.Country_Code
import com.google.gson.reflect.TypeToken

/**
 * Created by mindiii on 2/7/18.
 */
class Type_Token {
    companion object {
        val country_list = object : TypeToken<List<Country_Code>>() {}.type
    }
}