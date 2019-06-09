package in.peerreview.fmradioindia.applogic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class StatManager {
  private long mPlayTime = 0;
  private long mPlayCount = 0;
  private long mStartTime = 0;
  private Context mContext;

  @Inject
  public StatManager(@Named("ApplicationContext") Context context) {
    mContext = context;
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    mPlayTime = sp.getLong("TimeSpent", mPlayTime);
  }

  private void storePlayTime(long time) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    SharedPreferences.Editor editor = sp.edit();
    mPlayTime += time;
    editor.putLong("TimeSpent", mPlayTime);
    editor.apply();
  }

  public String getPlayTime() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    long time_ns = sp.getLong("TimeSpent", mPlayTime);
    StringBuilder stringBuilder = new StringBuilder();
    long time_min = TimeUnit.NANOSECONDS.toMinutes(time_ns);
    long time_hour = TimeUnit.NANOSECONDS.toHours(time_ns);
    if (time_hour > 0) stringBuilder.append(time_hour + " Hours ");
    if (time_min > 0) stringBuilder.append(time_min % 60 + " Minutes");
    return stringBuilder.toString().length() == 0 ? "0" : stringBuilder.toString();
  }

  public void storePlayCount(long time) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    SharedPreferences.Editor editor = sp.edit();
    mPlayCount += time;
    editor.putLong("PlayCount", mPlayCount);
    editor.apply();
  }

  public long getPlayCount() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    return sp.getLong("PlayCount", mPlayCount);
  }

  public void onStartPlaying() {
    if (mStartTime != 0) {
      onStopPlaying();
    }
    mStartTime = System.nanoTime();
  }

  public void onStopPlaying() {
    storePlayTime(System.nanoTime() - mStartTime);
    mStartTime = 0;
  }
}
