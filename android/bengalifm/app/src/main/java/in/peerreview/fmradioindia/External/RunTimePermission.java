package in.peerreview.fmradioindia.External;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by ddutta on 8/24/2017.
 * RunTimePermission.setup(this);
 *
 * @Override public void onRequestPermissionsResult(int requestCode,
 * String permissions[], int[] grantResults) {
 * RunTimePermission.processResult(requestCode,permissions,grantResults);
 * }
 */
/*

USERS:
RunTimePermission.askPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, new IPermissionCallbacks() {
@Override
public void success() {
        Log.d(TAG,"Success callback executed!");
        }

@Override
public void failure() {
        Log.d(TAG,"error callback executed!");
        }
        });
*/

public class RunTimePermission {
    public interface IPermissionCallbacks {
        void success();

        void failure();
    }

    private static final String TAG = "RunTimePermission";
    private static Activity mContext;
    private static int count = 0;
    private static int retrycount = 0;

    public static void setup(Activity cx) {
        mContext = cx;
    }

    static HashMap<Integer, IPermissionCallbacks> maps = new HashMap<>();

    public static void askPermission(String permission, IPermissionCallbacks callback) {
        retrycount = 2;
        if (ContextCompat.checkSelfPermission(mContext, permission /**/)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, permission)) {
                Log.d(TAG, "shouldShowRequestPermissionRationale called....");
            } else {
            }
            maps.put(count, callback);
            Log.d(TAG, "Requesting....");
            ActivityCompat.requestPermissions(mContext, new String[]{permission}, count);
            count++;

        } else {
            Log.d(TAG, "Already granted!");
            if (callback != null) {
                callback.success();
            }
        }
    }

    public static void processResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "processResult recieved for " + requestCode);
        IPermissionCallbacks look = maps.get(requestCode);
        if (look != null) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Already success!");
                look.success();
            } else {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>   Permission failed <<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                look.failure();
            }
        }
    }
    //helper


}
