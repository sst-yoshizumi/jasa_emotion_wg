package jp.or.jasa.emotionwg.vitallink;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import jp.or.jasa.emotionwg.vitallink.IMyService;

public class MainActivity extends AppCompatActivity {
    // センサデータを送信する周期（ms）
    private static final int SEND_MESSAGE_SPAN = 5000;

    private static final String LOGIN_URL = "<SensingWaveクラウドサービスのURL>";
    private static final String DETAIL_URL_SP = "<SensingWaveクラウドサービスのURL>";
    private static final String OTHER_URL = "<SensingWaveクラウドサービスのURL>";
    private static final String JS_SETVALUE = "javascript:document.querySelector('%s').value='%s';void 0;";
    private static final String JS_CLICKANCHOR = "javascript:document.querySelector('%s').click();";
    private static final String USER_ID_D_SP = "<SensingWaveクラウドサービス上で対象ユーザーのID>";
    private static final String LOGIN_ID = "<SensingWaveクラウドサービスのログインID>";
    private static final String LOGIN_PW = "<SensingWaveクラウドサービスのログインPW>";

    private static final String BED_IN = "bed0.png";   // "入床:ON";
    private static final String BED_OUT = "bed1.png";    // "入床:OFF";
    private static final String HEARTBEAT_TAG_SP = ">心拍数<";
//    private static final String HEARTBEAT_START = "<span style=\"font-size:x-large;\">";
    private static final String HEARTBEAT_START1 = "<span style=";
    private static final String HEARTBEAT_START2 = ">";
    private static final String HEARTBEAT_END = "</span>";
    private static final String LOGTAG = "== VitalLink Activity =";

    private final static String TAG_BEDIN = "1";
    private final static String TAG_SLEEPING   = "2";
    private final static String TAG_MORNING    = "4";
    private final static String TAG_WAKEUP   = "5";

    public int seating = 0;
    public int heartbeat = 0;
    public int breath = 0;

//    public VitalLinkApplication vitalData;
    public SensorData vitalData;

    static int bedIn = -1;     // -1 = 初回／ 0 = 離床／ 1 = 入床
    private static boolean hasLoggedIn = false;
    private Exception exception;
    private Handler handler = new Handler();
    public Handler debugMessage = new Handler();

    private Intent serviceIntent;
    private IMyService binder;

    private String text;

    private ProgressDialog progress;
    int DIALOG_DISPLAY_LENGTH = 1000;

    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//x        // サービスインテントの生成
//x        serviceIntent = new Intent(this, CustomIms.class);
//x        // サービスと接続
//x        startService();
//x        connectService();

        //Webビューの生成(1)
        WebView myWebView = (WebView)findViewById(R.id.webView1);
        myWebView.getSettings().setJavaScriptEnabled(true);

        //リンクをタップしたときに標準ブラウザを起動させない
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {    // STEP3
                Log.d(LOGTAG, "onPageFinished()");

                if (url.startsWith(DETAIL_URL_SP)) {
                    // 定期的に情報更新する。
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            view.reload();
                        }
                    };
                    view.loadUrl("javascript:window.MainActivity.viewSource(document.documentElement.outerHTML);");
                    new Handler().postDelayed(r, SEND_MESSAGE_SPAN);
                }
                else if (url.startsWith(OTHER_URL)) {
                    bedIn = -1;
                    String detailUser = DETAIL_URL_SP + USER_ID_D_SP;
                    view.loadUrl(detailUser);
                }
                else if (url.startsWith(LOGIN_URL)) {
                    if (hasLoggedIn == false) {
                        hasLoggedIn = true;
                        view.loadUrl(String.format(JS_SETVALUE, "#UserId", LOGIN_ID));
                        view.loadUrl(String.format(JS_SETVALUE, "#Password", LOGIN_PW));
                        view.loadUrl(String.format(JS_CLICKANCHOR, "#login_button"));
                    }
                    bedIn = -1;
                }
                else {
                    bedIn = -1;
                    String detailUser = DETAIL_URL_SP + USER_ID_D_SP;
                    view.loadUrl(detailUser);
                }
            }
        });

        myWebView.addJavascriptInterface(this, "MainActivity");    // STEP1 "MyWebViewActivity"

        // ログインページにアクセスする。
        myWebView.loadUrl(this.LOGIN_URL);
    }

    public void debugMessge(final String src) {
        debugMessage.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), src, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @JavascriptInterface    // STEP2
    public void viewSource(final String src) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                final String data;
                boolean statusChanged = false;

                TextView text = (TextView) findViewById(R.id.textView);
                text.setText(src);    // STEP4
                data = src;

