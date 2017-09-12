package in.peerreview.fmradioindia.External;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/********************************************************************
 * Here is how to Use this:
 * CacheControl c = CacheControl.GET_LIVE_ELSE_CACHE;
 * MyOkHttp.getData(url, c, new IResponse() {
 *
 * @Override public void success(JSONObject jsonObject) {
 * Log.d(TAG, jsonObject.toString());
 * }
 * @Override public void error(String msg) {
 * Log.d(TAG, msg);
 * }
 * });
 *****************************************************************/

public class MyOkHttp {
    public interface IResponse {
        void success(JSONObject jsonObject);

        void error(String msg);
    }

    public enum CacheControl {
        GET_CACHE_ELSE_LIVE,
        GET_LIVE_ELSE_CACHE,
        GET_LIVE_ONLY,
        GET_CACHE_ONLY,
    }

    ;

    private static final String TAG = "MyOkHttp";
    private static OkHttpClient m_Httpclient = new OkHttpClient();
    private static boolean mDebug = false;
    private static Context mContext;

    public static void setup(Context cx) {
        mContext = cx;
    }

    public static void getData(final String url, CacheControl cacheControl, final IResponse responce) {
        final String key = url.hashCode() + "";
        Request request = new Request.Builder()
                .url(url)
                .build();

        switch (cacheControl) {
            case GET_CACHE_ONLY:
                JSONObject Jobject = readFromCache(key);
                if (Jobject == null) {
                    responce.error("Cache Not found");
                } else {
                    responce.success(Jobject);
                }
                return;
            case GET_LIVE_ONLY:
                Log.d(TAG, "Trying fetch from network....");
                m_Httpclient.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                responce.error("Internal error:" + e.getMessage());
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                try {
                                    String jsonData = response.body().string();
                                    JSONObject Jobject = new JSONObject(jsonData);
                                    responce.success(Jobject);
                                    storeInCache(key, Jobject);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    responce.error("Internal error happened while parsing the json object");
                                }
                            }
                        });
                return;
            case GET_CACHE_ELSE_LIVE:
                Jobject = readFromCache(key);
                if (Jobject != null) {
                    responce.success(Jobject);
                } else {
                    getData(url, CacheControl.GET_LIVE_ONLY, responce);
                }
                return;
            case GET_LIVE_ELSE_CACHE:
                Log.d(TAG, "Trying fetch from network....");
                m_Httpclient.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                getData(url, CacheControl.GET_CACHE_ONLY, responce);
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                try {
                                    String jsonData = response.body().string();
                                    JSONObject Jobject = new JSONObject(jsonData);
                                    responce.success(Jobject);
                                    storeInCache(key, Jobject);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    getData(url, CacheControl.GET_CACHE_ONLY, responce);
                                }
                            }
                        });
                return;
        }
    }

    private static void storeInCache(String filename, JSONObject data) {
        File cDir = mContext.getCacheDir();
        File tempFile = new File(cDir.getPath() + "/" + filename);
        ;
        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFile, false);
            writer.write(data.toString());
            writer.close();
            Log.d(TAG, "Write successfully to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Write error");
        }
    }

    private static JSONObject readFromCache(String filename) {
        File cDir = mContext.getCacheDir();
        File tempFile = new File(cDir.getPath() + "/" + filename);
        String strLine = "";
        StringBuilder text = new StringBuilder();

        /** Reading contents of the temporary file, if already exists */
        try {
            FileReader fReader = new FileReader(tempFile);
            BufferedReader bReader = new BufferedReader(fReader);

            /** Reading the contents of the file , line by line */
            while ((strLine = bReader.readLine()) != null) {
                text.append(strLine + "\n");
            }
            JSONObject x = new JSONObject(text.toString());
            Log.d(TAG, "Read succedssfuly from " + filename);
            return x;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found..");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Read error occures");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Not able to read data as Json is not valid");
        }
        return null;
    }
}
