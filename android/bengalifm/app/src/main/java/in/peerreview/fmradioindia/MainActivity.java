package in.peerreview.fmradioindia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageButton;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private static MainActivity s_activity;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public  ViewPagerAdapter viewadapter;
    NavigationView m_navigationView;
    private ImageButton play,prev,next,fev,vol;
    private  GifImageButton loading1, loading2;

    public Nodes m_curPlayingNode;
    public static List<Nodes> m_curPlayList = new ArrayList<>();

    public static List<Nodes> m_febPlayList = new ArrayList<>();
    private List<BaseFragment> s_allFrags = new ArrayList<>();
    public BaseFragment m_curFragment = null;

    static MediaPlayer mPlayer;
    static{
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private boolean m_isPlaying = false;
    private OkHttpClient m_Httpclient;
    private static boolean s_tryplaying;

    public static MainActivity Get(){
        return s_activity;
    }
    public static Context getContext(){
        if(s_activity != null) {
            return s_activity.getApplicationContext();
        }
        else{
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s_activity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        m_Httpclient = new OkHttpClient();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.radio);
        tabLayout.getTabAt(1).setIcon(R.drawable.fev);
        tabLayout.getTabAt(2).setIcon(R.drawable.music);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        m_navigationView = (NavigationView) findViewById(R.id.nav_view);
        m_navigationView.setNavigationItemSelectedListener(this);
        m_navigationView.bringToFront();
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);
       prev = (ImageButton) findViewById(R.id.prev);
       vol = (ImageButton) findViewById(R.id.vol);
       fev = (ImageButton) findViewById(R.id.like);
       loading2 =(GifImageButton) findViewById(R.id.loading2);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        vol.setOnClickListener(this);
        fev.setOnClickListener(this);
        loadFev();
        LoadRemoteData(null);
        sendEventLaunch();
        populateLocalFiles();
        FirebaseMessaging.getInstance().subscribeToTopic("News");
        FirebaseInstanceId.getInstance().getToken();
        onNewIntent(getIntent());
    }
    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(intent.getAction().equals("android.intent.action.MAIN")){

        } else if(intent.getAction().equals("LiveNotification")) {
            if(extras != null){
                String msg = extras.getString("message");
                String url = extras.getString("url");
                if(msg !=null && url != null){
                    Nodes n = new Nodes(msg,null,url,"LiveNotification exclusive");
                    m_curPlayingNode = n;
                    s_allFrags.get(0).addNodes(n);
                    play();
                }
            }
        } else{

        }


    }

    // *******************************Setting up the pages ************************************************
    private void setupViewPager(ViewPager viewPager) {
        viewadapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFragment b1 = new BaseFragment(R.layout.fragment_one);
        s_allFrags.add(b1);
        viewadapter.addFragment(b1, "ONE");
        BaseFragment b2 = new BaseFragment(R.layout.fragment_two);
        s_allFrags.add(b2);
        viewadapter.addFragment(b2, "TWO");
        BaseFragment b3 = new BaseFragment(R.layout.fragment_three);
        s_allFrags.add(b3);
        viewadapter.addFragment(b3, "THREE");
        viewPager.setAdapter(viewadapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }
        public Fragment getFragment(int i){
            return mFragmentList.get(i);
        }
        public Fragment getActiveFragment(ViewPager container, int position) {
            String name = makeFragmentName(container.getId(), position);
            //android.app.FragmentManager mFragmentManager = getFragmentManager();
            return  getSupportFragmentManager().findFragmentByTag(name);
        }

        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }
        public BaseFragment getVisibleFragment() {
            int cur = viewPager.getCurrentItem();
            return s_allFrags.get(cur);
        }
    }

    // ******************************* API to read remote data ************************************************

    private  List<Nodes> m_searchPlayList = new ArrayList<>();
    void handleResponse(Response response) {
        m_searchPlayList.clear();
        if (!response.isSuccessful()) {
            BaseFragment bf = s_allFrags.get(0);
            if(bf != null) {
                bf.showLoadingError();
            }
        } else {
            try {
                String jsonData = response.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONArray Jarray = null;
                Jarray = Jobject.getJSONArray("out");
                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject object = Jarray.getJSONObject(i);
                    if(object.has("name") && object.has("name")) { //TODO
                        m_searchPlayList.add(new Nodes(object.optString("name",null), object.optString("img",null), object.optString("url",null), object.optString("tags",null)));
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(m_searchPlayList, new Comparator<Nodes>() {
                            @Override
                            public int compare(Nodes a1, Nodes a2) {
                                return a1.getName().compareTo(a2.getName());
                            }
                        });
                        s_allFrags.get(0).setNodes(m_searchPlayList);

                        //show the view.
                        BaseFragment bf = s_allFrags.get(0);
                        if(bf != null) {
                            if (m_searchPlayList.size() == 0) {
                                bf.showLoadingEmpty();
                            } else {
                                bf.showLList();
                                if(m_curPlayingNode != null){
                                    m_curPlayingNode = m_searchPlayList.get(0);
                                }
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
    public void LoadRemoteData(String query){
        if(query == null){
            query = "state=active";
        }
        //show hide loading animation.
        BaseFragment bf = s_allFrags.get(0);
        if(bf != null) {
                bf.showLoadingTry();
        }
        //getting normal data
        Request request = new Request.Builder().url("http://52.89.112.230/api/nodel_bengalifm?limit=200&"+query).build();
        m_Httpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        BaseFragment bf = s_allFrags.get(0);
                        if(bf != null) {
                            bf.showLoadingError();
                        }
                    }
                });
                e.printStackTrace();
            }
            @Override
            public void onResponse(Response response) throws IOException {
                handleResponse(response);
            }
        });
        // getting ONE exclusive data
        /*
        request = new Request.Builder().url("http://52.89.112.230/api/nodel_bengalifm?limit=1&state=exclusive").build();
        m_Httpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }
            @Override
            public void onResponse(Response response) throws IOException {
                handleResponse(response);
            }
        });
        */
    }
    public void showToast(final String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,  msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ******************************* API to read local songs ************************************************
    private class ExploreLocalFilesTask extends AsyncTask<File, Integer, ArrayList<Nodes>> {
        protected ArrayList<Nodes>  doInBackground(File... urls) {
            return getPlayList(urls[0]);
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(ArrayList<Nodes> result) {
           s_allFrags.get(2).setNodes(result);
        }
    }
    ArrayList<Nodes> getPlayList(File root) {
        ArrayList<Nodes> fileList = new ArrayList<>();
        try {
            File rootFolder = root;
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file) != null) {
                        fileList.addAll(getPlayList(file));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    Nodes song = new Nodes(file.getName().substring(0,15)+"...mp3","INDISK_FILE", file.getAbsolutePath(), "IN_DISK_FILE" );
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }

    public void populateLocalFiles(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.Get(),  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            } else {
                File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                new ExploreLocalFilesTask().execute(root);
            }
        } else {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            new ExploreLocalFilesTask().execute(root);
        }
    }

    // ******************************* Run time permission managermnet ************************************************
    private final int WRITE_EXTERNAL_STORAGE = 1110;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    new ExploreLocalFilesTask().execute(root);
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }
    // ******************************* Common List pager Design ************************************************
    public int getChannelIcon(Nodes cur){
        if(cur.getType() != null  && cur.getType().indexOf("DISK") != -1){
            return R.drawable.guitar3;
        }
        else if(cur.getName().toLowerCase().indexOf("bongnet") != -1){
            return R.drawable.bongonet;
        }
        else if(cur.getName().toLowerCase().indexOf("mirchi") != -1){
            return R.drawable.mirchi ;
        }
        else if(cur.getName().toLowerCase().indexOf("air") != -1){
            return R.drawable.air;
        }
        else if(cur.getName().toLowerCase().indexOf("hungama") != -1){
            return R.drawable.hungama;
        }
        else if(cur.getName().toLowerCase().indexOf("mirchi") != -1){
            return R.drawable.mirchi;
        }
        else if(cur.getName().toLowerCase().indexOf("city") != -1){
            return R.drawable.radiocity;
        }
        else if(cur.getName().toLowerCase().indexOf("ishq") != -1){
            return R.drawable.ishq;
        }
        else if(cur.getName().toLowerCase().indexOf("big") != -1){
            return R.drawable.big;
        }
        else if(cur.getName().toLowerCase().indexOf("friend") != -1){
            return R.drawable.friends;
        }
        else if(cur.getType().toLowerCase().indexOf("exclusive") != -1){
            return R.drawable.exclusive;
        }/*/*
        else if(web.get(position).getImg() != null){
            Picasso.with(context).load(web.get(position).getImg()).into(imageView);
        } */else{
            return R.drawable.guitar1;
        }
    }



    // ******************************* Music Player Button Handlers************************************************
    @Override
    public void onClick(View v) {
        if(m_curPlayingNode == null) return;
        // Perform action on click
        switch(v.getId()) {
            case R.id.play:
                if(m_isPlaying){
                    stop();
                } else{
                    play();
                }
                break;
            case R.id.prev:
                //perform the click on previous item in the list
                for( int i =0 ;i<m_curPlayList.size();i++){
                    if (m_curPlayList.get(i) == m_curPlayingNode && i >0){
                        m_curPlayingNode = m_curPlayList.get(i-1);
                        play();
                        break;
                    }
                }

                break;
            case R.id.next:
                for( int i =0 ;i<m_curPlayList.size()-1;i++){
                    if (m_curPlayList.get(i) == m_curPlayingNode){
                        m_curPlayingNode = m_curPlayList.get(i+1);
                        play();
                        break;
                    }
                }

                break;
            case R.id.vol:
                //Stop MediaPlayer
                //MediaPlayer.create(getBaseContext(), R.raw.voicefile).stop();
                break;
            //case R.id.stop:
                //Stop MediaPlayer
                //MediaPlayer.create(getBaseContext(), R.raw.voicefile).stop();
                //break;
            case R.id.like:
                if(m_curPlayingNode != null){
                    sendTelemetry("like_click",  new HashMap<String, String>(){{
                        put("name",m_curPlayingNode.getName());
                        put("url",m_curPlayingNode.getUrl());
                    }});
                    setImage(fev,R.drawable.greenlike);
                }
                break;
        }
    }
    // ******************************* Music Player Impl************************************************
    public void play(){
        if(m_curPlayingNode == null || m_curPlayingNode.getUrl() == null) {
            showToast("Invalid FM, Please try playing others");
            return;
        };
        if(s_tryplaying == true){
            showToast("Please wait..I am trying to play "+m_curPlayingNode.getName());
            return;
        }
        s_tryplaying = true;
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.reset();
        }
        if(mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try{
            play.setVisibility(View.GONE);
            loading2.setVisibility(View.VISIBLE);
            setImage(play,R.drawable.mirchi);
            mPlayer.setDataSource(m_curPlayingNode.getUrl());
            if(m_curFragment != null){
                m_curFragment.showInProgressAnimation();
            }
            mPlayer.prepareAsync();
        }
        catch (final Exception e) {
            //UI change
            setImage(play,R.drawable.play);
            play.setVisibility(View.VISIBLE);
            loading2.setVisibility(View.GONE);
            showToast("Not able to play Music, please try again");
            stop();
            e.printStackTrace();
            sendTelemetry("play_exception",  new HashMap<String, String>(){{
                put("name",m_curPlayingNode.getName());
                put("url",m_curPlayingNode.getUrl());
                put("Exception", e.toString());
            }});
        } finally {

        }
        //if we ave an excpetion at this points let's return.
        if(mPlayer == null) return;

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
                m_isPlaying = true;
                setImage(play,R.drawable.pause);
                loading2.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                if(isInFev(m_curPlayingNode)){
                    setImage(fev,R.drawable.fevyes);
                } else{
                    setImage(fev,R.drawable.fev);
                }
                if(m_curFragment != null){
                    m_curFragment.clearAnimation();
                    m_curFragment.showPlayingAnimation();
                }
                sendTelemetry("play_success",  new HashMap<String, String>(){{
                        put("name",m_curPlayingNode.getName());
                        put("url",m_curPlayingNode.getUrl());
                }});
                pauseNotification();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
        s_tryplaying = false;
    }
    public void stop(){
        if(s_tryplaying == true){
            showToast("Please wait..I am trying to play "+m_curPlayingNode.getName());
            return;
        }
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.reset();
            mPlayer = null;
            if( m_isPlaying == true) {
                setImage(play, R.drawable.play);
                loading2.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                if (m_curFragment != null) {
                    m_curFragment.clearAnimation();
                }
            }
        }
        m_isPlaying = false;
        s_tryplaying = false;
        cancelNotification();
    }

    // ******************************* Helpers ************************************************
    void setImage(ImageView v, int id){
        if(v == null) return;
        v.setImageResource(id);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.s_live) {
            LoadRemoteData("state=active");
        } else if (id == R.id.s_kolkata) {
            LoadRemoteData("state=active&tags=kolkata");
        }else if (id == R.id.s_delhi) {
            LoadRemoteData("state=active&tags=delhi");
        }else if (id == R.id.s_mumbai) {
            LoadRemoteData("state=active&tags=mumbai");
        }else if (id == R.id.s_hyderabad) {
            LoadRemoteData("state=active&tags=hyderabad");
        }else if (id == R.id.s_pune) {
            LoadRemoteData("state=active&tags=pune");
        }else if (id == R.id.s_bangalore) {
            LoadRemoteData("state=active&tags=bangalore");
        }else if (id == R.id.s_chennai) {
            LoadRemoteData("state=active&tags=chennai");
        } else if (id == R.id.s_bangladesh) {
            LoadRemoteData("state=active&tags=bangladesh");
        } else if (id == R.id.s_hindi) {
            LoadRemoteData("state=active&tags=hindi");
        }else if (id == R.id.s_bangla) {
            LoadRemoteData("state=active&tags=bengali");
        }else if (id == R.id.s_tamil) {
            LoadRemoteData("state=active&tags=tamil");
        }else if (id == R.id.s_telegu) {
            LoadRemoteData("state=active&tags=telegu");
        }else if (id == R.id.s_marathi) {
            LoadRemoteData("state=active&tags=marathi");
        } else if (id == R.id.s_malayalam) {
            LoadRemoteData("state=active&tags=malayalam");
        } else if (id == R.id.s_kannada) {
            LoadRemoteData("state=active&tags=kannada");
        }else if (id == R.id.s_monkebaat_bengali) {
            LoadRemoteData("state=recorded&tags=mkbt_bengali");
        } else if (id == R.id.s_monkebaat_hindi) {
            LoadRemoteData("state=recorded&tags=mkbt_hindi");
        } else if (id == R.id.nav_send1) {
            //LoadRemoteData("state=active&tag=bengali");
        }
        else if (id == R.id.test) {
            //DIPANKAR TEST
            createNotification(NOTI.play);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // ******************************* Shared Preferences************************************************
    private static final String PREFS_TAG = "SharedPrefs";
    private static final String PRODUCT_TAG = "MyProduct";


    private List<Nodes> loadFev(){
        Gson gson = new Gson();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(PRODUCT_TAG, "");
        Type type = new TypeToken<List<Nodes>>() {}.getType();
        List<Nodes> temp = gson.fromJson(jsonPreferences, type);
        if(temp != null){
            m_febPlayList = temp;
        }
        s_allFrags.get(1).setNodes(m_febPlayList);
        showHideAnimation();
        return m_febPlayList;
    }
    private boolean addToFev(final Nodes x){
        if(x == null) return false ;
        sendTelemetry("addToFev",  new HashMap<String, String>(){{
            put("name",x.getName());
            put("url",x.getUrl());
        }});
        for (Nodes a : m_febPlayList){
            if(a.getUrl() != null &&  a.getUrl().equals(x.getUrl())){
                return false;
            }
        }
        m_febPlayList.add(x);
        s_allFrags.get(1).setNodes(m_febPlayList);
        showHideAnimation();
        return true;
    }
    private boolean removeFromFev(final Nodes x){
        if(x == null) return false ;
        sendTelemetry("removeFromFev",  new HashMap<String, String>(){{
            put("name",x.getName());
            put("url",x.getUrl());
        }});
        for (Nodes a : m_febPlayList){
            if(a.getUrl() != null &&  a.getUrl().equals(x.getUrl())){
                m_febPlayList.remove(x);
                s_allFrags.get(1).setNodes(m_febPlayList);
                showHideAnimation();
                return true;
            }
        }
        return false;
    }

    public int toggleFev(final Nodes x){
        if(isInFev(x)){
            if(removeFromFev(x)){
                return -1;
            }
        } else{
            if(addToFev(x)){
                return 1;
            }
        }
        return 0;
    }

    private boolean isInFev(Nodes x){
        if(x == null) return false;
        for (Nodes a : m_febPlayList){
            if(a.getUrl() != null && a.getUrl().equals(x.getUrl())){
               return true;
            }
        }
        return false;
    }
    private void saveFev(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(m_febPlayList);
        editor.putString(PRODUCT_TAG, json);
        editor.commit();
    }
    public void showHideAnimation(){
        BaseFragment bf = s_allFrags.get(1);
        if (bf != null){
            if(m_febPlayList.size() ==0){
                bf.showLoading();
            } else{
                bf.showLList();
            }
        }
    }

    //################  Notification ##############################################################
    private static NotificationManager notificationManager;
    private final int NOTIFICATION_ID =111;
    enum NOTI{
        play,
        pause,
        stop

    };
    private void createNotification(NOTI type){
        if(m_curPlayingNode == null){
            return;
        }
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        Intent notificationIntent = new Intent(MainActivity.Get().getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(MainActivity.Get().getApplicationContext(), 0,
                notificationIntent, 0);

        Notification notification;

        Intent switchIntent1 = new Intent(this, switchButtonListener.class);
        switchIntent1.setAction("stop");
        PendingIntent pendingSwitchIntent1 = PendingIntent.getBroadcast(this, 0, switchIntent1, 0);
        Intent switchIntent2 = new Intent(this, switchButtonListener.class);
        switchIntent2.setAction("play");
        PendingIntent pendingSwitchIntent2 = PendingIntent.getBroadcast(this, 0, switchIntent2, 0);
        Intent switchIntent3 = new Intent(this, switchButtonListener.class);
        switchIntent3.setAction("pause");
        PendingIntent pendingSwitchIntent3 = PendingIntent.getBroadcast(this, 0, switchIntent3, 0);
        if (type == NOTI.play) {
            notification = new NotificationCompat.Builder(MainActivity.Get().getApplicationContext())
                    .setContentIntent(intent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(getChannelIcon(m_curPlayingNode))
                    .addAction(R.drawable.greenplay, "Play", pendingSwitchIntent2)
                    .addAction(R.drawable.greenstop, "Stop", pendingSwitchIntent1)
                    .setContentTitle(m_curPlayingNode.getName())
                    .setContentText("Best FM playing app in India")
                   // .setLargeIcon(R.mipmap.ic_launcher)
                    .build();
        } else{
            notification = new NotificationCompat.Builder(MainActivity.Get().getApplicationContext())
                    .setContentIntent(intent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(getChannelIcon(m_curPlayingNode))
                    .addAction(R.drawable.greenpause, "Pause", pendingSwitchIntent3)
                    .addAction(R.drawable.greenstop, "Stop", pendingSwitchIntent1)
                    .setContentTitle(m_curPlayingNode.getName())
                    .setContentText("Best FM playing app in India")
                    //.setLargeIcon(getChannelIcon(m_curPlayingNode))
                    .build();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    void playNotification(){
        createNotification(NOTI.play);
    }
    void pauseNotification(){
        createNotification(NOTI.pause);
    }
    void cancelNotification(){
        if(notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
    public static class switchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MainActivity.Get() == null){
                return;
            }
            switch(intent.getAction()){
                case "play":
                    MainActivity.Get().play();
                    MainActivity.Get().pauseNotification();
                    break;
                case "pause":
                    MainActivity.Get().stop();
                    MainActivity.Get().playNotification();
                    break;
                case "stop":
                    MainActivity.Get().stop();
                    MainActivity.Get().cancelNotification();
                    MainActivity.Get().finish();
                    break;
            }
        }

    }
    //################  Telemetry #################################################################
    protected static String getSaltString() {
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
    void sendEventLaunch(){
        JSONObject data = new JSONObject();
        TimeZone tz = TimeZone.getDefault();
        try {
            data.put("_cmd", "insert");
            data.put("deviceinfo.version", System.getProperty("os.version")); // OS version
            data.put("deviceinfo.version", System.getProperty("os.version")); // OS version
            data.put("deviceinfo.sdk", android.os.Build.VERSION.SDK); // OS version
            data.put("deviceinfo.device", android.os.Build.DEVICE ); // OS version
            data.put("deviceinfo.model", android.os.Build.MODEL ); // OS version
            data.put("deviceinfo.product", android.os.Build.PRODUCT  ); // OS version
            data.put("deviceinfo.deviceid", Settings.Secure.getString(MainActivity.Get().getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID) ); // OS version
            data.put("deviceinfo.timezone", "TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID());

            sendTelemtry("launch", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendEventPlay(){
        JSONObject data = new JSONObject();
        try {
            data.put("_cmd", "insert");
            data.put("name",m_curPlayingNode.getName());
            data.put("url",m_curPlayingNode.getUrl());
            sendTelemtry("play",data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void sendTelemetry(String tag, Map<String,String> map){
        JSONObject data = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                data.put(entry.getKey(), entry.getValue());
            }
            sendTelemtry(tag, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendTelemtry(String tag, JSONObject json){
        if (BuildConfig.DEBUG) {
            Log.d("DIPANKAR","Skipping telemetry as debug build");
            return;
        }
        try {
            json.put("session",s_session);
            json.put("_cmd", "insert");
            json.put("_dotserializeinp",true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
            Calendar cal = Calendar.getInstance();
            String strDate = sdf.format(cal.getTime());
            json.put("timestamp",strDate);
            json.put("tag",tag);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url("http://52.89.112.230/api/nodelstat_bengalifm")
                    .post(body)
                    .build();
            m_Httpclient.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.d("Dipankar", "Telemtery: Failed "+e.toString());
                        }
                        @Override
                        public void onResponse(Response response) throws IOException {
                            Log.d("Dipankar", "Telemtery: Success "+response.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

class Nodes{
    public Nodes(String name, String img, String url, String tags) {
        this.name = name;
        this.img = img;
        this.tags = tags;
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public String getImg() {
        return img;
    }
    public String getType() {
        return tags;
    }
    public String getUrl() {
        return url;
    }
    String name,img,tags,url;
};
/* Script perser

res = [];for ( var i =0;i<31;i++){name =$($('.detailNew h5')[i]).html();url="http://cdn.narendramodi.in/"+JSON.parse($($('.detailNew .BindPop')[i]).attr('data-id')).bengali; res.push({name:name,url:url,tags:"mkbt_bengali india",state:"recorded"});} console.log(JSON.stringify(res))

 */