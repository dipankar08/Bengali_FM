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
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;

import in.peerreview.fmradioindia.External.MediaPlayerUtils;
import in.peerreview.fmradioindia.External.SimpleSend;
import in.peerreview.fmradioindia.External.Telemetry;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static MainActivity s_activity;
    public static MainActivity Get() {
        return s_activity;
    }

    private RecyclerView rv;
    private RVAdapter adapter;
    private ImageView play,next,prev;
    private GifImageView tryplayin;
    private TextView message, isplaying;
    LinearLayout qab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s_activity = this;
        setContentView(R.layout.activity_main);
        setuptoolbar();
        play = (ImageView)findViewById(R.id.play);
        prev = (ImageView)findViewById(R.id.prev);
        next = (ImageView)findViewById(R.id.next);

        message = (TextView)findViewById(R.id.message);
        tryplayin = (GifImageView)findViewById(R.id.tryplaying);
        isplaying = (TextView) findViewById(R.id.isplaying);
        qab = (LinearLayout) findViewById(R.id.qab);

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
                HideQAB();
                return false;
            }
        });
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

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

    }

    void setRV(){
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adapter = new RVAdapter(null,this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        adapter.update(Nodes.getNodes());
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        rv.setItemAnimator(animator);
    }
    public RVAdapter getAdapter(){
        return adapter;
    }

    // Click events and helpsers
    void play(final Nodes temp){
        HideQAB();
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
                                    put("_payload","success_count");
                                }})
                                .post();
                    }
                    PauseUI(finalTemp);
                }
                @Override
                public void error(String msg, Exception e) {
                    if(finalTemp != null){
                        Telemetry.sendTelemetry("play_error",  new HashMap<String, String>(){{
                            put("url",finalTemp.getUrl());
                        }});
                        new SimpleSend.Builder()
                                .url("http://52.89.112.230/api/nodel_bengalifm")
                                .payload(new HashMap<String, String>() {{
                                    put("_cmd","increment");
                                    put("id",finalTemp.getUid());
                                    put("_payload","error_count");
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
                        put("_payload","click_count");
                    }})
                    .post();
        } else{
            PlayUI(null);
        }
    }

    public void filterByTag(final String tag){
        Nodes.filterByTag(tag);
        HideQAB();
        Telemetry.sendTelemetry("click_qsb",  new HashMap<String, String>(){{
            put("id",tag);
        }});
    }

    public void onClick(View v) {
        Nodes temp = null;
        switch (v.getId()) {
            //Player Comands
            case R.id.play:
                if(MediaPlayerUtils.isPlaying()){
                    MediaPlayerUtils.stop();
                    PlayUI(null);
                } else {
                    play(Nodes.getCurNode());
                }
                break;
            case R.id.prev:
                play(Nodes.getCurNode());
                break;
            case R.id.next:
                play(Nodes.getCurNode());
                break;
            case R.id.mainbody:
                HideQAB();
                break;
            //QAB Commands
            case R.id.top5:
                filterByTag("top");
                break;
            case R.id.kolkata:
                filterByTag("kolkata");
                break;
            case R.id.hindi:
                filterByTag("hindi");
                break;
            case R.id.bangaladesh:
                filterByTag("bangladesh");
                break;
            case R.id.recent:
                filterByTag("recent");
                break;
        }
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
    }
    void TryPlayUI(Nodes n){
        message.setText("Wait, Try playing "+n.getName() +" ...");
        play.setImageResource(R.drawable.play);
        play.setVisibility(View.GONE);
        isplaying.setVisibility(View.GONE);
        tryplayin.setVisibility(View.VISIBLE);
    }
    void PauseUI(Nodes n){
        message.setText("Now Playing "+n.getName());
        play.setImageResource(R.drawable.pause);
        play.setVisibility(View.VISIBLE);
        isplaying.setVisibility(View.VISIBLE);
        tryplayin.setVisibility(View.GONE);
    }
    void ShowQAB(){
        qab.setVisibility(View.VISIBLE);
    }
    void HideQAB(){
        qab.setVisibility(View.GONE);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
