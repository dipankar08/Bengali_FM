package in.peerreview.fmradioindia.ui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.applogic.AppTrackingUtils;
import in.peerreview.fmradioindia.di.ContextModule;
import in.peerreview.fmradioindia.di.DaggerMyComponent;
import in.peerreview.fmradioindia.di.MyComponent;
import javax.inject.Inject;

public class MyApplication extends Application {

  @Inject AppTrackingUtils mAppTrackingUtils;

  public static final String CHANNEL_ID = "MUSIC_NOTIFICATION_CHANNEL";
  private static MyComponent component;

  @Override
  public void onCreate() {
    super.onCreate();
    createNotificationChannel();
    component =
        DaggerMyComponent.builder()
            .contextModule(new ContextModule(getApplicationContext()))
            .build();
    component.inject(this);
    mAppTrackingUtils.init(this);
  }

  private void createNotificationChannel() {
    DLog.d("Crearing Notification Channel");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel serviceChannel =
          new NotificationChannel(
              CHANNEL_ID, "Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(serviceChannel);
    }
  }

  public static MyComponent getMyComponent() {
    return component;
  }
}
