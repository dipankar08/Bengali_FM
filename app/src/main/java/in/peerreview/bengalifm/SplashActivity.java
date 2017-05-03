package in.peerreview.bengalifm;

/**
 * Created by ddutta on 4/30/2017.
 */
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);
        TextView tv = (TextView) findViewById(R.id.title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoSlab-Bold.ttf");
        tv.setTypeface(face);

        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            String version = info.versionName;
            TextView tv1 = (TextView) findViewById(R.id.version);
            tv1.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new LoadResourceAsync().execute();
    }


    class LoadResourceAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(SplashActivity.this,"Trying to load list",Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onPostExecute(String results) {
            if(results.equals("Done")) {
                Toast.makeText(SplashActivity.this,"Loadig list complete",Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(SplashActivity.this, MusicAndroidActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            } else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                alertDialogBuilder.setMessage("Slow internet, not able to load resource! Do you want to precced with exiting channel?");
                alertDialogBuilder.setPositiveButton("yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ChannelList.populateDefault();
                        Intent mainIntent = new Intent(SplashActivity.this, MusicAndroidActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            ChannelList.setList(new ArrayList<Channel>());
            if( ServiceProxy.loadFMChannel() && ServiceProxy.loadLiveChannel()){
                return "Done";
            }
            return "Not Done";
        }
    }

}