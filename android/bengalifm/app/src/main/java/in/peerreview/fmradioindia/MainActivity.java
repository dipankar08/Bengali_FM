package in.peerreview.fmradioindia;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;

import in.peerreview.fmradioindia.External.MediaPlayerUtils;
import in.peerreview.fmradioindia.External.SimpleSend;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s_activity = this;
        setContentView(R.layout.activity_main);
        setuptoolbar();
        play = (ImageView)findViewById(R.id.play);
        prev = (ImageView)findViewById(R.id.prev);
        next = (ImageView)findViewById(R.id.next);
        rv = (RecyclerView)findViewById(R.id.rv);
        message = (TextView)findViewById(R.id.message);
        tryplayin = (GifImageView)findViewById(R.id.tryplaying);
        isplaying = (TextView) findViewById(R.id.isplaying);

        initExternal();
        setRV();
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
        //serach view.
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
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
    }
    private void initExternal() {

    }

    void setRV(){
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adapter = new RVAdapter(null,this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        adapter.update(Nodes.getNodes());
    }
    public RVAdapter getAdapter(){
        return adapter;
    }

    // Click events and helpsers
    void play(final Nodes temp){
        if(temp != null){
            final Nodes finalTemp = temp;
            MediaPlayerUtils.play(temp.getUrl(), new MediaPlayerUtils.IPlayerCallback() {
                @Override
                public void tryPlaying() {
                    TryPlayUI(finalTemp);
                }
                @Override
                public void success(String msg) {
                    PauseUI(finalTemp);
                }
                @Override
                public void error(String msg, Exception e) {
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
                        put("_payload","count");
                    }})
                    .post();
        } else{
            PlayUI(null);
        }
    }
    public void onClick(View v) {
        Nodes temp = null;
        switch (v.getId()) {
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
    void ShowLoadUI(){

    }
    void HideLoadUI(){

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
