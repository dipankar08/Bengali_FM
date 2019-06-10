package in.peerreview.fmradioindia.applogic;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.network.TelemetryManager;
import in.peerreview.fmradioindia.ui.MyApplication;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppTrackingUtils implements Application.ActivityLifecycleCallbacks {

  private int numberOfActivities = 0;
  private long startTime = 0L;

  @Inject TelemetryManager mTelemetryManager;

  @Inject
  public AppTrackingUtils() {
    MyApplication.getMyComponent().inject(this);
  }

  public void init(Application application) {
    application.registerActivityLifecycleCallbacks(this);
  }

  @Override
  public void onActivityCreated(Activity activity, Bundle bundle) {
    if (numberOfActivities == 0) {
      startTime = System.currentTimeMillis();
    }
    numberOfActivities++;
  }

  @Override
  public void onActivityStarted(Activity activity) {}

  @Override
  public void onActivityResumed(Activity activity) {}

  @Override
  public void onActivityPaused(Activity activity) {}

  @Override
  public void onActivityStopped(Activity activity) {
    numberOfActivities--;
    if (numberOfActivities == 0) {
      Long appUsedTime = System.currentTimeMillis() - startTime;
      DLog.d("User used app for $timeString" + (appUsedTime / 1000) + "second");
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("time", appUsedTime.toString());
      mTelemetryManager.sendDataWithService(
          activity, TelemetryManager.TELEMETRY_TIME_APP_FOREGROUD, map);
    }
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

  @Override
  public void onActivityDestroyed(Activity activity) {}
}
