package in.peerreview.fmradioindia.External;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;


/**
 * Created by ddutta on 9/13/2017.
 */
public class SimpleSend {
    private static String TAG = "SimpleSend";

    private SimpleSend() {
        throw new RuntimeException("Private constructor cannot be accessed");
    }

    public interface IResponseCallback {
        void success(String msg);

        void error(String msg);
    }

    public static class Builder {
        private static OkHttpClient m_Httpclient = new OkHttpClient();
        private static String url = "";
        private static Map<String, String> data;
        private static IResponseCallback callback;

        public Builder url(String url) {
            Builder.url = url;
            return this;
        }

        public Builder payload(Map<String, String> data) {
            Builder.data = data;
            return this;
        }

        public Builder callback(IResponseCallback callback) {
            Builder.callback = callback;
            return this;
        }

        public void post() {
            JSONObject json = new JSONObject();
            try {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    json.put(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            m_Httpclient.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.d(TAG, "SimpleSend: Failed " + e.toString());
                            if (callback != null) {
                                callback.error(e.toString());
                            }
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            Log.d(TAG, "SimpleSend: Success " + response.body().string());
                            if (callback != null) {
                                callback.error(response.body().string());
                            }
                        }
                    });
        }

        public void get() {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            m_Httpclient.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.d(TAG, "SimpleSend: Failed " + e.toString());
                            if (callback != null) {
                                callback.error(e.toString());
                            }
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            Log.d(TAG, "SimpleSend: Success " + response.toString());
                            if (callback != null) {
                                callback.error(response.toString());
                            }
                        }
            });
        }
    }
}