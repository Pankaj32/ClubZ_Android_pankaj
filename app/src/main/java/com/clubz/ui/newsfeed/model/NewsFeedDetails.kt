package com.clubz.ui.newsfeed.model

/**
 * Created by chiranjib on 4/7/18.
 */
class NewsFeedDetails {
    /**
     * status : success
     * message : Feed added successfully
     * feedDetail : {"news_feed_attachment":"http://clubz.co/dev/uploads/news_feed_attachment/c322b983122aebdcf5967710d1adda57.jpg","news_feed_title":"Kids competition","newsFeedId":"30","news_feed_description":"Mini to you soon so I am looking at least","is_comment_allow":"1","clubId":"103","club_name":"Kidzee","crd":"2018-07-04 07:14:19","likes":"0","comments":"0"}
     */

    private var status: String? = null
    private var message: String? = null
    private var feedDetail: FeedDetailBean? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getFeedDetail(): FeedDetailBean? {
        return feedDetail
    }

    fun setFeedDetail(feedDetail: FeedDetailBean) {
        this.feedDetail = feedDetail
    }

    class FeedDetailBean {
        /**
         * news_feed_attachment : http://clubz.co/dev/uploads/news_feed_attachment/c322b983122aebdcf5967710d1adda57.jpg
         * news_feed_title : Kids competition
         * newsFeedId : 30
         * news_feed_description : Mini to you soon so I am looking at least
         * is_comment_allow : 1
         * clubId : 103
         * club_name : Kidzee
         * crd : 2018-07-04 07:14:19
         * likes : 0
         * comments : 0
         */

        var news_feed_attachment: String? = null
        var news_feed_title: String? = null
        var newsFeedId: String? = null
        var news_feed_description: String? = null
        var is_comment_allow: String? = null
        var clubId: String? = null
        var club_name: String? = null
        var crd: String? = null
        var likes: String? = null
        var comments: String? = null
    }
}