package in.peerreview.fmradioindia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.peerreview.fmradioindia.External.MediaPlayerUtils;
import in.peerreview.fmradioindia.External.MyOkHttp;
import in.peerreview.fmradioindia.External.RunTimePermission;
import in.peerreview.fmradioindia.External.Telemetry;
import pl.droidsonroids.gif.GifImageButton;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static MainActivity s_activity;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ViewPagerAdapter viewadapter;
    NavigationView m_navigationView;
    private ImageButton play, prev, next, fev, vol;
    private GifImageButton loading1, loading2;

    public Nodes m_curPlayingNode;
    public static List<Nodes> m_curPlayList = new ArrayList<>();

    public static List<Nodes> m_febPlayList = new ArrayList<>();
    private List<BaseFragment> s_allFrags = new ArrayList<>();
    public BaseFragment m_curFragment = null;


    private boolean m_isPlaying = false;
    private OkHttpClient m_Httpclient;
    private static boolean s_tryplaying;

    public static MainActivity Get() {
        return s_activity;
    }

    public static Context getContext() {
        if (s_activity != null) {
            return s_activity.getApplicationContext();
        } else {
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


        m_navigationView = (NavigationView) findViewById(R.id.nav_view);
        m_navigationView.setNavigationItemSelectedListener(this);
        m_navigationView.bringToFront();
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);
        prev = (ImageButton) findViewById(R.id.prev);
        vol = (ImageButton) findViewById(R.id.vol);
        fev = (ImageButton) findViewById(R.id.like);
        loading2 = (GifImageButton) findViewById(R.id.loading2);

        //Setting up Extenal modules....
        Telemetry.setup(this);
        MediaPlayerUtils.setup(this);
        MyOkHttp.setup(this);
        RunTimePermission.setup(this);
        Telemetry.setup(this);

        // making prerequisites
        LoadRemoteData(null);


        FirebaseMessaging.getInstance().subscribeToTopic("News");
        FirebaseInstanceId.getInstance().getToken();
        onNewIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getAction() == null || intent.getExtras() == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (intent.getAction().equals("android.intent.action.MAIN")) {

        } else if (intent.getAction().equals("LiveNotification")) {
            if (extras != null) {
                String msg = extras.getString("message");
                String url = extras.getString("url");
                if (msg != null && url != null) {
                    Nodes n = new Nodes(null, msg, null, url, "LiveNotification exclusive");
                    m_curPlayingNode = n;
                    s_allFrags.get(0).addNodes(n);
                    play();
                }
            }
        } else {

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

        public Fragment getFragment(int i) {
            return mFragmentList.get(i);
        }

        public Fragment getActiveFragment(ViewPager container, int position) {
            String name = makeFragmentName(container.getId(), position);
            //android.app.FragmentManager mFragmentManager = getFragmentManager();
            return getSupportFragmentManager().findFragmentByTag(name);
        }

        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }

        public BaseFragment getVisibleFragment() {
            int cur = viewPager.getCurrentItem();
            return s_allFrags.get(cur);
        }
    }

    // ******************************* UI rendering... ************************************************
    public int getChannelIcon(Nodes cur) {
        if (cur.getType() != null && cur.getType().indexOf("DISK") != -1) {
            return R.drawable.guitar3;
        } else if (cur.getName().toLowerCase().indexOf("bongnet") != -1) {
            return R.drawable.bongonet;
        } else if (cur.getName().toLowerCase().indexOf("mirchi") != -1) {
            return R.drawable.mirchi;
        } else if (cur.getName().toLowerCase().indexOf("air") != -1) {
            return R.drawable.air;
        } else if (cur.getName().toLowerCase().indexOf("hungama") != -1) {
            return R.drawable.hungama;
        } else if (cur.getName().toLowerCase().indexOf("mirchi") != -1) {
            return R.drawable.mirchi;
        } else if (cur.getName().toLowerCase().indexOf("city") != -1) {
            return R.drawable.radiocity;
        } else if (cur.getName().toLowerCase().indexOf("ishq") != -1) {
            return R.drawable.ishq;
        } else if (cur.getName().toLowerCase().indexOf("big") != -1) {
            return R.drawable.big;
        } else if (cur.getName().toLowerCase().indexOf("friend") != -1) {
            return R.drawable.friends;
        } else if (cur.getType().toLowerCase().indexOf("exclusive") != -1) {
            return R.drawable.exclusive;
        }/*/*
        else if(web.get(position).getImg() != null){
            Picasso.with(context).load(web.get(position).getImg()).into(imageView);
        } */ else {
            return R.drawable.guitar1;
        }
    }

    public void showLoadingTry() {
        BaseFragment bf = s_allFrags.get(0);
        if (bf != null) {
            bf.showLoadingTry();
        }
    }

    public void showLoadingError() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                BaseFragment bf = s_allFrags.get(0);
                if (bf != null) {
                    bf.showLoadingError();
                }
            }
        });
    }

    public void hideLoading() {

    }

    public void populateData(final List<Nodes> nodes) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Collections.sort(nodes, new Comparator<Nodes>() {
                    @Override
                    public int compare(Nodes a1, Nodes a2) {
                        return a1.getName().compareTo(a2.getName());
                    }
                });
                // s_allFrags.get(0).setNodes(nodes);

                //show the view.
                BaseFragment bf = s_allFrags.get(0);
                if (bf != null) {
                    if (nodes.size() == 0) {
                        bf.showLoadingEmpty();
                    } else {
                        bf.showLList();
                        if (m_curPlayingNode != null) {
                            m_curPlayingNode = nodes.get(0);
                        }
                    }
                }
            }
        });
    }

    public void showToast(final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showTryPlaying() {
        play.setVisibility(View.GONE);
        loading2.setVisibility(View.VISIBLE);
        if (m_curFragment != null) {
            m_curFragment.showInProgressAnimation();
        }
    }

    public void showPlaying() {
    }

    public void showPluse() {
    }

    public void showStop() {
        setImage(play, R.drawable.play);
        play.setVisibility(View.VISIBLE);
        loading2.setVisibility(View.GONE);
    }

    void setImage(ImageView v, int id) {
        if (v == null) return;
        v.setImageResource(id);
    }

    /////////////////////////  end of UI ////////////////////////////////////////////////////////////


    //////////////////////////////////////////  Write all New APIs Here /////////////////////////////
    public void LoadRemoteData(String query) {
        if (query == null) {
            query = "state=active";
        }
        showLoadingTry();
        String url = "http://52.89.112.230/api/nodel_bengalifm?limit=200&" + query;
        MyOkHttp.CacheControl c = MyOkHttp.CacheControl.GET_LIVE_ELSE_CACHE;
        MyOkHttp.getData(url, c, new MyOkHttp.IResponse() {
            @Override
            public void success(JSONObject jsonObject) {
                try {
                    List<Nodes> nodes = new ArrayList<Nodes>();
                    JSONArray Jarray = jsonObject.getJSONArray("out");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
                        if (object.has("name") && object.has("name")) { //TODO
                            nodes.add(new Nodes(object.optString("uid", null), object.optString("name", null), object.optString("img", null), object.optString("url", null), object.optString("tags", null)));
                        }
                    }
                    populateData(nodes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String msg) {
                showLoadingError();
                Log.d(TAG, msg);
            }
        });
    }

    public void getRunTimePermission() {
        RunTimePermission.askPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, new RunTimePermission.IPermissionCallbacks() {
            @Override
            public void success() {
                Log.d(TAG, "Success callback executed!");
                //File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                // new ExploreLocalFilesTask().execute(root);
            }

            @Override
            public void failure() {
                Log.d(TAG, "error callback executed!");
            }
        });
    }

    public void stop() {
        MediaPlayerUtils.stop(new MediaPlayerUtils.IPlayerCallback() {
            @Override
            public void tryPlaying() {

            }

            @Override
            public void success(String msg) {

            }

            @Override
            public void error(String msg, Exception e) {

            }

            @Override
            public void complete(String msg) {

            }
        });
    }

    public void play() {
        MediaPlayerUtils.play(m_curPlayingNode.getUrl(), new MediaPlayerUtils.IPlayerCallback() {
            @Override
            public void tryPlaying() {
                showLoadingTry();
            }

            @Override
            public void success(String msg) {
                showPlaying();
            }

            @Override
            public void error(String msg, Exception e) {
                showStop();
            }

            @Override
            public void complete(String msg) {
                showStop();
            }
        });
    }

    public void MusicAction(View v) {
        if (m_curPlayingNode == null) return;
        switch (v.getId()) {
            case R.id.play:
                if (MediaPlayerUtils.isPlaying()) {
                    stop();
                } else {
                    play();
                }
                break;
            case R.id.prev:
                //perform the click on previous item in the list
                for (int i = 0; i < m_curPlayList.size(); i++) {
                    if (m_curPlayList.get(i) == m_curPlayingNode && i > 0) {
                        m_curPlayingNode = m_curPlayList.get(i - 1);
                        play();
                        break;
                    }
                }

                break;
            case R.id.next:
                for (int i = 0; i < m_curPlayList.size() - 1; i++) {
                    if (m_curPlayList.get(i) == m_curPlayingNode) {
                        m_curPlayingNode = m_curPlayList.get(i + 1);
                        play();
                        break;
                    }
                }
                break;
        }
    }

    ///////////////////////////////////  Write your Code which use Extrenal here ///////////////////////////////
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
            LoadRemoteData("tags=kolkata");
        } else if (id == R.id.s_delhi) {
            LoadRemoteData("tags=delhi");
        } else if (id == R.id.s_mumbai) {
            LoadRemoteData("tags=mumbai");
        } else if (id == R.id.s_hyderabad) {
            LoadRemoteData("tags=hyderabad");
        } else if (id == R.id.s_pune) {
            LoadRemoteData("tags=pune");
        } else if (id == R.id.s_bangalore) {
            LoadRemoteData("tags=bangalore");
        } else if (id == R.id.s_chennai) {
            LoadRemoteData("tags=chennai");
        } else if (id == R.id.s_bangladesh) {
            LoadRemoteData("tags=bangladesh");
        } else if (id == R.id.s_hindi) {
            LoadRemoteData("state=active&tags=hindi");
        } else if (id == R.id.s_bangla) {
            LoadRemoteData("state=active&tags=bengali");
        } else if (id == R.id.s_tamil) {
            LoadRemoteData("state=active&tags=tamil");
        } else if (id == R.id.s_telegu) {
            LoadRemoteData("state=active&tags=telegu");
        } else if (id == R.id.s_marathi) {
            LoadRemoteData("state=active&tags=marathi");
        } else if (id == R.id.s_malayalam) {
            LoadRemoteData("state=active&tags=malayalam");
        } else if (id == R.id.s_kannada) {
            LoadRemoteData("state=active&tags=kannada");
        } else if (id == R.id.s_monkebaat_bengali) {
            LoadRemoteData("state=recorded&tags=mkbt_bengali");
        } else if (id == R.id.s_monkebaat_hindi) {
            LoadRemoteData("state=recorded&tags=mkbt_hindi");
        } else if (id == R.id.nav_send1) {
            //LoadRemoteData("state=active&tag=bengali");
        } else if (id == R.id.test) {
            //DIPANKAR TEST
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
/* Script perser

res = [];for ( var i =0;i<31;i++){name =$($('.detailNew h5')[i]).html();url="http://cdn.narendramodi.in/"+JSON.parse($($('.detailNew .BindPop')[i]).attr('data-id')).bengali; res.push({name:name,url:url,tags:"mkbt_bengali india",state:"recorded"});} console.log(JSON.stringify(res))

 */