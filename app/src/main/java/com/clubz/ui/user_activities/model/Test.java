package com.clubz.ui.user_activities.model;

import java.util.List;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : ok
     * data : [{"image":"http://clubz.co/dev/uploads/activity_image/6658df64236ffec11b469522dd34233c.jpg","activityName":"Football Match","activityId":"12","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/1056b7090cdc255f2af7ea21c203a472.jpg","activityName":"My activity","activityId":"11","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/ab5330781463c058c48118eb976344d1.jpg","activityName":"Holy Festival","activityId":"10","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/a2d8260f55348603ef453101fe1c51d6.jpg","activityName":"New activity pankaj","activityId":"8","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/8fcfdf8b7831c2e53689d21f54754b3e.jpg","activityName":"Annual Function","activityId":"7","club_name":"Lions Club","events":[{"activityEventId":"2","event_title":"my event","event_date":"2018-05-30","event_time":"07:10:00","description":"test","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"26","fee_type":"Fixed","max_users":"6","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"1","event_title":"event fav","event_date":"2018-05-30","event_time":"12:08:00","description":"test","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"26","fee_type":"Fixed","max_users":"6","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"3","event_title":"new event of activity","event_date":"2018-05-31","event_time":"13:00:00","description":"test","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"26","fee_type":"Fixed","max_users":"6","is_confirm":"0","total_users":"1","joined_users":"0"}]}]
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
         * image : http://clubz.co/dev/uploads/activity_image/6658df64236ffec11b469522dd34233c.jpg
         * activityName : Football Match
         * activityId : 12
         * club_name : Lions Club
         * events : []
         */

        private String image;
        private String activityName;
        private String activityId;
        private String club_name;
        private List<?> events;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public String getClub_name() {
            return club_name;
        }

        public void setClub_name(String club_name) {
            this.club_name = club_name;
        }

        public List<?> getEvents() {
            return events;
        }

        public void setEvents(List<?> events) {
            this.events = events;
        }
    }
}
