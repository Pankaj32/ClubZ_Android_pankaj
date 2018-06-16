package com.clubz.ui.user_activities.model;

import java.util.List;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : ok
     * data : {"full_name":"Chiranjib Ganguly","userId":"98","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","isConfirmed":"0","affiliates":[{"userAffiliateId":"114","affiliate_name":" Dharam","isConfirmed":"0"}]}
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
         * full_name : Chiranjib Ganguly
         * userId : 98
         * profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * isConfirmed : 0
         * affiliates : [{"userAffiliateId":"114","affiliate_name":" Dharam","isConfirmed":"0"}]
         */

        private String full_name;
        private String userId;
        private String profile_image;
        private String isConfirmed;
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

        public String getIsConfirmed() {
            return isConfirmed;
        }

        public void setIsConfirmed(String isConfirmed) {
            this.isConfirmed = isConfirmed;
        }

        public List<AffiliatesBean> getAffiliates() {
            return affiliates;
        }

        public void setAffiliates(List<AffiliatesBean> affiliates) {
            this.affiliates = affiliates;
        }

        public static class AffiliatesBean {
            /**
             * userAffiliateId : 114
             * affiliate_name :  Dharam
             * isConfirmed : 0
             */

            private String userAffiliateId;
            private String affiliate_name;
            private String isConfirmed;

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

            public String getIsConfirmed() {
                return isConfirmed;
            }

            public void setIsConfirmed(String isConfirmed) {
                this.isConfirmed = isConfirmed;
            }
        }
    }
}
