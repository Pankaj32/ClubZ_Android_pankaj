package com.clubz.ui.user_activities.model;

import java.util.List;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : ok
     * data : {"today":[{"image":"http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg","activityName":"Diwaly Festivals ","activityId":"9","club_name":"Mindiii Sport","is_like":"0","events":[{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]}],"tomorrow":[{"image":"http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg","activityName":"Diwaly Festivals ","activityId":"9","club_name":"Mindiii Sport","is_like":"0","events":[{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]}],"soon":[{"image":"http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg","activityName":"Diwaly Festivals ","activityId":"9","club_name":"Mindiii Sport","is_like":"0","events":[{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]}]}
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
        private List<TodayBean> today;
        private List<TomorrowBean> tomorrow;
        private List<SoonBean> soon;

        public List<TodayBean> getToday() {
            return today;
        }

        public void setToday(List<TodayBean> today) {
            this.today = today;
        }

        public List<TomorrowBean> getTomorrow() {
            return tomorrow;
        }

        public void setTomorrow(List<TomorrowBean> tomorrow) {
            this.tomorrow = tomorrow;
        }

        public List<SoonBean> getSoon() {
            return soon;
        }

        public void setSoon(List<SoonBean> soon) {
            this.soon = soon;
        }

        public static class TodayBean {
            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            private String image;
            private String activityName;
            private String activityId;
            private String club_name;
            private String is_like;
            private List<EventsBean> events;

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

            public String getIs_like() {
                return is_like;
            }

            public void setIs_like(String is_like) {
                this.is_like = is_like;
            }

            public List<EventsBean> getEvents() {
                return events;
            }

            public void setEvents(List<EventsBean> events) {
                this.events = events;
            }

            public static class EventsBean {
                /**
                 * activityEventId : 4
                 * event_title : diwali celebration
                 * event_date : 2018-05-31
                 * event_time : 17:00:00
                 * description :
                 * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
                 * latitude : 22.705138200000004
                 * longitude : 75.9090618
                 * fee : 25
                 * fee_type : Voluntary
                 * max_users : 5
                 * is_confirm : 0
                 * total_users : 1
                 * joined_users : 1
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
                private String max_users;
                private String is_confirm;
                private String total_users;
                private String joined_users;

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

                public String getMax_users() {
                    return max_users;
                }

                public void setMax_users(String max_users) {
                    this.max_users = max_users;
                }

                public String getIs_confirm() {
                    return is_confirm;
                }

                public void setIs_confirm(String is_confirm) {
                    this.is_confirm = is_confirm;
                }

                public String getTotal_users() {
                    return total_users;
                }

                public void setTotal_users(String total_users) {
                    this.total_users = total_users;
                }

                public String getJoined_users() {
                    return joined_users;
                }

                public void setJoined_users(String joined_users) {
                    this.joined_users = joined_users;
                }
            }
        }

        public static class TomorrowBean {
            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            private String image;
            private String activityName;
            private String activityId;
            private String club_name;
            private String is_like;
            private List<EventsBeanX> events;

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

            public String getIs_like() {
                return is_like;
            }

            public void setIs_like(String is_like) {
                this.is_like = is_like;
            }

            public List<EventsBeanX> getEvents() {
                return events;
            }

            public void setEvents(List<EventsBeanX> events) {
                this.events = events;
            }

            public static class EventsBeanX {
                /**
                 * activityEventId : 4
                 * event_title : diwali celebration
                 * event_date : 2018-05-31
                 * event_time : 17:00:00
                 * description :
                 * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
                 * latitude : 22.705138200000004
                 * longitude : 75.9090618
                 * fee : 25
                 * fee_type : Voluntary
                 * max_users : 5
                 * is_confirm : 0
                 * total_users : 1
                 * joined_users : 1
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
                private String max_users;
                private String is_confirm;
                private String total_users;
                private String joined_users;

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

                public String getMax_users() {
                    return max_users;
                }

                public void setMax_users(String max_users) {
                    this.max_users = max_users;
                }

                public String getIs_confirm() {
                    return is_confirm;
                }

                public void setIs_confirm(String is_confirm) {
                    this.is_confirm = is_confirm;
                }

                public String getTotal_users() {
                    return total_users;
                }

                public void setTotal_users(String total_users) {
                    this.total_users = total_users;
                }

                public String getJoined_users() {
                    return joined_users;
                }

                public void setJoined_users(String joined_users) {
                    this.joined_users = joined_users;
                }
            }
        }

        public static class SoonBean {
            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            private String image;
            private String activityName;
            private String activityId;
            private String club_name;
            private String is_like;
            private List<EventsBeanXX> events;

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

            public String getIs_like() {
                return is_like;
            }

            public void setIs_like(String is_like) {
                this.is_like = is_like;
            }

            public List<EventsBeanXX> getEvents() {
                return events;
            }

            public void setEvents(List<EventsBeanXX> events) {
                this.events = events;
            }

            public static class EventsBeanXX {
                /**
                 * activityEventId : 4
                 * event_title : diwali celebration
                 * event_date : 2018-05-31
                 * event_time : 17:00:00
                 * description :
                 * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
                 * latitude : 22.705138200000004
                 * longitude : 75.9090618
                 * fee : 25
                 * fee_type : Voluntary
                 * max_users : 5
                 * is_confirm : 0
                 * total_users : 1
                 * joined_users : 1
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
                private String max_users;
                private String is_confirm;
                private String total_users;
                private String joined_users;

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

                public String getMax_users() {
                    return max_users;
                }

                public void setMax_users(String max_users) {
                    this.max_users = max_users;
                }

                public String getIs_confirm() {
                    return is_confirm;
                }

                public void setIs_confirm(String is_confirm) {
                    this.is_confirm = is_confirm;
                }

                public String getTotal_users() {
                    return total_users;
                }

                public void setTotal_users(String total_users) {
                    this.total_users = total_users;
                }

                public String getJoined_users() {
                    return joined_users;
                }

                public void setJoined_users(String joined_users) {
                    this.joined_users = joined_users;
                }
            }
        }
    }
}
