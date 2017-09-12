package in.peerreview.fmradioindia;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.peerreview.fmradioindia.External.MyOkHttp;

public class Nodes {
    public Nodes(String uid, String name, String img, String url, String tags, int count) {
        this.uid = uid;
        this.name = name;
        this.img = img;
        this.tags = tags;
        this.imgurl = url;
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
        return url;
    }
    public int getCount() {
        return count;
    }
    String uid, name, img, tags, imgurl;
    int count;

    private static final String url= "http://52.89.112.230/api/nodel_bengalifm?limit=200&state=Active";
    private static final String TAG= "";
    private static List<Nodes> mNodes;
    public static void loadData(){
        final List<Nodes> nodes = new ArrayList<>();
        MyOkHttp.CacheControl c = MyOkHttp.CacheControl.GET_LIVE_ELSE_CACHE;
        MyOkHttp.getData(url, c, new MyOkHttp.IResponse() {
             @Override public void success(JSONObject jsonObject) {
                Log.d(TAG, jsonObject.toString());

                 JSONArray Jarray = null;
                 try {
                     Jarray = jsonObject.getJSONArray("out");
                     for (int i = 0; i < Jarray.length(); i++) {
                         JSONObject object = Jarray.getJSONObject(i);
                         if(object.has("name") && object.has("name")) { //TODO
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
                                     return a1.getName().compareTo(a2.getName());
                                 }
                             });
                             MainActivity.Get().getAdapter().update(nodes);
                             mNodes= nodes;
                             Toast.makeText(MainActivity.Get(),  "FM List is loaded successfully!", Toast.LENGTH_SHORT).show();

                         }});

                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
             @Override public void error(String msg) {
                 Toast.makeText(MainActivity.Get(),  "FM List is loaded successfully!", Toast.LENGTH_SHORT).show();
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
}