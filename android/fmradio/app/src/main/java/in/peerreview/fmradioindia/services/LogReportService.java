package in.peerreview.fmradioindia.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.network.TelemetryManager;
import in.peerreview.fmradioindia.ui.MyApplication;
import java.util.HashMap;
import javax.inject.Inject;

public class LogReportService extends Service {
  @Inject TelemetryManager mTelemetryManager;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    DLog.d("LogReportService onCreate");
    MyApplication.getMyComponent().inject(this);
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    DLog.d("LogReportService onStartCommand");
    if (intent != null) {
      HashMap<String, String> hashMap =
          (HashMap<String, String>) intent.getSerializableExtra("DATA_MAP");
      String tag = intent.getStringExtra("TAG");
      mTelemetryManager.sendData(tag, hashMap);
    }
    this.stopSelf();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    DLog.d("LogReportService onDestroy");
    super.onDestroy();
  }
}
