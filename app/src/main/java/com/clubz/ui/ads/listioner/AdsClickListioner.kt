package com.clubz.ui.ads.listioner

import com.clubz.data.model.UserInfo

interface AdsClickListioner {
    fun onItemClick(position: Int)
    fun onFabClick(position: Int)
    fun onUserClick(user: UserInfo)
    fun onLongPress(position: Int)
}