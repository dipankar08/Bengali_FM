package in.peerreview.fmradioindia;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;

import in.peerreview.fmradioindia.External.AndroidUtils;
import in.peerreview.fmradioindia.External.MediaPlayerUtils;
import in.peerreview.fmradioindia.External.SimpleSend;
import in.peerreview.fmradioindia.External.Telemetry;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static MainActivity s_activity;
    public static MainActivity Get() {
        return s_activity;
    }

    private RecyclerView rv;
    private RVAdapter adapter;
    private ImageView play,next,prev,fev,lock,unlock;
    private GifImageView tryplayin;
    private TextView message, isplaying;
    private ViewGroup lock_screen;
    LinearLayout qab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s_activity = this;
        setContentView(R.layout.activity_main);
        setuptoolbar();

        initViews();
        initExternal();
        setRV();
        setSearch();
    }




    private void setSearch() {
        //serach view.
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                ShowQAB();
                Log.d("Dipankar",newText);
                Nodes.filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Dipankar",query);
                Nodes.filter(query);
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowQAB();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //Do something on collapse Searchview
                filterByTag("clear");
                HideQAB();
                return false;
            }
        });
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initExternal() {
        Paper.init(this);
        AndroidUtils.setup(this);
    }

    void setRV(){
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adapter = new RVAdapter(null,this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        adapter.update(Nodes.getNodes());
        rv.setItemAnimator(new SlideInLeftAnimator());
    }
    public RVAdapter getAdapter(){
        return adapter;
    }

    // Click events and helpsers
    void play(final Nodes temp){
        HideQAB();
        Nodes.setCurNode(temp);
        if(temp != null){
            final Nodes finalTemp = temp;
            MediaPlayerUtils.play(temp.getUrl(), new MediaPlayerUtils.IPlayerCallback() {
                @Override
                public void tryPlaying() {
                    TryPlayUI(finalTemp);
                }
                @Override
                public void success(String msg) {
                    if(finalTemp != null){
                        Telemetry.sendTelemetry("play_success",  new HashMap<String, String>(){{
                            put("url",finalTemp.getUrl());
                        }});
                        new SimpleSend.Builder()
                                .url("http://52.89.112.230/api/nodel_bengalifm")
                                .payload(new HashMap<String, String>() {{
                                    put("_cmd","increment");
                                    put("id",finalTemp.getUid());
                                    put("_payload","count_success");
                                }})
                                .post();
                        Nodes.addToRecent(finalTemp);
                    }
                    PauseUI(finalTemp);

                }
                @Override
                public void error(String msg, Exception e) {
                    if(finalTemp != null){
                        Toast.makeText(MainActivity.Get(),"Stream is not avibale for "+finalTemp.getName()+". Please try after sometime",Toast.LENGTH_SHORT).show();
                        Telemetry.sendTelemetry("play_error",  new HashMap<String, String>(){{
                            put("url",finalTemp.getUrl());
                        }});
                        new SimpleSend.Builder()
                                .url("http://52.89.112.230/api/nodel_bengalifm")
                                .payload(new HashMap<String, String>() {{
                                    put("_cmd","increment");
                                    put("id",finalTemp.getUid());
                                    put("_payload","count_error");
                                }})
                        .post();
                    }
                    PlayUI(finalTemp);
                }
                @Override
                public void complete(String msg) {
                    PlayUI(finalTemp);
                }
            });
            new SimpleSend.Builder()
                    .url("http://52.89.112.230/api/nodel_bengalifm")
                    .payload(new HashMap<String, String>() {{
                        put("_cmd","increment");
                        put("id",temp.getUid());
                        put("_payload","count_click");
                    }})
                    .post();
        } else{
            PlayUI(null);
        }
    }

    public void filterByTag(final String tag){
        if(tag.equals("recent")){
            adapter.update(Nodes.getRecent());
        } else if (tag.equals("feb")){
            adapter.update(Nodes.getFavorite());
        }else if (tag.equals("clear")){
            adapter.update(Nodes.getNodes());
        } else{
            Nodes.filterByTag(tag);
        }
        HideKeyboard();
        rv.scrollToPosition(0);
    }

    void initViews(){

        message = (TextView)findViewById(R.id.message);
        tryplayin = (GifImageView)findViewById(R.id.tryplaying);
        isplaying = (TextView) findViewById(R.id.isplaying);
        qab = (LinearLayout) findViewById(R.id.qab);


        play = (ImageView)findViewById(R.id.play);
        prev = (ImageView)findViewById(R.id.prev);
        next = (ImageView)findViewById(R.id.next);
        fev = (ImageView)findViewById(R.id.fev);
        lock = (ImageView)findViewById(R.id.lock);
        unlock = (ImageView)findViewById(R.id.unlock);

        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        fev.setOnClickListener(this);
        lock.setOnClickListener(this);


        ((TextView)findViewById(R.id.qsb_kolkata)).setOnClickListener(this);
        ((TextView)findViewById(R.id.qsb_bangaladesh)).setOnClickListener(this);
        ((TextView)findViewById(R.id.qsb_hindi)).setOnClickListener(this);
        ((TextView)findViewById(R.id.qsb_recent)).setOnClickListener(this);
        ((TextView)findViewById(R.id.qsb_clear)).setOnClickListener(this);
        ((TextView)findViewById(R.id.qsb_fev)).setOnClickListener(this);

        lock_screen = (ViewGroup) findViewById(R.id.lock_screen);
        lock_screen.setOnClickListener(this);
        unlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UnLockUI();
                return true;
            }
        });
    }

    @Override
    public void onClick(final View v)
    {
        Nodes temp = Nodes.getCurNode();
        switch (v.getId()) {
            //Player Comands
            case R.id.play:
                if(MediaPlayerUtils.isPlaying()){
                    MediaPlayerUtils.stop();
                    PlayUI(null);
                } else {
                    play(temp);
                }
                break;
            case R.id.prev:
                play(Nodes.getPrevNode());
                break;
            case R.id.next:
                play(Nodes.getNextNode());
                break;
            case R.id.fev:
                if(temp != null) {
                    Nodes.handleFavorite(temp);
                }
                break;
            case R.id.lock:
                LockUI();
                break;
            case R.id.topcontainer:
                HideQAB();
                break;
            //QAB Commands
            case R.id.qsb_kolkata:
                filterByTag("kolkata");
                break;
            case R.id.qsb_hindi:
                filterByTag("hindi");
                break;
            case R.id.qsb_bangaladesh:
                filterByTag("bangladesh");
                break;
            case R.id.qsb_recent:
                filterByTag("recent");
                break;
            case R.id.qsb_clear:
                filterByTag("clear");
                break;
            case R.id.qsb_fev:
                adapter.update(Nodes.getFavorite());
                break;
        }
        Telemetry.sendTelemetry("click_event",  new HashMap<String, String>(){{
            put("id",getResources().getResourceEntryName(v.getId()));
        }});
    }

    //UI change
    void PlayUI(Nodes n){
        if(n == null){
            message.setText("");
        } else{
            message.setText("Stoped "+n.getName());
        }
        play.setImageResource(R.drawable.play);
        play.setVisibility(View.VISIBLE);
        isplaying.setVisibility(View.GONE);
        tryplayin.setVisibility(View.GONE);
        fev.setVisibility(View.GONE);
        // Notify to remove play icon.
        adapter.notifyDataSetChanged();
    }
    void TryPlayUI(Nodes n){
        if(n != null) {
            message.setText("Wait, Try playing " + n.getName() + " ...");
        }
        play.setImageResource(R.drawable.play);
        play.setVisibility(View.GONE);
        isplaying.setVisibility(View.GONE);
        fev.setVisibility(View.GONE);
        tryplayin.setVisibility(View.VISIBLE);
    }
    void PauseUI(Nodes n){
        if(n != null) {
            message.setText("Now Playing " + n.getName());
            if(Nodes.isFev(n) == true){
                enableFeb();
            } else{
                disableFeb();
            }
        }
        play.setImageResource(R.drawable.pause);
        play.setVisibility(View.VISIBLE);
        fev.setVisibility(View.VISIBLE);
        isplaying.setVisibility(View.VISIBLE);
        tryplayin.setVisibility(View.GONE);
        //update views
        adapter.notifyDataSetChanged();
    }
    void ShowQAB(){
        qab.setVisibility(View.VISIBLE);
    }
    void HideQAB(){
        qab.setVisibility(View.GONE);
        HideKeyboard();
    }
    void HideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    void enableFeb(){
        fev.setImageResource(R.drawable.heart_active);
        fev.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }
    void disableFeb(){
        fev.setImageResource(R.drawable.heart);
        fev.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }
    void LockUI(){
        lock_screen.setVisibility(View.VISIBLE);
        lock.setVisibility(View.GONE);
        unlock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }
    void UnLockUI(){
        lock_screen.setVisibility(View.GONE);
        lock.setVisibility(View.VISIBLE);
        lock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }
    //Other overrides here
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*****************************  START NAVIGATION DRAWER SUPPORT ****************/
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.rate:
                AndroidUtils.RateIt();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String tag ="clear";
        if (id == R.id.s_live) {
            tag = "clear";
        } else if (id == R.id.s_kolkata) {
            tag = "kolkata";
        }else if (id == R.id.s_delhi) {
            tag = "delhi";
        }else if (id == R.id.s_mumbai) {
            tag = "mumbai";
        }else if (id == R.id.s_hyderabad) {
            tag = "hyderabad";
        }else if (id == R.id.s_pune) {
            tag = "pune";
        }else if (id == R.id.s_bangalore) {
            tag = "bangalore";
        }else if (id == R.id.s_chennai) {
            tag = "chennai";
        } else if (id == R.id.s_bangladesh) {
            tag = "bangladesh";
        } else if (id == R.id.s_hindi) {
            tag = "hindi";
        }else if (id == R.id.s_bangla) {
            tag = "bengali";
        }else if (id == R.id.s_tamil) {
            tag = "tamil";
        }else if (id == R.id.s_telegu) {
            tag = "telegu";
        }else if (id == R.id.s_marathi) {
            tag = "marathi";
        } else if (id == R.id.s_malayalam) {
            tag = "malayalam";
        } else if (id == R.id.s_kannada) {
            tag = "kannada";
        }
        filterByTag(tag);
        final String tag1 = tag;
        Telemetry.sendTelemetry("click_navigation",  new HashMap<String, String>(){{
            put("tag",tag1);
        }});
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
