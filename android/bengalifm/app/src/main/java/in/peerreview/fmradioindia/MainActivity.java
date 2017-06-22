package in.peerreview.fmradioindia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

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
import pl.droidsonroids.gif.GifTextView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private static MainActivity s_activity;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewadapter;
    NavigationView m_navigationView;
    private ImageButton play,prev,next,fev,vol;
    private  GifImageButton loading1, loading2;

    private Nodes m_curPlayingNode;
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
       fev = (ImageButton) findViewById(R.id.fev);
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
    private  List<Nodes> m_searchPlayList = new ArrayList<>();
    void handleResponse(Response response) {
        m_searchPlayList.clear();
        if (!response.isSuccessful()) {
            showToast("Not able to fetch data from server");
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
                                bf.showLoading();
                            } else {
                                bf.showLList();
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
                bf.showLoading();
        }

        //getting normal data
        Request request = new Request.Builder().url("http://52.89.112.230/api/nodel_bengalifm?limit=200&"+query).build();
        m_Httpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                showToast("Load FM list failed");
                e.printStackTrace();
            }
            @Override
            public void onResponse(Response response) throws IOException {
                showToast("Load FM list failed");
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
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
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
    class CustomListAdapter extends ArrayAdapter<Nodes> {

        private final Activity context;
        private final List<Nodes> web;

        public CustomListAdapter(Activity context, List<Nodes> web) {
            super(context, R.layout.list, web);
            this.context = context;
            this.web = web;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;
            if (convertView == null) {
               LayoutInflater inflater = context.getLayoutInflater();
                rowView= inflater.inflate(R.layout.list, null, true);
            } else{
                rowView= convertView;
            }
            Log.d("Dipankar",position+"");
            LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.item);
            TextView sl = (TextView) rowView.findViewById(R.id.sl);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            final ImageView fev = (ImageView) rowView.findViewById(R.id.isfev);
            TextView excl = (TextView) rowView.findViewById(R.id.excl);

            sl.setText((position+1)+".");
            final Nodes cur = web.get(position);
            if(cur == null){
                // todo
            }
            txtTitle.setText(web.get(position).getName());
            if(web.get(position).getType() != null  && web.get(position).getType().indexOf("DISK") != -1){
                setImage(imageView,R.drawable.guitar3);
            }
            else if(web.get(position).getUrl() != null  && web.get(position).getUrl().indexOf("bongonet") != -1){
                setImage(imageView,R.drawable.bongonet);
            }
            else if(web.get(position).getUrl() != null  && web.get(position).getUrl().indexOf("mirchi") != -1){
                setImage(imageView,R.drawable.mirchi);
            }
            else if(web.get(position).getUrl() != null  && web.get(position).getUrl().indexOf("airlive") != -1){
                setImage(imageView,R.drawable.air);
            }
            else if(web.get(position).getName() != null  && web.get(position).getName().indexOf("Hungama") != -1){
                setImage(imageView,R.drawable.hungama);
            }
            else if(cur.getName().toLowerCase().indexOf("mirchi") != -1){
                setImage(imageView,R.drawable.mirchi);
            }
            else if(cur.getName().toLowerCase().indexOf("city") != -1){
                setImage(imageView,R.drawable.radiocity);
            }
            else if(cur.getName().toLowerCase().indexOf("ishq") != -1){
                setImage(imageView,R.drawable.ishq);
            }
            else if(cur.getName().toLowerCase().indexOf("big") != -1){
                setImage(imageView,R.drawable.big);
            }
            else if(cur.getName().toLowerCase().indexOf("friend") != -1){
                setImage(imageView,R.drawable.friends);
            }
            else if(web.get(position).getImg() != null){
                Picasso.with(context).load(web.get(position).getImg()).into(imageView);
            } else{
                setImage(imageView,R.drawable.guitar1);
            }

            if((m_curPlayingNode != null) && m_curPlayingNode.getUrl() == web.get(position).getUrl() ){
               // ((GifTextView) rowView.findViewById(R.id.play_anim)).setVisibility(View.VISIBLE);
            }
            //exclusive item
            if(web.get(position).getType() != null  && web.get(position).getType().indexOf("Exclusive") != -1){
                layout.setBackgroundColor(Color.parseColor("#ebf442"));
                setImage(imageView,R.drawable.exclusive);
                excl.setVisibility(View.VISIBLE);
            } else{
                if(layout != null){
                   //TODO  layout.setBackgroundColor(Color.parseColor("#000"));
                }
                excl.setVisibility(View.GONE);
            }
            if(isInFev(cur)){
                setImage(fev,R.drawable.fevyesblack);
            } else{
                setImage(fev,R.drawable.fevblack);
            }
            fev.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    int res = toggleFev(cur);
                    if(res == 1){ //added
                        setImage(fev,R.drawable.fevyesblack);
                    } else if(res == -1){ //removed
                        setImage(fev,R.drawable.fevblack);
                    }
                }
            });
            return rowView;
        }
        public void clearData() {
            // clear the data
            web.clear();
            //this.setNotifyOnChange();
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
            case R.id.fev:
                Nodes n = m_curPlayingNode;
                if(isInFev(n) == false){
                    addToFev(n);
                    saveFev();
                    fev.setImageResource(R.drawable.fevyes);
                    showToast("Added to favourite");
                } else{
                    removeFromFev(n);
                    saveFev();
                    fev.setImageResource(R.drawable.fev);
                    showToast("Removed from favourite");
                }
                break;
        }
    }
    // ******************************* Music Player Impl************************************************
    public void play(){
        if(m_curPlayingNode == null || m_curPlayingNode.getUrl() == null) {
            showToast("Invalid FM, Please try playing others");
        };
        if(s_tryplaying == true){
            showToast("Please wait..I am trying to play "+m_curPlayingNode.getName());
            return;
        }
        s_tryplaying = true;
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.reset();
            mPlayer = null;
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

        if (id == R.id.s_bengali) {
            LoadRemoteData("state=active&tags=bengali");
        } else if (id == R.id.s_hindi) {
            LoadRemoteData("state=active&tags=hindi");
        } else if (id == R.id.s_bangladesh) {
            LoadRemoteData("state=active&tags=bangladesh");
        } else if (id == R.id.s_monkebaat_bengali) {
            LoadRemoteData("state=recoded&tags=monkebaat_bengali");
        } else if (id == R.id.s_monkebaat_hindi) {
            LoadRemoteData("state=recoded&tags=monkebaat_hindi");
        } else if (id == R.id.nav_send1) {
            //LoadRemoteData("state=active&tag=bengali");
        }
        else if (id == R.id.s_active) {
            LoadRemoteData("state=Active");
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

    private int toggleFev(final Nodes x){
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
    // ******************************* Fragments************************************************
    @SuppressLint("validFragment")
    public class BaseFragment extends Fragment{
        protected String m_fragmentTag;
        protected View m_myview;
        protected int m_id;
        protected CustomListAdapter m_adapter;
        protected  List<Nodes> m_NodeList = new ArrayList<>();
        private List<BaseFragment> s_allFrags = new ArrayList<>();
        public BaseFragment(int id) {
            m_id = id;
            s_allFrags.add(this);
        }
        public BaseFragment get(int id) {
            return s_allFrags.get(id);
        }

        public void setFragmentTag(String tag)
        {
            this.m_fragmentTag = tag;
        }

        public String getFragmentTag()
        {
            return this.m_fragmentTag;
        }

        @Override
        public void onResume() {
            super.onResume();

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            m_myview = inflater.inflate(m_id, container, false);
            if(m_NodeList.size() != 0){
               showLList();
            } else{
                showLoading();
            }
            m_adapter = renderList();
            return m_myview;
        }
        public View getView(){
            return m_myview;
        }

        public CustomListAdapter getListAdapter(){
            return m_adapter;
        }

        public List<MainActivity.Nodes> getNodes(){
            return m_NodeList;
        }
        public void setNodes(List<Nodes> list ){
            if (list != null){
                m_NodeList.clear();
                m_NodeList.addAll(list);
                if(m_adapter != null) {
                    m_adapter.notifyDataSetChanged();
                }
            }
        }
        private  CustomListAdapter renderList( ) {
            //list = (ListView) findViewById(R.id.list);
            CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, m_NodeList);
            ListView listview= (ListView)m_myview.findViewById(R.id.list);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "You clicked " + m_NodeList.get(position).getName(), Toast.LENGTH_SHORT).show();
                    m_curFragment = viewadapter.getVisibleFragment();
                    m_curPlayList = m_curFragment.getNodes();
                    if(m_curPlayList != null && position <m_curPlayList.size() ) {
                        m_curPlayingNode = m_curPlayList.get(position);
                        play();
                    } else{
                        showToast("Click returns invalid entry!");
                    }
                }
            });
            return adapter;
        }

        public void clearAnimation(){
            ListView listview= (ListView)m_myview.findViewById(R.id.list);
            if(listview == null) return;
            for (int i=0 ;i <listview.getChildCount();i++){
// todo                    listview.getChildAt(i).findViewById(R.id.play_anim).setVisibility(View.GONE);

            }
        }
        public void showInProgressAnimation(){
            ListView listview= (ListView)m_myview.findViewById(R.id.list);
            if(listview == null) return;
            for (int i=0 ;i <listview.getChildCount();i++){
                if(m_NodeList.get(i).getUrl().equals(m_curPlayingNode.getUrl())){
                    //setImage(listview.getChildAt(i).findViewById(R.id.play_anim),R.drawable.loading2);
                    //listview.getChildAt(i).findViewById(R.id.play_anim).setVisibility(View.VISIBLE);
                }
            }
        }
        public void showPlayingAnimation(){
            ListView listview= (ListView)m_myview.findViewById(R.id.list);
            if(listview == null) return;
            for (int i=0 ;i <listview.getChildCount();i++){
                if(m_NodeList.get(i).getUrl().equals(m_curPlayingNode.getUrl())){
                    //setImage(listview.getChildAt(i).findViewById(R.id.play_anim),R.drawable.play_anim);
                    //listview.getChildAt(i).findViewById(R.id.play_anim).setVisibility(View.VISIBLE);
                }
            }
        }

        public void showLoading() {
            if(m_myview != null){
                m_myview.findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
                m_myview.findViewById(R.id.list).setVisibility(View.GONE);
            }
        }
        public void showLList() {
            if(m_myview != null){
                m_myview.findViewById(R.id.search_loading).setVisibility(View.GONE);
                m_myview.findViewById(R.id.list).setVisibility(View.VISIBLE);
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
