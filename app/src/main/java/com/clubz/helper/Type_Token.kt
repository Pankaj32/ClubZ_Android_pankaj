package com.clubz.helper

import com.clubz.data.model.*
import com.google.gson.reflect.TypeToken

/**
 * Created by mindiii on 2/7/18.
 */
class Type_Token {
    companion object {
        val country_list    = object : TypeToken<List<Country_Code>>() {}.type
        val image_list      = object : TypeToken<List<Int>>() {}.type
        val club_category   = object : TypeToken<List<Club_Category>>() {}.type
        val club_list       = object : TypeToken<List<Clubs>>() {}.type
        val potential_list  = object : TypeToken<List<Club_Potential_search>>() {}.type
        val club_member_list  = object : TypeToken<List<ClubMember>>() {}.type
    }
}