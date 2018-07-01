package com.psalms40.servicebasic1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button startBtn;
    private TextView textView;

    public static final String BR_NEWS_ACTiON_NAME = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        startBtn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = v -> {
        Intent serviceIntent = new Intent(this, NewsSimpleService3.class);
        String buttonLabel = ((Button) v).getText().toString();
        if (buttonLabel.equals("서비스 시작")) {
            serviceIntent.putExtra("newsSubject", 3);
            startService(serviceIntent);
            ((Button)v).setText("서비스 멈춤");
        } else {
            stopService(serviceIntent);
            ((Button)v).setText("서비스 시작");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // manifest에 등록하는 것과 차이는 없음
        // 생명주기로 리시버 관리
        IntentFilter brFilter = new IntentFilter(BR_NEWS_ACTiON_NAME);
        registerReceiver(newsReceiver, brFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티 종료시 서비스 종료
        Intent serviceIntent = new Intent(this, NewsSimpleService2.class);
        stopService(serviceIntent);
        // 액티비티 종료시 리시버 종료
        if (newsReceiver != null) unregisterReceiver(newsReceiver);
    }

    // 코드에서 등록할 리시버
    // 범용적인 앱이 아닌 경우 manifest 에 등록하지 않고 내부에만 등록하도록 한다
    BroadcastReceiver newsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String brAction = intent.getAction();
            if (brAction.equals(BR_NEWS_ACTiON_NAME)) {
                String headLine = intent.getStringExtra("newsHeadLine");
                String section = intent.getStringExtra("newsSection");
                textView.setText(section + ":" + headLine);
            }
        }
    };


}
