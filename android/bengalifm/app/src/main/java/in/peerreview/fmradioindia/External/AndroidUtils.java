package in.peerreview.fmradioindia.External;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import in.peerreview.fmradioindia.BuildConfig;

/**
 * Created by ddutta on 9/18/2017.
 */
public class AndroidUtils {
    private static Context mContext;
    public static void setup(Context cx) {
        mContext = cx;
    }

    public static boolean isDebug(){
        if (BuildConfig.DEBUG) {
            return true;
        } else{
            return false;
        }
    }
    public static void  RateIt(){
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);        try {
            mContext.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            mContext. startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
        }
    }
}


