package in.peerreview.fmradioindia.External;

import android.content.Context;
import android.provider.Settings;
import android.support.graphics.drawable.animated.BuildConfig;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

/**
 * ------------------------------------------
 * User Guide
 * ------------------------------------------
 * Using is really simple in just two step.
 * 1. Install
 * Telemetry.setup("URL",false);
 * <p/>
 * 2. Using to pass tag and Map.Example,
 * sendTelemetry("play_exception",  new HashMap<String, String>(){{
 * put("name",m_curPlayingNode.getName());
 * put("url",m_curPlayingNode.getUrl());
 * put("Exception", e.toString());
 * }});
 * <p/>
 * <p/>
 * // Get the application Context
 * private static MainActivity sMainActivity;
 * public static MainActivity Get(){
 * return sMainActivity;
 * }
 * protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * sMainActivity = this;
 * }
 */
public class Telemetry {
    private OkHttpClient m_Httpclient;
    private boolean mDebug = false;
    private String mUrl;

    private static Telemetry mTelemetry;
    private final static String TAG = "Telemetry";
    private static Context mContext;

    public static void setup(Context cx) {
        mContext = cx;
    }

    public static Telemetry Get() {
        if (mTelemetry == null) {
            mTelemetry = new Telemetry();
        }
        return mTelemetry;
    }

    public static void setup(String url, boolean isForce) {
        Telemetry t = Get();
        t.mUrl = url;
        t.mDebug = isForce;
        t.m_Httpclient = new OkHttpClient();
        t.sendEventLaunch();
    }

    public static void sendTelemetry(String tag, Map<String, String> map) {
        Telemetry t = Get();
        if (t.m_Httpclient == null || t.mUrl == null) {
            Log.d(TAG, "You must need to call setup() first.");
            return;
        }
        if (t.mDebug == false) {
            Log.d(TAG, "Ignore Sending telemetry data as debug build ");
            return;
        }
        JSONObject data = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                data.put(entry.getKey(), entry.getValue());
            }
            t.sendTelemtry(tag, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////  Private API's ///////////////////////////////////////
    private void sendTelemtry(String tag, JSONObject json) {
        if (BuildConfig.DEBUG && !mDebug) {
            Log.d("DIPANKAR", "Skipping telemetry as debug build");
            return;
        }
        try {
            json.put("session", s_session);
            json.put("_cmd", "insert");
            json.put("_dotserializeinp", true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
            Calendar cal = Calendar.getInstance();
            String strDate = sdf.format(cal.getTime());
            json.put("timestamp", strDate);
            json.put("tag", tag);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(mUrl)
                    .post(body)
                    .build();
            m_Httpclient.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.d("Dipankar", "Telemtery: Failed " + e.toString());
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            Log.d("Dipankar", "Telemtery: Success " + response.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    private static String s_session = getSaltString();

    private void sendEventLaunch() {
        JSONObject data = new JSONObject();
        TimeZone tz = TimeZone.getDefault();
        try {
            data.put("_cmd", "insert");
            data.put("deviceinfo.version", System.getProperty("os.version")); // OS version
            data.put("deviceinfo.version", System.getProperty("os.version")); // OS version
            data.put("deviceinfo.sdk", android.os.Build.VERSION.SDK); // OS version
            data.put("deviceinfo.device", android.os.Build.DEVICE); // OS version
            data.put("deviceinfo.model", android.os.Build.MODEL); // OS version
            data.put("deviceinfo.product", android.os.Build.PRODUCT); // OS version
            data.put("deviceinfo.deviceid", Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)); // OS version
            data.put("deviceinfo.timezone", "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());
            sendTelemtry("launch", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
