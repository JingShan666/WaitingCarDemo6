package com.example.mvp.waitingcardemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    int i = 0;
    private WaitingCarView waitingCarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waitingCarView = (WaitingCarView) findViewById(R.id.didi_view);
        final Timer timer = new Timer();
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                if (i>=360) {
                    i = 0;
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(timeTask,500,50);//每100毫秒时候刷新一下，第三个参数

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                waitingCarView.setData(i+=1);
            }
            super.handleMessage(msg);
        }
    };
    }

