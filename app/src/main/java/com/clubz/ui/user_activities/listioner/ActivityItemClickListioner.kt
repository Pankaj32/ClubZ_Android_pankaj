package com.clubz.ui.user_activities.listioner

interface ActivityItemClickListioner {
    fun onLongPress(type: String, activityPosition: Int)
    fun onItemClick(type: String, activityPosition: Int)
    fun onJoinClick(type: String, activityPosition: Int)
    fun onConfirm(type: String, activityPosition: Int, eventPosition: Int)
    fun onEventDateClick(activityPosition: Int, eventPosition: Int)
}