package jp.or.jasa.emotionwg.vitallink;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class VitalLinkApplication extends Application {
    private static Context context;
    private static int seating = 0;
    private static int heartbeat = 0;
    private static int breath = 0;
    private static CustomDevice customDevice;

    private static final String LOGTAG = "=== VitalLinkApp ===";

    public void onCreate() {
        super.onCreate();
        VitalLinkApplication.context = getApplicationContext();
        Log.d(LOGTAG, "onCreate()");
    }

    public static Context getAppContext() {
        Log.d(LOGTAG, "getAppContext()");
        return VitalLinkApplication.context;
    }

    public static void setParameter(int aSeating, int aHeartbeat, int aBreath) {
        Log.d(LOGTAG, "setParameter()");
        VitalLinkApplication.seating = aSeating;
        VitalLinkApplication.heartbeat = aHeartbeat;
        VitalLinkApplication.breath = aBreath;
    }

    public static int getSeating() {
        return VitalLinkApplication.seating;
    }

    public static int getHeartbeat() {
        return VitalLinkApplication.heartbeat;
    }

    public static int getBreath() {
        return VitalLinkApplication.breath;
    }

    public static void setCustomDevice(CustomDevice aCustomDevice) {
        customDevice = aCustomDevice;
    }

    public static CustomDevice getCustomDevice() {
        return customDevice;
    }
}
