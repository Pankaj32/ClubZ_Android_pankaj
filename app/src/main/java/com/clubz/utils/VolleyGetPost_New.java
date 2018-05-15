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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 14/4/17.
 */

public abstract class VolleyGetPost_New {



    Context context;
    String Url;
    boolean isMethodGet;
    int retryTime = 20000;
    Activity activity;

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

   // SessionManager sessionManager;

    /**
     * @param activity    set Your current activity Like LoginActivity.this , (LoginActivity) getActivity
     * @param context     prefferd getApplication Context
     * @param url         WebService URl
     * @param isMethodGet if false , Then Volley will call POST method you need to set the Body then, True Volley Get will executed.
     */
    public VolleyGetPost_New(Activity activity, Context context, String url, boolean isMethodGet) {
        this.context = context;
        this.Url = url;
        this.isMethodGet = isMethodGet;
        this.activity = activity;
       // this.sessionManager = new SessionManager(context);
    }

    /***
     * required this to execute the request
     */
    public void execute() {
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
                         //   if(activity instanceof HomeActivity && !sessionManager.isLoggedIn()) return;
                            onVolleyResponse(response);

                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {Util.Companion.e("response" , error.toString());
                                toast(activity, R.string.swr);
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
                    Map<String, DataPart> data = new HashMap<String, DataPart>();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);

                    try {
                        // populate text payload
                        Map<String, String> params = setParams(new HashMap<String, String>());
                        if (params != null && params.size() > 0) {
                            textParse(dos, params, getParamsEncoding());
                        }
                        // populate data byte payload
                            data = setDatapart(data);
                        if (data != null && data.size() > 0) {
                            dataParse(dos, data);
                        }
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        return bos.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bos.toByteArray();
                }
            };

            int socketTimeout = retryTime;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            postRequest.setShouldCache(false);
            ClubZ.instance.addToRequestQueue(postRequest);//,activity.getClass().getSimpleName());


        } else {
            try {
               // Toast.makeText(context,R.string.einternet,Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
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
    public VolleyGetPost_New setRetryTime(int retryTime) {
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
    public abstract Map<String, DataPart> setDatapart(Map<String, DataPart> params);


    public static void toast(Activity activity,int str){
        Toast.makeText(activity,str,Toast.LENGTH_LONG).show();
    }
    public static void toast(Activity activity,String str){
        Toast.makeText(activity,str,Toast.LENGTH_LONG).show();
    }


    /**
     * Simple data container use for passing byte file
     */
    public class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        /**
         * Default data part
         */
        public DataPart() {
        }

        /**
         * Constructor with data.
         *
         * @param name label of data
         * @param data byte data
         */
        public DataPart(String name, byte[] data) {
            fileName = name;
            content = data;
        }

        /**
         * Constructor with mime data type.
         *
         * @param name     label of data
         * @param data     byte data
         * @param mimeType mime data like "image/jpeg"
         */
        public DataPart(String name, byte[] data, String mimeType) {
            fileName = name;
            content = data;
            type = mimeType;
        }

        /**
         * Getter file name.
         *
         * @return file name
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Setter file name.
         *
         * @param fileName string file name
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Getter content.
         *
         * @return byte file data
         */
        public byte[] getContent() {
            return content;
        }

        /**
         * Setter content.
         *
         * @param content byte file data
         */
        public void setContent(byte[] content) {
            this.content = content;
        }

        /**
         * Getter mime type.
         *
         * @return mime type
         */
        public String getType() {
            return type;
        }

        /**
         * Setter mime type.
         *
         * @param type mime type
         */
        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @param data             loop through data
     * @throws IOException
     */
    private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }


    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as DataPart from collection
     * @param inputName        name of data input
     * @throws IOException
     */
    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     * @throws IOException
     */
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }


    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     * @throws IOException
     */
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        //dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

}
