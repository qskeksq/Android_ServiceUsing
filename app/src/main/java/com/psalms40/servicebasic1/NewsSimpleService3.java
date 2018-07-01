package com.psalms40.servicebasic1;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by mac on 2018. 3. 15..
 */

public class NewsSimpleService3 extends Service {

    private BackgroundThread thread;
    private MainHandler mainHandler;
    private int extraValue;

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "NewSimpleService2 서비스 생성 onCreate 호출", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (flags == START_FLAG_RETRY) {
            // 정상종료가 아닌 경우 반드시 실행되어야 하는 인텐트 정보 여기서 실행

        }

        if (intent != null) {
            // START_STICKY 모드로 비정상 종료되는 경우
            extraValue = intent.getIntExtra("section", 0);
        }

        if (thread == null) thread = new BackgroundThread();
        thread.start();

        if (mainHandler == null) mainHandler = new MainHandler(this);

        Toast.makeText(getApplicationContext(), "NewSimpleService2 onStartCommand", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    /**
     * stopService 시 호출
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 스레드 멈춤
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        // 서비스 멈춤
        this.stopSelf();
    }

    private class BackgroundThread extends Thread {
        private boolean isInterrupted = false;
        private HashMap<String, String> newsMap;
        private Random random;

        public BackgroundThread() {
            random = new Random(System.currentTimeMillis());
            newsMap = new HashMap<>();
            newsMap.put("정치", "한반도 극적 통일 이루어져~~~");
            newsMap.put("경제", "통일한국 GNP 10만 달러 이룩");
            newsMap.put("사회", "대한민국 노총각 결혼추진위원회 헌법에 명시화");
            newsMap.put("연예", "소녀시대 수영 아카데미 여우 주연상 수상");
        }

        @Override
        public void run() {
            while (!isInterrupted) {
                String newsHeadLine = "";
                String newsSection = "";
                if (extraValue == 0) {
                    newsHeadLine = newsMap.get("정치");
                    newsSection = "정치";
                } else if (extraValue == 1) {
                    newsHeadLine = newsMap.get("경제");
                    newsSection = "경제";
                } else if (extraValue == 2) {
                    newsHeadLine = newsMap.get("사회");
                    newsSection = "사회";
                } else {
                    newsHeadLine = newsMap.get("연예");
                    newsSection = "연예";
                }
                extraValue = random.nextInt(4);
                try {
                    sleep(3000);
                } catch (InterruptedException interruption) {
                    newsHeadLine = "서비스 및 백그라운드 스레드 종료";
                    newsSection = "서비스 종료";
                    currentThread().interrupt();
                }
                Intent receiverIntent = new Intent(MainActivity.BR_NEWS_ACTiON_NAME);
                receiverIntent.putExtra("newsHeadLine", newsHeadLine);
                receiverIntent.putExtra("newsSection", newsSection);
                sendBroadcast(receiverIntent);
            }
        }
    }

    private void handleMessage(Message backgroundMessage) {
        String newsMessage = (String) backgroundMessage.obj;
        switch (backgroundMessage.what) {
            case  0 :
                Toast.makeText(getApplication(),"정치면 : " +
                        newsMessage, Toast.LENGTH_LONG).show();
                break;
            case  1 :
                Toast.makeText(getApplication(), "경제면 : " +
                        newsMessage, Toast.LENGTH_LONG).show();
                break;
            case  2 :
                Toast.makeText(getApplication(), "사회면 : " +
                        newsMessage, Toast.LENGTH_LONG).show();
                break;
            case 3 :
                Toast.makeText(getApplication(), "연예면 : " +
                        newsMessage, Toast.LENGTH_LONG).show();
                break;
        }

    }

    private static class MainHandler extends Handler {

        private WeakReference<NewsSimpleService3> weakService;

        public MainHandler(NewsSimpleService3 service2) {
            weakService = new WeakReference<>(service2);
        }

        @Override
        public void handleMessage(Message msg) {
            NewsSimpleService3 service2 = weakService.get();
            service2.handleMessage(msg);
        }
    }


}
