package jp.or.jasa.emotionwg.vitallink;

import android.util.Log;

public class SensorData {
    public int seating = 0;
    public int heartbeat = 0;
    public int breath = 0;
    private static final String LOGTAG = "== VitalLink Data =";

    public void setParameter(int aSeating, int aHeartbeat, int aBreath) {
        Log.d(LOGTAG, "setParameter()");
        seating = aSeating;
        heartbeat = aHeartbeat;
        breath = aBreath;
    }

    public int getSeating() {
        return seating;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public int getBreath() {
        return breath;
    }
}
