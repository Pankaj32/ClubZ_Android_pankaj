package com.clubz.ui.user_activities.model;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : Activity created successfully
     * details : {"activityId":"36","name":"Kid Best Performer","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee_type":"Dynamic","fee":"10","min_users":"3","max_users":"7","user_role":"Club Manager","description":"Kids are the only reason the first one for a new message was sent using Tapatalk","terms_conditions":"Terms and condition and a good idea of 68","image":"http://clubz.co/dev/uploads/activity_image/64c37950b2c749ef3a93a2dbdf0c28a3.jpg","creator_name":"Dharmraj","leader_name":"","creator_profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","club_name":"Kidzee"}
     */

    private String status;
    private String message;
    private DetailsBean details;

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

    public DetailsBean getDetails() {
        return details;
    }

    public void setDetails(DetailsBean details) {
        this.details = details;
    }

    public static class DetailsBean {
        /**
         * activityId : 36
         * name : Kid Best Performer
         * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
         * latitude : 22.705138200000004
         * longitude : 75.9090618
         * fee_type : Dynamic
         * fee : 10
         * min_users : 3
         * max_users : 7
         * user_role : Club Manager
         * description : Kids are the only reason the first one for a new message was sent using Tapatalk
         * terms_conditions : Terms and condition and a good idea of 68
         * image : http://clubz.co/dev/uploads/activity_image/64c37950b2c749ef3a93a2dbdf0c28a3.jpg
         * creator_name : Dharmraj
         * leader_name :
         * creator_profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * club_name : Kidzee
         */

        private String activityId;
        private String name;
        private String location;
        private String latitude;
        private String longitude;
        private String fee_type;
        private String fee;
        private String min_users;
        private String max_users;
        private String user_role;
        private String description;
        private String terms_conditions;
        private String image;
        private String creator_name;
        private String leader_name;
        private String creator_profile_image;
        private String club_name;

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getFee_type() {
            return fee_type;
        }

        public void setFee_type(String fee_type) {
            this.fee_type = fee_type;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getMin_users() {
            return min_users;
        }

        public void setMin_users(String min_users) {
            this.min_users = min_users;
        }

        public String getMax_users() {
            return max_users;
        }

        public void setMax_users(String max_users) {
            this.max_users = max_users;
        }

        public String getUser_role() {
            return user_role;
        }

        public void setUser_role(String user_role) {
            this.user_role = user_role;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTerms_conditions() {
            return terms_conditions;
        }

        public void setTerms_conditions(String terms_conditions) {
            this.terms_conditions = terms_conditions;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCreator_name() {
            return creator_name;
        }

        public void setCreator_name(String creator_name) {
            this.creator_name = creator_name;
        }

        public String getLeader_name() {
            return leader_name;
        }

        public void setLeader_name(String leader_name) {
            this.leader_name = leader_name;
        }

        public String getCreator_profile_image() {
            return creator_profile_image;
        }

        public void setCreator_profile_image(String creator_profile_image) {
            this.creator_profile_image = creator_profile_image;
        }

        public String getClub_name() {
            return club_name;
        }

        public void setClub_name(String club_name) {
            this.club_name = club_name;
        }
    }
}
