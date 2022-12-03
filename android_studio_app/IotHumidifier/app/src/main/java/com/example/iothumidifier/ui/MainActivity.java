package com.example.iothumidifier.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iothumidifier.R;
import com.example.iothumidifier.ui.apicall.GetThingShadow;
import com.example.iothumidifier.ui.apicall.UpdateShadow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

//디바이스 조회와 상태변경을 수행하는 메인 액티비티
public class MainActivity extends AppCompatActivity {
    String urlStr="https://lwlzk2ffu1.execute-api.us-east-1.amazonaws.com/api/devices/Humidifier"; //API주소, 디바이스를 미리 지정함.
    Timer timer;
    Button startGetBtn;
    Button stopGetBtn;
    Dialog updateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateDialog = new Dialog(MainActivity.this);       // Dialog 초기화
        updateDialog.setContentView(R.layout.update_dialog);       // xml 레이아웃 파일과 연결
        
        //2초마다 디바이스 조회후 출력
        startGetBtn = findViewById(R.id.startGetBtn);
        startGetBtn.setEnabled(true); 
        startGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       new GetThingShadow(MainActivity.this,urlStr).execute();
                                   }
                               },
                        0,2000);
//
                startGetBtn.setEnabled(false);
                stopGetBtn.setEnabled(true);
            }
        });
        //조회 중지
        stopGetBtn = findViewById(R.id.stopGetBtn);
        stopGetBtn.setEnabled(false);
        stopGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null)
                    timer.cancel();
                clearTextView();
                startGetBtn.setEnabled(true);
                stopGetBtn.setEnabled(false);
            }
        });
        //디바이스 상태 업데이트-상태 선택하는 Dialog
        Button updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showupdateDialog();
            }
        });
        //로그 조회 버튼(새로운 창)
        Button logGetBtn = findViewById(R.id.logGetBtn);
        logGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlstr = "https://lwlzk2ffu1.execute-api.us-east-1.amazonaws.com/api/devices/Humidifier/log";
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                intent.putExtra("getLogsURL", urlstr);
                startActivity(intent);
            }
        });
    }
    //조회 중지하면 상태 표시 비우는 부분
    private void clearTextView() {
        TextView textHum = findViewById(R.id.text_hum);
        TextView textDevice = findViewById(R.id.text_device_onoff);
        TextView textWater = findViewById(R.id.text_water);
        TextView textCriteria = findViewById(R.id.text_criteria);
        TextView textMode = findViewById(R.id.text_mode);
        textHum.setText(" - ");
        textDevice.setText(" - ");
        textCriteria.setText(" - ");
        textWater.setText(" - ");
        textMode.setText(" - ");
    }
    //상태 변경 버튼을 누르면 다이어얼로그를 띄움
    public void showupdateDialog(){
        updateDialog.show();
        Button noBtn = updateDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss(); // 다이얼로그 닫기
            }
        });
        Button yesBtn = updateDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText criteria= updateDialog.findViewById(R.id.criteria);
                String c =criteria.getText().toString();
                RadioGroup modes = updateDialog.findViewById(R.id.device_mode_); //라디오버튼들의 값을 그룹으로 간단하게 가져옴
                RadioButton mode = updateDialog.findViewById( modes.getCheckedRadioButtonId() );
                String m = mode.getText().toString();
                //update shadow
                JSONObject payload = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    if (c != null && !c.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "criteria");
                        tag1.put("tagValue", c);
                        jsonArray.put(tag1);
                    }
                    if (m != null && !m.equals("")) {
                        JSONObject tag2 = new JSONObject();
                        tag2.put("tagName", "device_onoff");
                        String m_val=m;
                        if(m.equals("Auto"))
                            m_val=""; //아두이노에 ""을 보내야 auto라고 받아들이게 설정함
                        tag2.put("tagValue", m_val);
                        jsonArray.put(tag2);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e("AndroidAPITest1", "JSONEXception");
                }
                Log.e("AndroidAPITest1","payload="+payload);
                if (payload.length() >0 ) {
                    //어떤 상태 변경을 요청했는지 메인액티비티에 표시함
                    TextView rq_c=MainActivity.this.findViewById(R.id.request_criteria);
                    TextView rq_d=MainActivity.this.findViewById(R.id.request_device_onoff);
                    rq_c.setText(c);
                    rq_d.setText(m);
                    new UpdateShadow(MainActivity.this, urlStr).execute(payload);
                    Toast.makeText(MainActivity.this, "상태 변경이 요청되었습니다.", Toast.LENGTH_SHORT).show();
                    updateDialog.dismiss(); // 다이얼로그 닫기
                }else
                    Toast.makeText(MainActivity.this,"변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
}