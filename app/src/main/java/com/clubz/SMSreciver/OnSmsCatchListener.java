package com.clubz.SMSreciver;

/**
 * Created by dharmraj on 21/12/17.
 */

public interface OnSmsCatchListener<T> {
    void onSmsCatch(String message);
}