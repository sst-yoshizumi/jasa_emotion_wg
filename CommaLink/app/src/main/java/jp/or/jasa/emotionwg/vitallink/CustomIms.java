package jp.or.jasa.emotionwg.vitallink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.toshiba.iflink.imsif.BaseIms;
import jp.or.jasa.emotionwg.vitallink.IMyService;

public class CustomIms extends BaseIms {
    private static CustomDevice customDevice;
    private String message = "Message";

    // データを送信するタイマ（ミリ秒）
    private static final int SEND_DATA_TIMER = 5000;

    private Timer timer = null;
    Handler handler = new Handler();

    // 死活監視間隔（ミリ秒）
    private static final long HEALTH_CHECK_INTERVAL = 10000;
    private static final String LOGTAG = "== VitalLink Ims =";

    // 3.1.1. 初期化 - 1.1. コンストラクタ
    public CustomIms() {
        super("VitalSensor");
        enableLog(true);
        Log.d(LOGTAG, "CustomIms()");
    }

    // 3.1.1. 初期化 - 1.2.
    @Override
    public void onCreate() {
        Log.d(LOGTAG, "onCreate()");
        // 3.1.1. 初期化 - 1.2.1. スーパークラス呼び出し
        super.onCreate();

//        messenger = new Messenger(new ServiceHandler(getApplicationContext()));

        // 3.1.1. 初期化 - 1.2.2. デバイスクラスのインスタンス生成
        customDevice = new CustomDevice(this);
        // デバイス管理リストに登録
        // ???: シーケンス図には記載なし。 3.2.1.2. onCreate に記載あり。
        // 登録することで ifLink アプリとの接続確立後に行われるデバイスの登録処理や
        // 通知された JOB の配信が自動で行われるようになる。
        super.mSensorList.add(customDevice);
    }

    // 3.1.1. 初期化 - 1.3.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOGTAG, "onStartCommand()");

        // 3.1.1. 初期化 - 1.3.1.
        return super.onStartCommand(intent, flags, startId);
    }

    // 3.1.1. 初期化 - 1.4.2.
    @Override
    protected void onEpaServiceConnected() {
        Log.d(LOGTAG, "onEpaServiceConnected()");
        //デバイス登録
        try {
            // 3.1.1. 初期化 - 1.4.2.1.
            if (!createDevice()) {
                return;
            }

            // 3.1.1. 初期化 - 1.4.2.2.
            // ifLink アプリからのイベント通知のフィルタリングと受信設定を行う
            registerCallback();

            // 3.1.1. 初期化 - 1.4.2.3. EPA接続通知
            customDevice.onConnectedEpa();

        }  catch (RemoteException | IllegalStateException e) {
            e.printStackTrace();
        }
        // 3.1.2. 死活監視 - 1. ヘルスチェック開始（ミリ秒単位）
        super.startHealthCheck(HEALTH_CHECK_INTERVAL);

        // 3.1.4. センサデータ送信 - 1. センサ送信開始要求
        customDevice.startToSendSensorData();

//        // タイマーでデータを定期的に送信する。
//        timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        Log.d(LOGTAG, "TimerTask");
////                        customDevice.sendSensorData();
//                    }
//                });
//            }
//        }, SEND_DATA_TIMER, SEND_DATA_TIMER);
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG, "onDestroy()");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onEpaServiceDisconnected() {
        Log.d(LOGTAG, "onEpaServiceDisconnected()");
        // 3.1.2. 死活監視 - 3. ヘルスチェック停止
        super.stopHealthCheck();
    }

    @Override
    protected void onEpaEvent(HashMap<String, Object> hashMap) {
        Log.d(LOGTAG, "onEpaEvent()");
        execJob(hashMap);
    }

    public CustomDevice getCustomDevice() {
        Log.d(LOGTAG, "getCustomDevice()");
        return customDevice;
    }

//    private Messenger messenger;

    static class ServiceHandler extends Handler {
        private Context cont;

        public ServiceHandler(Context aCont) {
            cont = aCont;
        }

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(cont, "Messageを受信しました。", Toast.LENGTH_SHORT).show();
            switch (msg.what) {
                case 0:
                    SensorData v = (SensorData)msg.obj;
                    customDevice.sendSensorData(v.getSeating(), v.getHeartbeat(), v.getBreath());
            }
        }
    }

    @Override
    public IBinder onBind(Intent i) {
//        super.onBind(i);
//        Toast.makeText(getApplicationContext(), "Bindしました", Toast.LENGTH_SHORT).show();
//        return messenger.getBinder();
        return IMyServiceBinder;
    }

    private final IMyService.Stub IMyServiceBinder = new IMyService.Stub() {
        public void setMessage(int aSeating, int aHeartbeat, int aBreath) throws RemoteException {
            Log.d(LOGTAG, "setMessage()");
            customDevice.sendSensorData(aSeating, aHeartbeat, aBreath);
        }
    };

    //////////////////////////////////////////////////////////////////////////
    // HTTP 通信
    public static byte[] http2data(String path) throws Exception {
        byte[] w = new byte[1024];
        HttpURLConnection c = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            // HTTP 接続のオープン
            URL url = new URL(path);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            in = c.getInputStream();

            // バイト配列の読込み
            out = new ByteArrayOutputStream();
            while (true) {
                int size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            }
            out.close();

            // HTTP 接続のクローズ
            in.close();
            c.disconnect();
            return out.toByteArray();
        } catch (Exception e) {
            try {
                if (c != null) c.disconnect();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e2) {
                ;
            }
            throw e;
        }
    }



}
