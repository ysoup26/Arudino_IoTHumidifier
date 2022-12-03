package com.example.iothumidifier.ui.apicall;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.iothumidifier.httpconnection.PutRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UpdateShadow extends PutRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    public UpdateShadow(Activity activity, String urlStr) {

        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();

        }
    }
    //결과 json을 팝업으로 띄워줌
    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(activity,result, Toast.LENGTH_SHORT).show();
    }
    
}
