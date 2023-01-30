package com.example.iothumidifier.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.iothumidifier.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class LogGraphActivity extends AppCompatActivity {
    LineChart linechart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_graph);
        linechart = findViewById(R.id.logGraph);

        LineDataSet lineDataSet1 = new LineDataSet(data1(),"Data Set1");

        lineDataSet1.setValueFormatter(new ValueFormatter() {
        });
    }
    private ArrayList<Entry> data1(){
        ArrayList<Entry> dataList = new ArrayList<>();

        dataList.add(new Entry(1,1000));
        dataList.add(new Entry(2,2000));
        dataList.add(new Entry(3,3000));
        dataList.add(new Entry(4,4000));
        return dataList;

    }
}