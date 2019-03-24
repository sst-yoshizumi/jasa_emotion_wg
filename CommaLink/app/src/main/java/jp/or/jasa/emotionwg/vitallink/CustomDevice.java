package jp.or.jasa.emotionwg.vitallink;

import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import jp.co.toshiba.iflink.epaapi.EPAdata;
import jp.co.toshiba.iflink.imsif.BaseDevice;
import jp.co.toshiba.iflink.imsif.BaseIms;

public class CustomDevice extends BaseDevice {
    // 送信データのデータ名
    private static final String DATA_NAME_SEATING = "seating";
    private static final String DATA_NAME_HEARTBEAT = "heartbeat";
    private static final String DATA_NAME_BREATH = "breath";

    private static final String VITAL_LINK_DEVICE_NAME = "VitalSensor";
    private static final String VITAL_LINK_SERIAL = "any";
    private static final String VITAL_LINK_SCHEMA_NAME = "default";
    private static final String VITAL_LINK_SCHEMA = "<schema name=\"" + VITAL_LINK_SCHEMA_NAME + "\">\n" +
            " <property name=\"devicename\" type=\"string\" />\n" +
            " <property name=\"deviceserial\" type=\"string\" />\n" +
            " <property name=\"timestamp\" type=\"timestamp\" />\n" +
            " <property name=\"" + DATA_NAME_SEATING +      "\" type=\"int\" />\n" +
            " <property name=\"" + DATA_NAME_HEARTBEAT +    "\" type=\"int\" />\n" +
            " <property name=\"" + DATA_NAME_BREATH +       "\" type=\"int\" />\n" +
            "</schema>";
    private static final String VITAL_LINK_COOKIE = BaseIms.EPA_COOKIE_KEY_TYPE + "=" + BaseIms.EPA_COOKIE_TYPE_VALUE_JOB +
            ";, " + BaseIms.EPA_COOKIE_KEY_DEVICE + "=" + VITAL_LINK_DEVICE_NAME +
            ";, " + BaseIms.EPA_COOKIE_KEY_ADDRESS + "=" + BaseIms.EPA_COOKIE_VALUE_ANY;

    // デバイスステータス
    // マイクロサービスがデバイスと接続中。
    public static final int PUBDEV_STATE_CONNECTING_DEVICE = DEV_STATE_CONNECTING_DEVICE;
    // マイクロサービスがデバイスと接続完了。
    public static final int PUBDEV_STATE_CONNECTED_DEVICE = DEV_STATE_CONNECTED_DEVICE;
    // マイクロサービスからデバイスが切断された。
    public static final int PUBDEV_ERROR_DISCONNECTED_DEVICE = DEV_ERROR_DISCONNECTED_DEVICE;
    // デバイスからセンサデータが送られていない。
    public static final int PUBDEV_ERROR_NO_RESPONSE_DEVICE = DEV_ERROR_NO_RESPONSE_DEVICE;

    private static final String LOGTAG = "== VitalLink Device =";

    private static int preSeating = 0;		// 前回の離着席状況

    // 3.1.1. 初期化 - 1.2.2. コンストラクタ
    public CustomDevice(BaseIms ims) {
        // 3.1.1. 初期化 - 1.2.2.1. スーパークラスのコンストラクタ
        super(ims);
        // 3.1.1. 初期化 - 1.2.2.2. デバイス名の設定
        setDeviceName(VITAL_LINK_DEVICE_NAME);
        // 3.1.1. 初期化 - 1.2.2.3. シリアル番号の設定
        setDeviceSerial(VITAL_LINK_SERIAL);
        // 3.1.1. 初期化 - 1.2.2.4. スキーマ名の設定
        setSchemaName(VITAL_LINK_SCHEMA_NAME);
        // 3.1.1. 初期化 - 1.2.2.5. スキーマデータの設定
        setSchema(VITAL_LINK_SCHEMA);
        // 3.1.1. 初期化 - 1.2.2.6. COOKIE データの設定
        setCookie(VITAL_LINK_COOKIE);

        // ドキュメントにはないが、これを設定しないと後の createDevice() 内でエラーが発生し、先に進めない。
        setAssetName(VITAL_LINK_DEVICE_NAME);

        enableLog(true);
        Log.d(LOGTAG, "CustomDevice()");
    }

