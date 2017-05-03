package in.peerreview.bengalifm;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
/**
 * Created by ddutta on 4/23/2017.
 */
class ServiceProxy  {
    protected static String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);
            if (n>0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    public static boolean loadFMChannel() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("http://52.89.112.230/api/bengalifm?page=0&limit=100");
        String text = null;
        List<Channel> ans = new ArrayList<>();
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);
            JSONObject obj = new JSONObject(text);
            if(obj.getString("status").equals("success")){
                JSONArray arr = obj.getJSONArray("out");

                for (int i = 0; i < arr.length(); i++){
                    String category = arr.getJSONObject(i).getString("category");
                    String name = arr.getJSONObject(i).getString("name");
                    String url = arr.getJSONObject(i).getString("url");
                    String type = null;// arr.getJSONObject(i).getString("type");
                    ans.add(new Channel(category,name,url,type));
                }
                ChannelList.appendList(ans);
                return true;
            }
        } catch (Exception e) {
            Log.d("Duipankar", String.valueOf(e.getStackTrace()));
        }
        return false;
    }
    public static boolean loadLiveChannel() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("http://52.89.112.230/api/bengalifmlive?page=0&limit=20");
        String text = null;
        List<Channel> ans = new ArrayList<>();
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);
            JSONObject obj = new JSONObject(text);
            if(obj.getString("status").equals("success")){
                JSONArray arr = obj.getJSONArray("out");

                for (int i = 0; i < arr.length(); i++){
                    String category = arr.getJSONObject(i).getString("category");
                    String name = arr.getJSONObject(i).getString("name");
                    String url = arr.getJSONObject(i).getString("url");
                    String type = null;// arr.getJSONObject(i).getString("type");
                    ans.add(new Channel(category,name,url,type));
                }
                ChannelList.appendList(ans);
                return true;
            }
        } catch (Exception e) {
            Log.d("Duipankar", String.valueOf(e.getStackTrace()));
        }
        return false;
    }
}