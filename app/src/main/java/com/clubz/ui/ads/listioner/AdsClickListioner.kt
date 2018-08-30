package com.clubz.ui.ads.listioner

interface AdsClickListioner {
    fun onItemClick(position: Int)
    fun onFabClick(position: Int)
    fun onLongPress(position: Int)
}