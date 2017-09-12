package in.peerreview.fmradioindia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static MainActivity s_activity;
    public static MainActivity Get() {
        return s_activity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s_activity = this;
        setContentView(R.layout.activity_main);
    }
}