    // 3.1.1. 初期化 - 1.4.2.3. EPA 接続通知
    public void onConnectedEpa() {
        Log.d(LOGTAG, "onConnectedEpa()");
        try {
            // 3.1.1. 初期化 - 1.4.2.3.1. デバイスの状態通知
            setDeviceStatus(DEV_STATE_CONNECTED_TMS);     // ifLink アプリとマイクロサービスが接続完了
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // 3.1.3. 状態通知 - 1. 状態変化
    public void onStatusChanged(int status) {
        Log.d(LOGTAG, "onStatusChanged()");
        try {
            // 引数の違いで 2 種類ある。使い分け可能。
            // 3.1.3. 状態通知 - 1.1. デバイスの状態通知
            setDeviceStatus(status);          // int
//          // 3.1.3. 状態通知 - 1.2. 異常時
//          setDeviceStatus(status, msg);    // int, String
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // 3.1.4. センサデータ送信 - 1. センサ送信開始要求
    public void startToSendSensorData() {
        Log.d(LOGTAG, "startToSendSensorData()");
        try {
            // 3.1.4. センサデータ送信 - 2. 状態通知
            setDeviceStatus(DEV_STATE_RUN_DEVICE);    // デバイスが稼働中
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // TODO: 3.1.4. センサデータ送信 - 3. センサデータ受信
    // seating : false = 離席 / true = 着席
    // heartbeat : 心拍数      // int i = Integer.parseInt(s); で文字列を数値に変換すること。
    // breath : 呼吸数
    public void sendSensorData() {
        Log.d(LOGTAG, "sendSensorData(1)");
        sendSensorData(
                VitalLinkApplication.getSeating(),
                VitalLinkApplication.getHeartbeat(),
                VitalLinkApplication.getBreath());
    }

    public void sendSensorData(int seating, int heartbeat, int breath) {
        Log.d(LOGTAG, "sendSensorData(2): seating=" + seating + " heartbeat=" + heartbeat + " breath=" + breath);
        // 3.1.4. センサデータ送信 - 3.1. センサデータ生成
        HashMap<String, Object> sensorData = super.createSensorData();
        EPAdata data;

        removeDeviceData();
        // 3.1.4. センサデータ送信 - 3.2. デバイスデータの登録
        // 着席/離席情報：着席時 1 / 離席時 0
        // 変更があったときだけ通知する。
        if (seating != preSeating) {
            Log.d(LOGTAG, "sendSensorData(2): seating Changed! " + preSeating + " -> " + seating);
            data = new EPAdata(DATA_NAME_SEATING, "int", Integer.toString(seating));
            addDeviceData(data);
            preSeating = seating;
        }

		// 心拍数、呼吸数は前回と変更が無くても毎回通知する。
        // 心拍数情報
        data = new EPAdata(DATA_NAME_HEARTBEAT, "int", Integer.toString(heartbeat));
        addDeviceData(data);
        // 呼吸数情報
        data = new EPAdata(DATA_NAME_BREATH, "int", Integer.toString(breath));
        addDeviceData(data);

        try {
            // 3.1.4. センサデータ送信 - 3.3. センサデータの設定
            // 登録したセンサデータを先に生成したセンサデータ情報に追加
            sensorData = setDeviceData(sensorData);
            // 3.1.4. センサデータ送信 - 3.4. センサデータの送信
            sendSensor(sensorData);
        } catch (RemoteException | IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    // センサデータ（デバイスデータ）を登録する
    private void addDeviceData(String name, String type, String data) {
        Log.d(LOGTAG, "addDeviceData()");
        EPAdata epa = new EPAdata(name, type, data);
        super.addDeviceData(epa);       // デバイスデータの登録
    }

    @Override
    public boolean onJob(HashMap<String, Object> hashMap) {
        Log.d(LOGTAG, "onJob()");
        String control = String.valueOf(hashMap.get("control"));
        if (control.equalsIgnoreCase(VITAL_LINK_DEVICE_NAME)) {
            sendSensorData();
            return true;
        }
        if (control.equalsIgnoreCase(DATA_NAME_HEARTBEAT)) {
            sendSensorData();
            return true;
        }

        return false;
    }

    @Override
    public void onTimeout(int i) {
        Log.d(LOGTAG, "onTimeout()");

    }
}
