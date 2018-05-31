package com.clubz.data.model

/**
 * Created by mindiii on ३१/३/१८.
 */
class Club_Potential_search{
    var club_name = ""
    var club_type = ""
    var distance  = ""

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Club_Potential_search)
            return false
        return if (other === this) true else this.club_name == other.club_name
    }

}