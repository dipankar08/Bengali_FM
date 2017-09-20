package in.peerreview.fmradioindia.External;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import in.peerreview.fmradioindia.MainActivity;
import in.peerreview.fmradioindia.Nodes;
import in.peerreview.fmradioindia.R;

/**
 * Created by ddutta on 9/15/2017.
 */
public class WelcomeActivity  extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageView radio;

    private static WelcomeActivity s_activity;
    public static WelcomeActivity Get() {
        return s_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        s_activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        animate();
        loading();
        initExternal();
    }

    private void initExternal() {
        MyOkHttp.setup(this);
        Telemetry.setup(this, "http://52.89.112.230/api/stat_bengalifm",false);
    }

    //step1: Write what should you do.
    private void loading() {
        Nodes.loadData();
    }

    void animate(){
        radio = (ImageView)findViewById(R.id.logo);
        Animation rotation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.bounce);
        rotation.setFillAfter(true);
        radio.startAnimation(rotation);
    }

    public void next(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    public void exit(){
        this.finish();
    }
}