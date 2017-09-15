package in.peerreview.fmradioindia;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import in.peerreview.fmradioindia.External.MyOkHttp;
import in.peerreview.fmradioindia.External.Telemetry;
import in.peerreview.fmradioindia.External.WelcomeActivity;

public class Nodes {
    public Nodes(String uid, String name, String img, String url, String tags, int count) {
        this.uid = uid;
        this.name = name;
        this.img = img;
        this.tags = tags;
        this.mediaurl = url;
        this.count = count;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getTags() {
        return tags;
    }

    public String getUrl() {
        return mediaurl;
    }
    public int getCount() {
        return count;
    }
    String uid, name, img, tags, mediaurl;
    int count;

    private static final String url= "http://52.89.112.230/api/nodel_bengalifm?limit=300";
    private static final String TAG= "";
    private static List<Nodes> mNodes;
    private static int mCurNodeIdx = 0;

    public static Nodes getCurNode(){
        if( mNodes == null){
            return null;
        }
        return mNodes.get(mCurNodeIdx);
    }
    public static Nodes getPrevNode(){
        return mNodes.get(--mCurNodeIdx);
    }
    public static Nodes getNextNode(){
        return mNodes.get(++mCurNodeIdx);
    }

    public static void loadData(){
        final List<Nodes> nodes = new ArrayList<>();
        MyOkHttp.CacheControl c = MyOkHttp.CacheControl.GET_LIVE_ELSE_CACHE;
        final long startTime = System.currentTimeMillis();
        MyOkHttp.getData(url, c, new MyOkHttp.IResponse() {
             @Override public void success(JSONObject jsonObject) {
                Log.d(TAG, jsonObject.toString());
                 JSONArray Jarray;
                 try {
                     Jarray = jsonObject.getJSONArray("out");
                     for (int i = 0; i < Jarray.length(); i++) {
                         JSONObject object = Jarray.getJSONObject(i);
                         if(object.has("name") && object.has("url")) { //TODO
                             nodes.add(new Nodes(
                                     object.optString("uid",null),
                                     object.optString("name",null),
                                     object.optString("img",null),
                                     object.optString("url",null),
                                     object.optString("tags",null),
                                     object.optInt("count",0)));
                         }
                     }
                     new Handler(Looper.getMainLooper()).post(new Runnable() {
                         @Override
                         public void run() {
                             Collections.sort(nodes, new Comparator<Nodes>() {
                                 @Override
                                 public int compare(Nodes a1, Nodes a2) {
                                     return a2.getCount() - a1.getCount();
                                 }
                             });
                             mNodes= nodes;
                             final long endTime   = System.currentTimeMillis();
                             Telemetry.sendTelemetry("data_fetch_time",  new HashMap<String, String>(){{
                                  put("time",endTime - startTime+"");
                             }});
                             WelcomeActivity.Get().next();
                         }});

                 } catch (JSONException e) {
                     e.printStackTrace();
                     WelcomeActivity.Get().exit();
                 }
             }
             @Override public void error(String msg) {
                 WelcomeActivity.Get().exit();
             }
        });
    }
    public static void filter(String text){
        if(mNodes == null) return;
        ArrayList<Nodes> filterdNames = new ArrayList<>();
        for (Nodes n : mNodes) {
            if (n.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(n);
            }
        }
        MainActivity.Get().getAdapter().update(filterdNames);
    }
    public static void filterByTag(String text){
        if(mNodes == null) return;
        ArrayList<Nodes> filterdNames = new ArrayList<>();
        for (Nodes n : mNodes) {
            if (n.getTags().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(n);
            }
        }
        MainActivity.Get().getAdapter().update(filterdNames);
    }
    public static void topFive(String text){
        if(mNodes == null) return;
        ArrayList<Nodes> filterdNames = new ArrayList<>();
        for (Nodes n : mNodes) {
            if (n.getTags().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(n);
            }
        }
        MainActivity.Get().getAdapter().update(filterdNames);
    }

    public static void FrequentlyPlayed(String text){
        if(mNodes == null) return;
        ArrayList<Nodes> filterdNames = new ArrayList<>();
        for (Nodes n : mNodes) {
            if (n.getTags().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(n);
            }
        }
        MainActivity.Get().getAdapter().update(filterdNames);
    }

    public static List<Nodes> getNodes() {
        return mNodes;
    }
}