//x                // 離席・着席の確認
//x                int resultBed = data.indexOf(BED_IN);
//x                if (resultBed != -1) {
//x                    // 最初はメッセージを表示しない
//x                    if (bedIn == -1) {
//x                    // 着席
//x                    }
//x                    else if (bedIn != 1) {   // 前回も着席ならメッセージを表示しない
//x                        showMessage("VitalLink", "着席しました。");
//x                        statusChanged = true;
//x                    }
//x                    bedIn = 1;
//x                    seating = 1;
//x                }
//x                else {
//x                    // 最初はメッセージを表示しない
//x                    if (bedIn == -1) {
//x                    }
//x                    // 離席
//x                    else if (bedIn != 0) {   // 前回も離席ならメッセージを表示しない
//x                        showMessage("VitalLink", "離席しました。");
//x                        statusChanged = true;
//x                    }
//x                    bedIn = 0;
//x                    seating = 0;
//x                }

// for debug
//                if (seating == 0) {
//                    showMessage("VitalLink", "着席しました。");
//                    seating = 1;
//                }
//                else {
//                    showMessage("VitalLink", "離席しました。");
//                    seating = 0;
//                }

                // 心拍数の取得
                heartbeat = getHeartbeatValue(data);
                if (heartbeat >= 30) {
                    // 心拍の検出の閾値として、30以上なら検出したとみなす。30未満はノイズと考えてみる。
                    // 心拍を検出して、前回は初めてまたは離床状態なら、入床したとみなす。
                    // 心拍を検出しても、前回入床していたら、もう通知しない。
                    if (bedIn <= 0) {
                        bedIn = 1;
                        seating = 1;
                        Log.d("***** debug *****","seating 1");
                    }
                    else {
                        seating = 0;
                        Log.d("***** debug *****","seating 0");
                    }
                }
