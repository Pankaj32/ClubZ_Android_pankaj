package com.clubz;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test {

    /**
     * status : success
     * message : Ad successfully created
     * adDetail : {"adId":"19","title":"ChatTest","fee":"20.56","is_renew":"1","description":"It's a chat test","user_id":"4","user_role":"Advertiser","image":"http://clubz.co/dev/uploads/ad_image/97adc8f68de1f18972d7258f444ed098.jpg","creator_name":"Chiranjib Ganguly","creator_profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","club_name":"Kidzee","clubId":"3","created":"2018-08-31 06:47:31","total_likes":"0"}
     */

    private String status;
    private String message;
    private AdDetailBean adDetail;

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

    public AdDetailBean getAdDetail() {
        return adDetail;
    }

    public void setAdDetail(AdDetailBean adDetail) {
        this.adDetail = adDetail;
    }

    public static class AdDetailBean {
        /**
         * adId : 19
         * title : ChatTest
         * fee : 20.56
         * is_renew : 1
         * description : It's a chat test
         * user_id : 4
         * user_role : Advertiser
         * image : http://clubz.co/dev/uploads/ad_image/97adc8f68de1f18972d7258f444ed098.jpg
         * creator_name : Chiranjib Ganguly
         * creator_profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * club_name : Kidzee
         * clubId : 3
         * created : 2018-08-31 06:47:31
         * total_likes : 0
         */

        private String adId;
        private String title;
        private String fee;
        private String is_renew;
        private String description;
        private String user_id;
        private String user_role;
        private String image;
        private String creator_name;
        private String creator_profile_image;
        private String club_name;
        private String clubId;
        private String created;
        private String total_likes;

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

        public String getClubId() {
            return clubId;
        }

        public void setClubId(String clubId) {
            this.clubId = clubId;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getTotal_likes() {
            return total_likes;
        }

        public void setTotal_likes(String total_likes) {
            this.total_likes = total_likes;
        }
    }
}
