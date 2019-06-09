package in.peerreview.fmradioindia.applogic;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import in.co.dipankar.quickandorid.utils.DLog;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class Utils {

  @Inject
  @Named("ApplicationContext")
  Context mContext;

  @Inject
  public Utils() {}

  public String getVersionString() {
    try {
      PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
      String version = pInfo.versionName;
      int no = pInfo.versionCode;
      return "Bengali FM: V-" + no + " (" + version + ")";
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "Unknown Version";
  }

  public void printExecutionTime(long startTime, long endTime) {
    long time_ns = endTime - startTime;
    long time_ms = TimeUnit.NANOSECONDS.toMillis(time_ns);
    long time_sec = TimeUnit.NANOSECONDS.toSeconds(time_ns);
    long time_min = TimeUnit.NANOSECONDS.toMinutes(time_ns);
    long time_hour = TimeUnit.NANOSECONDS.toHours(time_ns);
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("\nExecution Time: ");
    if (time_hour > 0) stringBuilder.append(time_hour + " Hours, ");
    if (time_min > 0) stringBuilder.append(time_min % 60 + " Minutes, ");
    if (time_sec > 0) stringBuilder.append(time_sec % 60 + " Seconds, ");
    if (time_ms > 0) stringBuilder.append(time_ms % 1E+3 + " MicroSeconds, ");
    if (time_ns > 0) stringBuilder.append(time_ns % 1E+6 + " NanoSeconds");

    DLog.d(stringBuilder.toString());
  }

  public static float convertDpToPixel(float dp, Context context) {
    return dp
        * ((float) context.getResources().getDisplayMetrics().densityDpi
            / DisplayMetrics.DENSITY_DEFAULT);
  }

  public static float convertPixelsToDp(float px, Context context) {
    return px
        / ((float) context.getResources().getDisplayMetrics().densityDpi
            / DisplayMetrics.DENSITY_DEFAULT);
  }
}
