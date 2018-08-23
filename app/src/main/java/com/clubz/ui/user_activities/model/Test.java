package com.clubz.ui.user_activities.model;

import java.util.List;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {


    /**
     * status : success
     * message : ok
     * data : [{"adId":"2","title":"Testing ads","fee":"100","is_renew":"1","description":"This is testing ads","club_id":"4","user_id":"5","user_role":"Manager","crd":"2018-08-19 09:36:29","image":"","club_name":"Friend Zone","full_name":"Chiru Ganguly","isFav":"0","currentDatetime":"2018-08-22 10:08:54","is_my_ads":"1","is_New":"0"},{"adId":"1","title":"My First Ad","fee":"25.8","is_renew":"1","description":"Hello friends ........","club_id":"4","user_id":"5","user_role":"Manager","crd":"2018-08-21 08:44:54","image":"http://clubz.co/dev/uploads/ad_image/6e3f49e0e8d68a491759184c6206cbeb.jpg","club_name":"Friend Zone","full_name":"Chiru Ganguly","isFav":"0","currentDatetime":"2018-08-22 10:08:54","is_my_ads":"1","is_New":"1"}]
     */

    private String status;
    private String message;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * adId : 2
         * title : Testing ads
         * fee : 100
         * is_renew : 1
         * description : This is testing ads
         * club_id : 4
         * user_id : 5
         * user_role : Manager
         * crd : 2018-08-19 09:36:29
         * image :
         * club_name : Friend Zone
         * full_name : Chiru Ganguly
         * isFav : 0
         * currentDatetime : 2018-08-22 10:08:54
         * is_my_ads : 1
         * is_New : 0
         */

        private String adId;
        private String title;
        private String fee;
        private String is_renew;
        private String description;
        private String club_id;
        private String user_id;
        private String user_role;
        private String crd;
        private String image;
        private String club_name;
        private String full_name;
        private String isFav;
        private String currentDatetime;
        private String is_my_ads;
        private String is_New;

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getIs_renew() {
            return is_renew;
        }

        public void setIs_renew(String is_renew) {
            this.is_renew = is_renew;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getClub_id() {
            return club_id;
        }

        public void setClub_id(String club_id) {
            this.club_id = club_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_role() {
            return user_role;
        }

        public void setUser_role(String user_role) {
            this.user_role = user_role;
        }

        public String getCrd() {
            return crd;
        }

        public void setCrd(String crd) {
            this.crd = crd;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getClub_name() {
            return club_name;
        }

        public void setClub_name(String club_name) {
            this.club_name = club_name;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getIsFav() {
            return isFav;
        }

        public void setIsFav(String isFav) {
            this.isFav = isFav;
        }

        public String getCurrentDatetime() {
            return currentDatetime;
        }

        public void setCurrentDatetime(String currentDatetime) {
            this.currentDatetime = currentDatetime;
        }

        public String getIs_my_ads() {
            return is_my_ads;
        }

        public void setIs_my_ads(String is_my_ads) {
            this.is_my_ads = is_my_ads;
        }

        public String getIs_New() {
            return is_New;
        }

        public void setIs_New(String is_New) {
            this.is_New = is_New;
        }
    }
}
