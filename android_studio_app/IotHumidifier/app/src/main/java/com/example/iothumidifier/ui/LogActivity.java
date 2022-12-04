package com.example.iothumidifier.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.iothumidifier.R;
import com.example.iothumidifier.ui.apicall.GetLog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

//시작 날짜와 종료 날짜를 선택하고 그 사이 데이터를 출력하는 액티비티
public class LogActivity extends AppCompatActivity {

    String getLogsURL;

    private TextView textView_Date1;
    private TextView textView_Date2;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final static String TAG = "AndroidAPITest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        //log에 대한 api url 획득
        Intent intent = getIntent();
        getLogsURL = intent.getStringExtra("getLogsURL");
        Log.i(TAG, "getLogsURL="+getLogsURL);

        Button startDateBtn = findViewById(R.id.start_date_button);
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        textView_Date1 = (TextView)findViewById(R.id.textView_date1);
                        textView_Date1.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth));
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2022, 11, 1);

                dialog.show();


            }
        });

        Button startTimeBtn = findViewById(R.id.start_time_button);
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView textView_Time1 = (TextView)findViewById(R.id.textView_time1);
                        textView_Time1.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                };

                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 14, 25, false);
                dialog.show();

            }
        });

        Button endDateBtn = findViewById(R.id.end_date_button);
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        textView_Date2 = (TextView)findViewById(R.id.textView_date2);
                        textView_Date2.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth));
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2022, 11, 1);

                dialog.show();


            }
        });

        Button endTimeBtn = findViewById(R.id.end_time_button);
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView textView_Time2 = (TextView)findViewById(R.id.textView_time2);
                        textView_Time2.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                };

                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 14, 28, false);
                dialog.show();

            }
        });
        //로그 조회 시작 버튼
        Button start = findViewById(R.id.log_start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetLog(LogActivity.this,getLogsURL).execute();
            }
        });

        //로그 조회 시작 버튼
        Button start_graph = findViewById(R.id.log_start_graph_button);
        start_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              //new GetLog(LogActivity.this,getLogsURL).execute();
                // 배경 색 (Adobe에서 색상조합표를 찾아보고 RGB 값을 따 왔음)
                TextView message = findViewById(R.id.message2);
                message.setText("조회중...");
                DrawGraph();

            }
        });

    }
    public void DrawGraph(){
        LineChart chart;
        // XML에서 생성해둔 View를 id를 통해 연결
        chart = (LineChart) findViewById(R.id.logGraph);
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 3));
        entries.add(new Entry(6, 1));
        entries.add(new Entry(7, 2));
        entries.add(new Entry(8, 0));
        entries.add(new Entry(9, 4));
        entries.add(new Entry(10, 3));

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));

        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        chart.setDoubleTapToZoomEnabled(true);
        //chart.animateY(2000, Easing.EasingOption.EaseInCubic);
        chart.invalidate();

//        ArrayList<Entry> values = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//
//            float val = (float) (Math.random() * 10); //값 집어넣는 부분
//
//            values.add(new Entry(i, val));
//        }
//        Log.e("AndroidAPITest1", String.valueOf(values));
//        LineDataSet set1;
//        set1 = new LineDataSet(values, "DataSet 1");
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1); // add the data sets
//
//        // create a data object with the data sets
//        LineData data = new LineData(dataSets);
//
//        // black lines and points
//        set1.setColor(Color.BLACK);
//        set1.setCircleColor(Color.BLACK);
//
//        // set data
//        chart.setData(data);
//        chart.setBackgroundColor(Color.LTGRAY); // 그래프 배경 색 설정
//        set1.setColor(Color.BLACK); // 차트의 선 색 설정
//        set1.setCircleColor(Color.BLACK); // 차트의 points 점 색 설정
//
//        set1.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
//        //set1.setFillColor(Color.BLACK); // 차트 아래 채우기 색 설정
    }

}