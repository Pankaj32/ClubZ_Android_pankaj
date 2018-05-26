package com.clubz.ui.club.`interface`

import com.clubz.data.model.Clubs

interface MyClubInteraction {
    fun onJoinClub(club : Clubs)
    fun onLeaveClub(club : Clubs)
}