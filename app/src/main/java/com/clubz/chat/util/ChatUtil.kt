package com.clubz.chat.util

import java.text.SimpleDateFormat

/**
 * Created by chiranjib on 25/6/18.
 */
class ChatUtil {
    companion object {
        val ARG_CHAT_ROOMS = "chat_rooms"
        val ARG_CHAT_HISTORY = "chat_history"
        val ARG_CHAT_BLOCK = "BlockUsers"
        val ARG_USERS = "users"
        val ARG_CLUB = "club"
        val ARG_CLUB_MEMBER = "clubMember"
        val ARG_NEWS_FEED = "newsFeed"
        val ARG_ACTIVITIES = "activities"
        val ARG_ADS= "adds"

        fun ConvertMilliSecondsToFormattedDateToTime(dateTime: String): String {
            try {
                val sfd = SimpleDateFormat("dd/MM/YYYY hh:mm a")

                return sfd.format(java.lang.Long.parseLong(dateTime))
            } catch (e: Exception) {

            }

            return ""
        }
    }
}