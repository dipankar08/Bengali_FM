package in.peerreview.fmradioindia.ui.pref;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.network.TelemetryManager;
import in.peerreview.fmradioindia.applogic.ThreadUtils;
import in.peerreview.fmradioindia.ui.MyApplication;
import in.peerreview.fmradioindia.ui.common.CommonUtils;

import java.util.Map;
import javax.inject.Inject;

public class UserPrefPresenter extends BasePresenter {
  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;

  @Inject ThreadUtils mThreadUtils;
  @Inject TelemetryManager mTelemetryManager;

  @Inject
  public UserPrefPresenter() {
    super("UserPrefPresenter");
    MyApplication.getMyComponent().inject(this);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {
            render(new UserPrefState.Builder().setConfig(mChannelManager.getUserPref()).build());
          }


          @Override
          public void onCatListRefreshed() {}

          @Override
          public void onChangeRecentSerachList() {}

          @Override
          public void onChangeFebList() {}

          @Override
          public void onChangeRecentPlayList() {}

            @Override
            public void onPrefUpdated() {
                render(new UserPrefState.Builder().setConfig(mChannelManager.getUserPref()).build());
            }
        });
  }

  public void onOptionChanged(Map<String, Boolean> opt) {
    mThreadUtils.execute(
        new Runnable() {
          @Override
          public void run() {
            mChannelManager.setFilterUserPref(opt);
          }
        });
      mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_BTN_OPTION);
  }

    public void onClickReport() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_BTN_REPORT);
        CommonUtils.sendFeedback(getContext());
    }

    public void onClickShare() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_BTN_SAHRE);
        CommonUtils.shareApp(getContext());
    }

    public void onClickFollow() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_BTN_FOLLOW);
        CommonUtils.followUs(getContext());
    }

    public void onClickRate() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_BTN_RATE);
        CommonUtils.openPlayStore(getContext());
    }

    public void onClickCredit() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_BTN_CREDIT);
        CommonUtils.showCredit(getContext());
    }
}
