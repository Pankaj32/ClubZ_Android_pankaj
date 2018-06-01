package com.clubz.ui.user_activities.listioner;

/**
 * Created by chiranjib on 28/5/18.
 */

public interface ParentViewClickListioner {
    void onItemMenuClick(int position);
    void onItemClick(int position);
    void onItemLike(int position);
    void onItemChat(int position);
    void onItemJoin(int position);
}
