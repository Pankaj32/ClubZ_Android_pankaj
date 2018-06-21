package com.clubz.ui.user_activities.model;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : ok
     * data : {"activityId":"20","name":"Diwali Festival","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee_type":"Voluntary","fee":"25","min_users":"2","max_users":"10","user_role":"admin","description":"Diwali festival and we will get you on Friday","terms_conditions":"Terms and conditions and a good weekend and then delete it and you it","image":"http://clubz.co/dev/uploads/activity_image/523bd8f1619144b713495338aa882fd7.jpg","is_like":"0","leader_name":"","creator_name":"Chiranjib Ganguly","creator_profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","next_event":{"activityEventId":"15","event_title":"edfuyyy","event_date":"2018-06-27","event_time":"00:51:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary"}}
     */

    private String status;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * activityId : 20
         * name : Diwali Festival
         * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
         * latitude : 22.705138200000004
         * longitude : 75.9090618
         * fee_type : Voluntary
         * fee : 25
         * min_users : 2
         * max_users : 10
         * user_role : admin
         * description : Diwali festival and we will get you on Friday
         * terms_conditions : Terms and conditions and a good weekend and then delete it and you it
         * image : http://clubz.co/dev/uploads/activity_image/523bd8f1619144b713495338aa882fd7.jpg
         * is_like : 0
         * leader_name :
         * creator_name : Chiranjib Ganguly
         * creator_profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * next_event : {"activityEventId":"15","event_title":"edfuyyy","event_date":"2018-06-27","event_time":"00:51:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary"}
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
        private String is_like;
        private String leader_name;
        private String creator_name;
        private String creator_profile_image;
        private NextEventBean next_event;

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

        public String getIs_like() {
            return is_like;
        }

        public void setIs_like(String is_like) {
            this.is_like = is_like;
        }

        public String getLeader_name() {
            return leader_name;
        }

        public void setLeader_name(String leader_name) {
            this.leader_name = leader_name;
        }

        public String getCreator_name() {
            return creator_name;
        }

        public void setCreator_name(String creator_name) {
            this.creator_name = creator_name;
        }

        public String getCreator_profile_image() {
            return creator_profile_image;
        }

        public void setCreator_profile_image(String creator_profile_image) {
            this.creator_profile_image = creator_profile_image;
        }

        public NextEventBean getNext_event() {
            return next_event;
        }

        public void setNext_event(NextEventBean next_event) {
            this.next_event = next_event;
        }

        public static class NextEventBean {
            /**
             * activityEventId : 15
             * event_title : edfuyyy
             * event_date : 2018-06-27
             * event_time : 00:51:00
             * description :
             * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
             * latitude : 22.705138200000004
             * longitude : 75.9090618
             * fee : 25
             * fee_type : Voluntary
             */

            private String activityEventId;
            private String event_title;
            private String event_date;
            private String event_time;
            private String description;
            private String location;
            private String latitude;
            private String longitude;
            private String fee;
            private String fee_type;

            public String getActivityEventId() {
                return activityEventId;
            }

            public void setActivityEventId(String activityEventId) {
                this.activityEventId = activityEventId;
            }

            public String getEvent_title() {
                return event_title;
            }

            public void setEvent_title(String event_title) {
                this.event_title = event_title;
            }

            public String getEvent_date() {
                return event_date;
            }

            public void setEvent_date(String event_date) {
                this.event_date = event_date;
            }

            public String getEvent_time() {
                return event_time;
            }

            public void setEvent_time(String event_time) {
                this.event_time = event_time;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
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

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }

            public String getFee_type() {
                return fee_type;
            }

            public void setFee_type(String fee_type) {
                this.fee_type = fee_type;
            }
        }
    }
}
