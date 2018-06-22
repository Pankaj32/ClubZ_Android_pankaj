package com.clubz.ui.user_activities.model;

import java.util.List;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : found
     * data : [{"full_name":"Pankaj ","userId":"111","profile_image":"http://clubz.co/dev/uploads/profile/0ec65816942ad238a1eae6d0a92d340e.jpg","affiliates":[{"userAffiliateId":"144","affiliate_name":" Nupur"},{"userAffiliateId":"142","affiliate_name":"Anil"},{"userAffiliateId":"143","affiliate_name":" Sunil"},{"userAffiliateId":"145","affiliate_name":" Dharm"}]},{"full_name":"Chiranjib Ganguly","userId":"98","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","affiliates":[{"userAffiliateId":"113","affiliate_name":"Aish"}]}]
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
         * full_name : Pankaj
         * userId : 111
         * profile_image : http://clubz.co/dev/uploads/profile/0ec65816942ad238a1eae6d0a92d340e.jpg
         * affiliates : [{"userAffiliateId":"144","affiliate_name":" Nupur"},{"userAffiliateId":"142","affiliate_name":"Anil"},{"userAffiliateId":"143","affiliate_name":" Sunil"},{"userAffiliateId":"145","affiliate_name":" Dharm"}]
         */

        private String full_name;
        private String userId;
        private String profile_image;
        private List<AffiliatesBean> affiliates;

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public List<AffiliatesBean> getAffiliates() {
            return affiliates;
        }

        public void setAffiliates(List<AffiliatesBean> affiliates) {
            this.affiliates = affiliates;
        }

        public static class AffiliatesBean {
            /**
             * userAffiliateId : 144
             * affiliate_name :  Nupur
             */

            private String userAffiliateId;
            private String affiliate_name;

            public String getUserAffiliateId() {
                return userAffiliateId;
            }

            public void setUserAffiliateId(String userAffiliateId) {
                this.userAffiliateId = userAffiliateId;
            }

            public String getAffiliate_name() {
                return affiliate_name;
            }

            public void setAffiliate_name(String affiliate_name) {
                this.affiliate_name = affiliate_name;
            }
        }
    }
}
