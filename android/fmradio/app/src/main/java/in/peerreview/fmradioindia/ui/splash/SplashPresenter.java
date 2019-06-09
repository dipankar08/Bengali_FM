package in.peerreview.fmradioindia.ui.splash;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.co.dipankar.quickandorid.arch.Error;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.applogic.PrefUtils;
import in.peerreview.fmradioindia.applogic.ThreadUtils;
import in.peerreview.fmradioindia.ui.MyApplication;
import javax.inject.Inject;

public class SplashPresenter extends BasePresenter {

  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;
  @Inject PrefUtils mPrefUtils;

  @Inject ThreadUtils mThreadUtils;

  public SplashPresenter(String name) {
    super(name);
    MyApplication.getMyComponent().inject(this);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {
            Error error = new Error.Builder().setTitle("Not able to load").setDesc(err).build();
            render(new SplashState.Builder().setError(error).build());
          }

          @Override
          public void onLoadSuccess() {
            render(new SplashState.Builder().setType(SplashState.Type.Done).setError(null).build());
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

            }
        });
    if (mPrefUtils.isFtux()) {
      onCompelteFTUX();
    } else {
      render(new SplashState.Builder().setType(SplashState.Type.Ftux).build());
    }
  }

  public void startFetch() {

    DLog.d("Running on background thread");
    mChannelManager.fetch();
  }

  public void onCompelteFTUX() {

    DLog.d("Running on background thread");
    mPrefUtils.setFtuxDone(true);
    render(new SplashState.Builder().setType(SplashState.Type.Boot).build());
    mChannelManager.fetch();
  }
}
