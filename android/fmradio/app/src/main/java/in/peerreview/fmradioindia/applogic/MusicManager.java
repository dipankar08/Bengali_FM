package in.peerreview.fmradioindia.applogic;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import in.co.dipankar.quickandorid.services.Item;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.network.TelemetryManager;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MusicManager {
  private MainActivity mContext;
  private TelemetryManager mTelemetryManager;
  private StatManager mStatManager;
  private ChannelManager mChannelManager;
  List<Callback> mCallbacks;

  private List<Channel> mFullChannelList;

  @Inject
  public MusicManager(
      TelemetryManager telemetryManager, StatManager statManager, ChannelManager channelManager) {
    mTelemetryManager = telemetryManager;
    mStatManager = statManager;
    mChannelManager = channelManager;
    mFullChannelList = new ArrayList<>();
    mCallbacks = new ArrayList<>();
  }

  public interface Callback {
    void onTryPlaying(Channel channelForId);

    void onSuccess(Channel channelForId);

    void onResume(Channel channelForId);

    void onError(Channel channelForId, String msg);

    void onPause(Channel channelForId);
  }

  public void addCallback(Callback callback) {
    mCallbacks.add(callback);
  }

  public void removeCallback(Callback callback) {
    mCallbacks.remove(callback);
  }

  public void startService(MainActivity context) {
    mContext = context;
    bindService();
  }

  public void stopService() {
    unBindService();
    mStatManager.onStopPlaying();
  }
  // Music player callbacks.
  private void unBindService() {}

  public void bindService() {
    Intent intent = new Intent(mContext, MusicService.class);
    mContext.bindService(intent, mConnection, BIND_AUTO_CREATE);
  }

  private ServiceConnection mConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
          MusicService.LocalBinder myLocalBinder = (MusicService.LocalBinder) iBinder;
          MusicForegroundService musicService = myLocalBinder.getService();
          DLog.d("Connected to bounded service");
          musicService.setCallback(
              new MusicForegroundService.Callback() {
                @Override
                public void onTryPlaying(String id, String msg) {
                  DLog.d("Binder::onTryPlaying called");
                  for (Callback callback : mCallbacks) {
                    callback.onTryPlaying(mChannelManager.getChannelForId(id));
                  }
                  mTelemetryManager.dbIncrementCount(id);
                  mTelemetryManager.markHit(TelemetryManager.TELEMETRY_PLAYER_TRY_PLAYING);
                  mStatManager.storePlayCount(1);
                }

                @Override
                public void onSuccess(String id, String msg) {
                  for (Callback callback : mCallbacks) {
                    callback.onSuccess(mChannelManager.getChannelForId(id));
                  }
                  DLog.d("Binder::onSuccess called");
                  mTelemetryManager.markHit(TelemetryManager.TELEMETRY_PLAYER_SUCCESS);
                  mTelemetryManager.dbIncrementSuccess(id);
                  mTelemetryManager.rankUp(id);
                  mStatManager.onStartPlaying();
                }

                @Override
                public void onResume(String id, String msg) {
                  for (Callback callback : mCallbacks) {
                    callback.onResume(mChannelManager.getChannelForId(id));
                  }
                  DLog.d("Binder::onResume called");
                  mTelemetryManager.rankUp(id);
                  mStatManager.onStartPlaying();
                }

                @Override
                public void onPause(String id, String msg) {
                  for (Callback callback : mCallbacks) {
                    callback.onPause(mChannelManager.getChannelForId(id));
                  }
                  DLog.d("Binder::onPause called");
                  mStatManager.onStopPlaying();
                }

                @Override
                public void onError(String id, String msg) {
                  for (Callback callback : mCallbacks) {
                    callback.onError(mChannelManager.getChannelForId(id), msg);
                  }
                  DLog.d("Binder::onError called");
                  mTelemetryManager.markHit(TelemetryManager.TELEMETRY_PLAYER_ERROR);
                  mTelemetryManager.dbIncrementError(id);
                  mTelemetryManager.rankDown(id);
                  mStatManager.onStopPlaying();
                }

                @Override
                public void onSeekBarPossionUpdate(String id, int total, int cur) {
                  DLog.d("Binder::onSeekBarPossionUpdate called");
                }

                @Override
                public void onMusicInfo(String id, HashMap<String, Object> info) {
                  DLog.d("Binder::onMusicInfo called");
                }

                @Override
                public void onComplete(String id, String msg) {
                  DLog.d("Binder::onComplete called");
                }
              });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          DLog.d("Disconnected to bounded service");
        }
      };

  public void playNext() {
    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_NEXT_BUTTON);
    Intent mService = new Intent(mContext, MusicService.class);
    mService.setAction(MusicForegroundService.Contracts.NEXT);
    ensureSurviceReady();
    mContext.startService(mService);
  }

  public void playPrev() {
    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_PREV_BUTTON);
    Intent mService = new Intent(mContext, MusicService.class);
    mService.setAction(MusicForegroundService.Contracts.PREV);
    ensureSurviceReady();
    mContext.startService(mService);
  }

  public void stopPlay() {
    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_PREV_BUTTON);
    Intent mService = new Intent(mContext, MusicService.class);
    mService.setAction(MusicForegroundService.Contracts.QUIT);
    ensureSurviceReady();
    mContext.startService(mService);
  }

  private boolean isServiceReady = false;

  private void ensureSurviceReady() {
    if (isServiceReady) {
      return;
    }
    isServiceReady = true;
  }

  public void playById(String id) {
    if (mChannelManager.getIndexForId(id) != null) {
      play(mChannelManager.getIndexForId(id));
      mChannelManager.markRecentPlayed(id);
    } else {
      DLog.d("Not able to play as not found");
    }
  }

  public void play(int position) {
    ensureSurviceReady();
    Intent mService = new Intent(mContext, MusicService.class);
    mService.putExtra("index", position);
    mService.setAction(MusicForegroundService.Contracts.PLAY_RANDOM);
    mContext.startService(mService);
  }

  public void playPause() {
    Intent mService = new Intent(mContext, MusicService.class);
    mService.setAction(MusicForegroundService.Contracts.PLAY_OR_PAUSE);
    ensureSurviceReady();
    mContext.startService(mService);
  }

  public void setPlayList(List<Channel> list1) {
    mFullChannelList = list1;
    Intent mService = new Intent(mContext, MusicService.class);
    List<Item> list = new ArrayList<Item>();
    for (Channel channel : mFullChannelList) {
      list.add(new Item(channel.getId(), channel.getName(), channel.getUrl()));
    }
    ensureSurviceReady();
    mService.putExtra("list", (Serializable) list);
    mService.setAction(MusicForegroundService.Contracts.LOAD);
    mContext.startService(mService);
  }
}
