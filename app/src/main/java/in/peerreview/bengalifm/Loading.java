package in.peerreview.bengalifm;

import android.app.ProgressDialog;

/**
 * Created by ddutta on 4/24/2017.
 */
public class Loading {
    private static ProgressDialog progressDialog;
    public static void showDownloadProgressDialog() {
        if(progressDialog != null) return;
        progressDialog = new ProgressDialog(MusicAndroidActivity.Get());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Wait for a min!");
        progressDialog.setMessage("Retriving data from server.");
        progressDialog.show();
    }
    public static void showPlayProgressDialog() {
        if(progressDialog != null) return;
        progressDialog = new ProgressDialog(MusicAndroidActivity.Get());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Wait for a min!");
        progressDialog.setMessage("Trying to play your radio...");
        progressDialog.show();
    }
    public static void hide() {
        if( progressDialog != null){
            progressDialog.hide();
            progressDialog = null;
        }

    }
}
