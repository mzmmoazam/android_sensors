package com.example.mzm.activity_inspector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mzm.activity_inspector.Models.activity_model;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private TextView textView, textView2, temp_db, top1, top2;
    private Button history, back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1221);
        }

        MessageReciever reciever = new MessageReciever(new Message());

        textView = (TextView) findViewById(R.id.textview);
        textView2 = (TextView) findViewById(R.id.textview2);
        temp_db = (TextView) findViewById(R.id.temp_db);
        history = (Button) findViewById(R.id.next_page);
        back = (Button) findViewById(R.id.back);
        top1 = (TextView) findViewById(R.id.top1);
        top2 = (TextView) findViewById(R.id.top2);

        Intent intent = new Intent(this, main_service.class);
        intent.putExtra("reciever", reciever);
        startService(intent);


        history.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {

                // get data from backend and set that text to the db_temp
                temp_db.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                history.setVisibility(View.GONE);
                top1.setVisibility(View.VISIBLE);
                top2.setVisibility(View.VISIBLE);

                db_helper db = new db_helper(getApplicationContext());
                ArrayList<activity_model> rows = db.get_data();
                StringBuffer str = new StringBuffer();
                for (activity_model item : rows) {
                    str.append(String.format("%s %s %s %s %s %s\n", item.start_time, item.end_time, item.dist, item.speed, item.acc, item.stance));
                }
                temp_db.setText(str);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                history.setVisibility(View.VISIBLE);
                temp_db.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                top2.setVisibility(View.GONE);
                top1.setVisibility(View.GONE);


            }
        });

    }

    public class Message {
        public void msgFromService(int resultCode, Bundle resultData) {
            if (resultData.getString("stance") != null) {
                textView.setText(resultData.getString("stance"));
            }
            if (resultData.getString("speed") != null) {
                textView2.setText(resultData.getString("speed"));
            }
        }
    }

}
