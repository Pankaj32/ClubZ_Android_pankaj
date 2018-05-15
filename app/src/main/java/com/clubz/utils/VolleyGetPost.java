package com.clubz.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.clubz.ClubZ;
import com.clubz.ui.cv.Internet_Connection_dialog;
import com.clubz.R;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 14/4/17.
 */

public abstract class VolleyGetPost {

    private Context context;
    private String Url;
    private boolean isMethodGet;
    private int retryTime = 20000;
    private Activity activity;
   // SessionManager sessionManager;

    /**
     * @param context     prefferd getApplication Context
     * @param url         WebService URl
     * @param isMethodGet if false , Then Volley will call POST method you need to set the Body then, True Volley Get will executed.
     */
    public VolleyGetPost(Context context, String url, boolean isMethodGet) {
        this.context = context;
        this.Url = url;
        this.isMethodGet = isMethodGet;
    }


    /**
     * @param activity    set Your current activity Like LoginActivity.this , (LoginActivity) getActivity
     * @param context     prefferd getApplication Context
     * @param url         WebService URl
     * @param isMethodGet if false , Then Volley will call POST method you need to set the Body then, True Volley Get will executed.
     */
    public VolleyGetPost(Activity activity, Context context, String url, boolean isMethodGet) {
        this.context = context;
        this.Url = url;
        this.isMethodGet = isMethodGet;
        this.activity = activity;
       // this.sessionManager = new SessionManager(context);
    }

    public void execute(){
        execute("");
    }

    /***
     * required this to execute the request
     */
    public void execute(String TAG) {
        final String Url = this.Url;
        final Context contextc = this.context;
        final boolean isMethodGet = this.isMethodGet;
        //Util.e("Url",Url);
        if (Util.Companion.isConnectingToInternet(contextc)) {
            RequestQueue queue = Volley.newRequestQueue(contextc);
            int method;
            if (this.isMethodGet) method = Request.Method.GET;
            else method = Request.Method.POST;
            StringRequest postRequest = new StringRequest(method, Url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Util.Companion.e("response" , Url+response);
                         //if(activity instanceof HomeActivity && !sessionManager.isLoggedIn()) return;
                            onVolleyResponse(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {Util.Companion.e("response" , error.toString());
                                toast(R.string.swr);
                                    if(error.networkResponse.statusCode==400 && new JSONObject(new String(error.networkResponse.data)).getString("message").equals("Invalid Auth Token")){
                                        {
                                           /*
                                           sessionManager.logout(activity);
                                           toast(activity,R.string.authtoken);
                                           */
                                        }
                                    }
                                    else {

                                    }
                            }catch (Exception e){
                            }
                            onVolleyError(error);

                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String ,String> params = new HashMap<String, String>();
                    return setParams(params);
                }


                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String ,String> params = new HashMap<String, String>();
                    return setHeaders(params);
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return super.getBody();
                }
            };

            int socketTimeout = retryTime;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            postRequest.setShouldCache(false);
            ClubZ.instance.addToRequestQueue(postRequest, TAG);//,activity.getClass().getSimpleName());


        } else {

            new Internet_Connection_dialog(context) {
                @Override
                public void tryaginlistner() {
                    this.dismiss();
                    execute();
                }
            }.show();
            onNetError();
        }
    }

    /***
     * @param retryTime set the secound for 30 sec pass 30000
     */
    public VolleyGetPost setRetryTime(int retryTime) {
        this.retryTime = retryTime;
        return this;
    }

    /***
     * @param response use this reponse
     */
    public abstract void onVolleyResponse(String response);

    public abstract void onVolleyError(VolleyError error);

    /**
     * This method will be executed if internet connection is not there
     * Don't forget to dismiss the progressbar in_ this (if there)
     */
    public abstract void onNetError();

    /***
     * @param params you just need to call params.put(Key,Value)
     * @return params Do not Return null if method is post
     */
    public abstract Map<String, String> setParams(Map<String, String> params);

    /***
     * @param params you just need to call params.put(Key,Value)
     * @return params Do not return null
     */
    public abstract Map<String, String> setHeaders(Map<String, String> params);


    public void toast(int str){
        Toast.makeText(context,str,Toast.LENGTH_LONG).show();
    }
    public void toast(String str){
        Toast.makeText(context,str,Toast.LENGTH_LONG).show();
    }
}
