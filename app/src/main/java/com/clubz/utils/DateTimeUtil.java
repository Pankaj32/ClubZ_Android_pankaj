package com.clubz.utils;

import android.content.Context;

import com.clubz.R;
import com.clubz.data.local.pref.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    /*private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;*/

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(long time, Context ctx) {
        String language= SessionManager.getObj().getLanguage();
        /*if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }*/

        //long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
       /* if (time > now || time <= 0) {
            return null;
        }*/

        int dim = getTimeDistanceInMinutes(time);
        String timeAgo = null;
        if (dim == 0) {
            timeAgo = ctx.getString(R.string.just_now);
        } else if (dim == 1) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                return   dim +" "+ ctx.getResources().getString(R.string.date_util_unit_minute)+ " " + ctx.getString(R.string.ago);
            }else {
                return  "" + ctx.getString(R.string.ago) +" "+ dim +" "+ ctx.getResources().getString(R.string.date_util_unit_minute);
            }
        } else if (dim >= 2 && dim <= 59) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo = dim + " " + ctx.getString(R.string.date_util_unit_minutes) + " " + ctx.getString(R.string.ago);
            }else{
                timeAgo = "" + ctx.getString(R.string.ago) +" "+ dim + " " + ctx.getString(R.string.date_util_unit_minutes);
            }
        } else if (dim >= 61 && dim <= 119) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo ="1  "+ctx.getString(R.string.date_util_unit_hour)+" "+ctx.getString(R.string.ago);
            }else {
                timeAgo =ctx.getString(R.string.ago)+ " 1 "+ctx.getString(R.string.date_util_unit_hour);
            }
        } else if (dim >= 120 && dim <= 1439) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo = (Math.round(dim / 60)) + " "+ctx.getString(R.string.date_util_unit_hours)+" "+ctx.getString(R.string.ago);
            }else {
                timeAgo = ""+ctx.getString(R.string.ago)+" "+(Math.round(dim / 60)) +" "+ctx.getString(R.string.date_util_unit_hours);
            }
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = /*elapsedDays +*/ " "+ctx.getString(R.string.yesterday);
        } else if (dim >= 2520 && dim <= 43199) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo = (Math.round(dim / 1440)) + " "+ctx.getString(R.string.date_util_unit_days)+" "+ctx.getString(R.string.ago);
            }else {
                timeAgo = ""+ctx.getString(R.string.ago)+" "+(Math.round(dim / 1440)) + " "+ctx.getString(R.string.date_util_unit_days);;
            }
        } else if (dim >= 43200 && dim <= 86399) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo ="1  "+ctx.getString(R.string.date_util_unit_month)+" "+ctx.getString(R.string.ago);
            }else {
                timeAgo =ctx.getString(R.string.ago)+ " 1 "+ctx.getString(R.string.date_util_unit_month);
            }
        } else if (dim >= 86400 && dim <= 525599) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo =(Math.round(dim / 43200))+" "+ctx.getString(R.string.date_util_unit_month)+" "+ctx.getString(R.string.ago);
            }else {
                timeAgo =ctx.getString(R.string.ago)+" "+(Math.round(dim / 43200))+" "+ctx.getString(R.string.date_util_unit_month);
            }
        } else if (dim >= 525600 && dim <= 655199) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo ="1  "+ctx.getString(R.string.date_util_unit_year)+" "+ctx.getString(R.string.ago);
            }else {
                timeAgo =ctx.getString(R.string.ago)+ " 1 "+ctx.getString(R.string.date_util_unit_year);
            }
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = /*ctx.getResources().getString(R.string.date_util_prefix_about) + " " +*/ (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo;
    }


    public static String getTimeAgo(long startTime, long endTime, Context ctx, String msg) {
        String language= SessionManager.getObj().getLanguage();
        Date curDate = currentDate();
        long now = curDate.getTime();
       if ( now >endTime ) {
            return ctx.getString(R.string.event_start_title);
        }

        int dim = getTimeDistanceInMinutes(endTime);
        String timeAgo = null;
        if (dim == 0) {
            timeAgo = ctx.getString(R.string.just_now);
        } else if (dim == 1) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                return   dim +" "+ ctx.getResources().getString(R.string.date_util_unit_minute);
            }else {
                return   dim +" "+ ctx.getResources().getString(R.string.date_util_unit_minute);
            }
        } else if (dim >= 2 && dim <= 59) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo = dim + " " + ctx.getString(R.string.date_util_unit_minutes) ;
            }else{
                timeAgo =  dim + " " + ctx.getString(R.string.date_util_unit_minutes);
            }
        } else if (dim >= 61 && dim <= 119) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo ="1  "+ctx.getString(R.string.date_util_unit_hour);
            }else {
                timeAgo =" 1 "+ctx.getString(R.string.date_util_unit_hour);
            }
        } else if (dim >= 120 && dim <= 1439) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo = (Math.round(dim / 60)) + " "+ctx.getString(R.string.date_util_unit_hours);
            }else {
                timeAgo = (Math.round(dim / 60)) +" "+ctx.getString(R.string.date_util_unit_hours);
            }
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = /*elapsedDays +*/ " "+ctx.getString(R.string.yesterday);
        } else if (dim >= 2520 && dim <= 43199) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo = (Math.round(dim / 1440)) + " "+ctx.getString(R.string.date_util_unit_days);
            }else {
                timeAgo = (Math.round(dim / 1440)) + " "+ctx.getString(R.string.date_util_unit_days);;
            }
        } else if (dim >= 43200 && dim <= 86399) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo ="1 "+ctx.getString(R.string.date_util_unit_month);
            }else {
                timeAgo ="1 "+ctx.getString(R.string.date_util_unit_month);
            }
        } else if (dim >= 86400 && dim <= 525599) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo =(Math.round(dim / 43200))+" "+ctx.getString(R.string.date_util_unit_month);
            }else {
                timeAgo =(Math.round(dim / 43200))+" "+ctx.getString(R.string.date_util_unit_month);
            }
        } else if (dim >= 525600 && dim <= 655199) {
            if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                timeAgo ="1 "+ctx.getString(R.string.date_util_unit_year);
            }else {
                timeAgo ="1 "+ctx.getString(R.string.date_util_unit_year);
            }
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = /*ctx.getResources().getString(R.string.date_util_prefix_about) + " " +*/ (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + msg;
    }

    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static String getDayDifference(Context mContext, String departDateTime, String returnDateTime) {
        String language= SessionManager.getObj().getLanguage();
        boolean isgrater = false;
        String returnDay = "";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date startDate = simpleDateFormat.parse(departDateTime);
            Date endDate = simpleDateFormat.parse(returnDateTime);

            //milliseconds
            long different = endDate.getTime() - startDate.getTime();

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        returnDay = /*elapsedSeconds +*/ mContext.getString(R.string.just_now);
                    } else {
                        if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                            returnDay = elapsedMinutes + " " + mContext.getString(R.string.date_util_unit_minutes) + " " + mContext.getString(R.string.ago);
                        }else{
                            returnDay = "" + mContext.getString(R.string.ago) +" "+ elapsedMinutes + " " + mContext.getString(R.string.date_util_unit_minutes);
                        }
                    }
                } else if (elapsedHours == 1) {
                    if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                        returnDay = elapsedHours + "  "+mContext.getString(R.string.date_util_unit_hour)+" "+mContext.getString(R.string.ago);
                    }else {
                        returnDay = ""+mContext.getString(R.string.ago)+" "+elapsedHours + " "+mContext.getString(R.string.date_util_unit_hour);
                    }
                } else {
                    if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                        returnDay = elapsedHours + " "+mContext.getString(R.string.date_util_unit_hours)+" "+mContext.getString(R.string.ago);
                    }else {
                        returnDay = ""+mContext.getString(R.string.ago)+" "+elapsedHours +" "+mContext.getString(R.string.date_util_unit_hours);
                    }

                }
            } else if (elapsedDays == 1) {
                returnDay = /*elapsedDays +*/ " "+mContext.getString(R.string.yesterday);
            } else {
                if (language.equals(Util.Companion.getENGLISH_LOCALE())) {
                    returnDay = elapsedDays + " "+mContext.getString(R.string.date_util_unit_days)+" "+mContext.getString(R.string.ago);;
                }else {
                    returnDay = ""+mContext.getString(R.string.ago)+" "+elapsedDays + " "+mContext.getString(R.string.date_util_unit_days);;
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDay;
    }

    public static String ConvertMilliSecondsToFormattedDate(String dateTime) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return sfd.format(Long.parseLong(dateTime));
        } catch (Exception e) {

        }
        return "";
    }

    public static String ConvertMilliSecondsToDateAndTime(String dateTime) {
        try {
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

            String dateString = date.format(Long.parseLong(dateTime));
            String timeString = time.format(Long.parseLong(dateTime));
            String currtDate = getCurrentDate();
            if (dateString.equals(currtDate)) {
                return setTimeFormat(timeString);
            } else {
                SimpleDateFormat dat = new SimpleDateFormat("dd/MM/yyyy");
                String datString =dat.format(Long.parseLong(dateTime));
                return datString;
            }
        } catch (Exception e) {

        }
        return "";
    }

    public static String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month1 = month + 1;
        String monthSt =month1 < 10 ? "0" + month1 : "" + month1;
        String daySt =day < 10 ? "0" + day : "" + day;
        return (year + "-" + monthSt + "-" + daySt);
    }

    public static String getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int munite = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        return (hour + ":" + munite + ":" + sec);

    }

    public static String setTimeFormat(String time) {
        String formatedTime = "";
        String[] TimeList = time.split(":");
        String hourSt = TimeList[0];
        String minute = TimeList[1];
        int hour = Integer.parseInt(hourSt);
        String format;
        if (hour == 0) {
            hour += 12;
            format = "a.m.";
        } else if (hour == 12) {
            format = "p.m.";
        } else if (hour > 12) {
            hour -= 12;
            format = "p.m.";
        } else {
            format = "a.m.";
        }
        formatedTime = "" + hour + ":" + minute + " " + format;
        return formatedTime;
    }
}
