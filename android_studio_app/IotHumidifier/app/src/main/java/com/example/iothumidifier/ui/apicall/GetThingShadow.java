package com.example.iothumidifier.ui.apicall;


import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iothumidifier.R;
import com.example.iothumidifier.httpconnection.GetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetThingShadow extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetThingShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null)
            return;
        Map<String, String> state = getStateFromJSONString(jsonString);
        TextView textHum = activity.findViewById(R.id.text_hum);
        TextView textDevice = activity.findViewById(R.id.text_device_onoff);
        TextView textWater = activity.findViewById(R.id.text_water);
        TextView textCriteria = activity.findViewById(R.id.text_criteria);
        TextView textMode = activity.findViewById(R.id.text_mode);
        //,humidity,water,device_onoff,criteria,self
        textHum.setText(state.get("humidity")+" %");
        textWater.setText(state.get("water")+" %");
        textDevice.setText(state.get("device_onoff"));
        textCriteria.setText(state.get("criteria")+" %");
        Log.e(TAG,state.get("criteria")+" %");
        String mode;
        if(state.get("self").equals("false"))
            mode="Auto";
        else
            mode="Self";
        textMode.setText(mode);
    }

    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            //,humidity,water,device_onoff,criteria,self
            String hValue = reported.getString("humidity");
            String wValue = reported.getString("water");
            String dValue = reported.getString("device_onoff");
            String cValue = reported.getString("criteria");
            String sValue = reported.getString("self");
            output.put("humidity", hValue);
            output.put("water",wValue);
            output.put("device_onoff",dValue);
            output.put("criteria", cValue);
            output.put("self", sValue);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}
