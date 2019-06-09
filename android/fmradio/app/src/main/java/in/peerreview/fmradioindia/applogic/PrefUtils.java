package in.peerreview.fmradioindia.applogic;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class PrefUtils {
  private static final String MY_PREFS_NAME = "fmradio";

  Context mContext;

  @Inject
  public PrefUtils(@Named("ApplicationContext") Context context) {
    mContext = context;
  }

  public boolean isFtux() {
    SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    return prefs.getBoolean("FTUX_DONE", false);
  }

  public void setFtuxDone(boolean isDone) {
    SharedPreferences.Editor editor =
        mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
    editor.putBoolean("FTUX_DONE", isDone);
    editor.apply();
  }
}
