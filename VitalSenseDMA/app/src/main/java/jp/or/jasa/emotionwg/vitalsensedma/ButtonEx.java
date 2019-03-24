package jp.or.jasa.emotionwg.vitalsensedma;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//ボタンとダイアログ
public class ButtonEx extends Activity implements
        View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_BEDIN = "1";
    private final static String TAG_SLEEPING   = "2";
    private final static String TAG_MORNING    = "4";
    private final static String TAG_WAKEUP   = "5";
    //    private UploadTask task;
    private Handler handler = new Handler();
    private String text;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //ボタンの生成(1)
        layout.addView(makeButton(res2bmp(this, R.drawable.bed_bedin), TAG_BEDIN));
        layout.addView(makeButton(res2bmp(this, R.drawable.bed_sleeping), TAG_SLEEPING));
        layout.addView(makeButton(res2bmp(this, R.drawable.bed_sleepy_morning), TAG_MORNING));
        layout.addView(makeButton(res2bmp(this, R.drawable.bed_wakeup), TAG_WAKEUP));
//        layout.addView(makeButton("メッセージダイアログの表示", TAG_BEDIN));
//        layout.addView(makeButton("Yes/Noダイアログの表示", TAG_SLEEPING));
//        layout.addView(makeButton("テキスト入力ダイアログの表示", TAG_MORNING));
//        layout.addView(makeButton(res2bmp(this, R.drawable.sample), TAG_WAKEUP));
    }

    //ボタンの生成(1)
    private Button makeButton(String text, String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }

    //イメージボタンの生成(3)
    private ImageButton makeButton(Bitmap bmp, String tag) {
        ImageButton button = new ImageButton(this);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setImageBitmap(bmp);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }

    //リソース→ビットマップ
    public Bitmap res2bmp(Context context, int resID) {
        return BitmapFactory.decodeResource(
                context.getResources(), resID);
    }

    //ボタン押下時に呼ばれる(2)
    public void onClick(View view) {
        final String tag = (String)view.getTag();

        Thread thread = new Thread(new Runnable() { public void run() {
            // HTTP 通信
            try {
//                text = new String(http2data(tag));
                text = new String(sendRequest(tag));
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

//        MessageDialog.show(this, "メッセージダイアログ", "ボタンを押した");
    }

    //HTTP 通信
    public static byte[] http2data(String request) throws Exception {
        byte[] w = new byte[1024];
        HttpURLConnection c = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        String urlSt = "<オリジナルIoTサービスのURL>";	// 2018年COMMAハウスでは、SSTのAzureに構築した。
        urlSt += request;

        try {
            URL url = new URL(urlSt);
            c = (HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            in = c.getInputStream();

            out = new ByteArrayOutputStream();
            while (true) {
                int size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            }
            out.close();

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

    //HTTP 通信
    public static byte[] sendRequest(String request) throws Exception {
//        if(request.length() != 0){
//            task = new UploadTask();
//            task.setListener(createListener());
//            task.execute(request);
//        }

        byte[] w = new byte[1024];
        HttpURLConnection c = null;

		String urlSt = "<オリジナルIoTサービスのURL>";	// 2018年COMMAハウスでは、SSTのAzureに構築した。
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //メッセージダイアログの定義(4)
    public static class MessageDialog extends DialogFragment {
        //ダイアログの表示(5)
        public static void show(
                Activity activity, String title, String text) {
            MessageDialog f = new MessageDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("text", text);
            f.setArguments(args);
            f.show(activity.getFragmentManager(), "messageDialog");
        }
    }
}
