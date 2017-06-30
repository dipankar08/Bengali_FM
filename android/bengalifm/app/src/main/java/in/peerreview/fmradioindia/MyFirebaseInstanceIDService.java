package in.peerreview.fmradioindia;

/**
 * Created by ddutta on 6/30/2017.
 */
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        try {
            sendRegistrationToServer(refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) throws JSONException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject data = new JSONObject();
        data.put("_cmd","gsm_register");
        data.put("reg_id",token);
        data.put("device_id", Settings.Secure.getString(MainActivity.Get().getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID));
        RequestBody body = RequestBody.create(JSON, data.toString());
        Request request = new Request.Builder()
                //.url("http://192.168.56.101/api/bengalifm_noti")
                .url("http://52.89.112.230/api/bengalifm_noti")
                .post(body)
                .build();

        OkHttpClient c = new OkHttpClient();
        try {
            c.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}