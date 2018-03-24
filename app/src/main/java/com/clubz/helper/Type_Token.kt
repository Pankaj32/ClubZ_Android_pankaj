package com.clubz.helper

import com.clubz.model.Club_Category
import com.clubz.model.Clubs
import com.clubz.model.Country_Code
import com.google.gson.reflect.TypeToken

/**
 * Created by mindiii on 2/7/18.
 */
class Type_Token {
    companion object {
        val country_list = object : TypeToken<List<Country_Code>>() {}.type
        val image_list = object : TypeToken<List<Int>>() {}.type
        val club_category = object : TypeToken<List<Club_Category>>() {}.type
        val club_list = object : TypeToken<List<Clubs>>() {}.type
    }
}