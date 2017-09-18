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
import java.util.LinkedList;
import java.util.List;

import in.peerreview.fmradioindia.External.MyOkHttp;
import in.peerreview.fmradioindia.External.Telemetry;
import in.peerreview.fmradioindia.External.WelcomeActivity;
import io.paperdb.Paper;

public class Nodes {
    public Nodes(String uid, String name, String img, String url, String tags, int error,int success, int click) {
        this.uid = uid;
        this.name = name;
        this.img = img;
        this.tags = tags;
        this.mediaurl = url;
        this.count_error = error;
        this.count_success = success;
        this.count_click = click;
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
        return count_click;
    }    public int getSuccess() {
        return count_success;
    }    public int getError() {
        return count_error;
    }
    public int getRank() {
        int res = 0;
        if(count_click > 0){
            res = (int)((float)count_success/count_click);
        }
        return res + count_click*10/100;
    }
    String uid, name, img, tags, mediaurl;
    int count_error,count_success,count_click;

    private static final String url= "http://52.89.112.230/api/nodel_bengalifm?limit=300&state1=Active";
    private static final String TAG= "";
    private static List<Nodes> mNodes;

    /******************  Start of Cur Nodes  ********************************/
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
    public static void setCurNode(Nodes n) {
        for( int i =0;i<mNodes.size();i++){
            if(mNodes.get(i).getUid().equals(n.getUid())){
                mCurNodeIdx = i;
                break;
            }
        }
    }
    public static List<Nodes> getNodes() {
        return mNodes;
    }

    /******************  End of Cur Nodes  ********************************/

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
                                     object.optInt("count_error",0),
                                     object.optInt("count_success",0),
                                     object.optInt("count_click",0)));
                         }
                     }
                     new Handler(Looper.getMainLooper()).post(new Runnable() {
                         @Override
                         public void run() {
                             Collections.sort(nodes, new Comparator<Nodes>() {
                                 @Override
                                 public int compare(Nodes a1, Nodes a2) {
                                     return a2.getRank() - a1.getRank();
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





    /******************  Start of filter ********************************/
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
    /******************  End of filter  ********************************/




    /*****************  Feb List **********************************/
    private static LinkedList<Nodes> feblist;
    public static List<Nodes> getFavorite(){
        if(feblist == null){
            feblist = Paper.book().read("FevList", new LinkedList());
        }
        return feblist;
    }
    public static void handleFavorite(Nodes temp){
        if(feblist == null){
            feblist = Paper.book().read("FevList", new LinkedList());
        }
        Nodes found = null;
        for (Nodes a : feblist) {
            if(a.getName().equals(temp.getName())){
                //exist - remove
                found = a;
                break;
            }
        }
        if(found != null){
            feblist.remove(found);
            MainActivity.Get().disableFeb();
        } else{
            feblist.add(0,temp);
            if(feblist.size() == 11){
                feblist.remove(feblist.size() - 1);
            }
            MainActivity.Get().enableFeb();
        }
        Paper.book().write("FevList", feblist);
    }










    /*****************  Recent List **********************************/
    private static LinkedList<Nodes> rectlist;
    public static List<Nodes> getRecent(){
        if(rectlist == null){
            rectlist = Paper.book().read("RecentList", new LinkedList());
        }
        return rectlist;
    }
    public static void addToRecent(Nodes n){
        if(rectlist == null){
            rectlist = Paper.book().read("RecentList", new LinkedList());
        }
        for (Nodes a : rectlist) {
            if(a.getName().equals(n.getName())){
                return;
            }
        }
        rectlist.add(0,n);
        if(rectlist.size() == 11){
            rectlist.remove(rectlist.size() - 1);
        }
        Paper.book().write("RecentList", rectlist);
    }
}