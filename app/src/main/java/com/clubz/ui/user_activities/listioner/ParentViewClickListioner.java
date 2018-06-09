package com.clubz.ui.user_activities.listioner;

import android.widget.ImageView;

/**
 * Created by chiranjib on 28/5/18.
 */

public interface ParentViewClickListioner {
    void onItemMenuClick(int position, ImageView itemMenu);
    void onItemClick(int position);
    void onItemLike(int position,String type);
    void onItemChat(int position);
    void onItemJoin(int position,String type);
}