//d                else if (bedIn > 0) {
//d                    // ここに来るということは、心拍数が低いか 0 で、かつ前回は入床状態なので、今回は離床とみなす。
//d                    bedIn = 0;
//d                    seating = 0;
//d                }
                else {
                    // ここに来たら、心拍数が低いか 0 なので、離床状態とする。
                    bedIn = 0;
                    seating = 0;
                    Log.d("***** debug *****","left");
                }

                // ifLink 3.1.4. センサデータ送信 - 3. センサデータ送信
                sendData(seating, heartbeat, breath);
            }
        });
    }

    private int getHeartbeatValue(String data) {
        String hb = "0";
        int startPos = 0;
        int endPos = 0;
        boolean found = false;
        int result = 0;

        startPos = data.indexOf(HEARTBEAT_TAG_SP);
        if (startPos < 0) {     // タグが見つからなければ、心拍数 0 とします。
            return 0;
        }
        startPos = data.indexOf(HEARTBEAT_START1, startPos);
        if (startPos < 0) {     // タグが見つからなければ、心拍数 0 とします。
            return 0;
        }
        startPos = data.indexOf(HEARTBEAT_START2, startPos);
        if (startPos < 0) {     // タグが見つからなければ、心拍数 0 とします。
            return 0;
        }
        startPos += 1;          // 開始タグの次の文字から心拍数の数値の始まり。
        endPos = data.indexOf(HEARTBEAT_END, startPos);
        if (startPos < 0) {     // タグが見つからなければ、心拍数 0 とします。（終わりのタグが見つからないことはないはずですが念のため。）
            return 0;
        }
        hb = data.substring(startPos, endPos);
        result = Integer.parseInt(hb);

        return result;
    }


    public void showMessage(String title, String text) {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.setTitle(title);
        progress.setMessage(text);
        progress.show();
        // 指定時間後にダイアログを消す
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
            }
        }, DIALOG_DISPLAY_LENGTH);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // サービス関連
    private boolean bConnected = false;

    // サービスが起動中かどうか調べる
    private boolean isServiceRunning(String className) {
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < serviceInfos.size(); i++) {
            if (serviceInfos.get(i).service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    // サービスの開始
    public void startService() {
        if (bConnected == true) {
            return;
        }
        // サービスの開始
        startService(serviceIntent);
    }

    // サービスとの接続
    public void connectService() {
        if (isServiceRunning("jp.or.jasa.emotionwg.vitallink.CustomIms")) {
            bindService(serviceIntent, connection, BIND_AUTO_CREATE);
            bConnected = true;
        }
    }

    // サービスの停止
    public void disconnectService() {
        if (bConnected == false) {
            return;
        }
        // サービスとの切断
        unbindService(connection);
        // サービスの停止
        stopService(serviceIntent);

        bConnected = false;
    }

    // サービスコネクションの生成
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = IMyService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };

    // サービスの操作
    private void sendData(int aSeating, int aHeartbeat, int aBreath) {
        Log.d(LOGTAG, "sendData()");
        try {
            if (aSeating == 1) {
                // 入床したメッセージを送信する。
                Thread thread = new Thread(new Runnable() { public void run() {
                    // HTTP 通信
                    try {
                        text = new String(sendRequest(TAG_BEDIN));
                    } catch (Exception e) {
                        text = null;
                    }
                    // ハンドラの生成
                    handler.post(new Runnable() { public void run() {
                        if (text != null) {
//                    editText.setText(text);
                        } else {
//                    editText.setText("読込み失敗しました。");
                        }
                    }});
                }});
                thread.start();
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Exception @ sendData()");
        }
    }

    //HTTP 通信
    public static byte[] sendRequest(String request) throws Exception {
//        if(request.length() != 0){
//            task = new UploadTask();
//            task.setListener(createListener());
//            task.execute(request);
//        }

        byte[] w = new byte[1024];

        String urlSt = "<オリジナルIoTサービスのURL>";
        urlSt += request;

        HttpURLConnection con = null;
        String result = null;
//            String word = "word="+params[0];
        String word = request;
        Log.d("***** debug *****","sendRequest : " + urlSt);

        try {
            // URL設定
            URL url = new URL(urlSt);

            // HttpURLConnection
            con = (HttpURLConnection) url.openConnection();

            // request POST
//            con.setRequestMethod("POST");
            con.setRequestMethod("PUT");

            // no Redirects
            con.setInstanceFollowRedirects(false);

            // データを書き込む
            con.setDoOutput(true);

            // 時間制限
            con.setReadTimeout(10000);
            con.setConnectTimeout(20000);

            // 接続
            con.connect();

            // POSTデータ送信処理
            OutputStream out = null;
            try {
                out = con.getOutputStream();
                out.write( word.getBytes("UTF-8") );
                out.flush();
                Log.d("***** debug *****","sendRequest : Sent(flush)");
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
                result="POST送信エラー";
                Log.d("***** debug *****","sendRequest : IOException");
            } finally {
                Log.d("***** debug *****","sendRequest : finally");
                if (out != null) {
                    out.close();
                }
            }

            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // レスポンスを受け取る処理等
                result="HTTP_OK";
            }
            else{
                result="status="+String.valueOf(status);
            }

        } catch (IOException e) {
            Log.d("***** debug *****","sendRequest : IOException 1");
            e.printStackTrace();
        } finally {
            Log.d("***** debug *****","sendRequest : finally 1");
            if (con != null) {
                con.disconnect();
            }
        }

        return w;
    }
}
