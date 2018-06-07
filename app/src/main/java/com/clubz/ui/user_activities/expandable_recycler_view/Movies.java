package com.clubz.ui.user_activities.expandable_recycler_view;

public class Movies {

    private String mName;
    private int mParentIndex;
    private int mChildIndex;

    public Movies(String name,int parentIndex,int childIndex) {
        mName = name;
        mParentIndex=parentIndex;
        mChildIndex=childIndex;
    }

    public String getName() {
        return mName;
    }

    public int getParentIndex() {
        return mParentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.mParentIndex = parentIndex;
    }

    public int getChildIndex() {
        return mChildIndex;
    }

    public void setChildIndex(int childIndex) {
        this.mChildIndex = childIndex;
    }
}
