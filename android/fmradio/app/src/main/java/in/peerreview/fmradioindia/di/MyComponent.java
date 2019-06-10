package in.peerreview.fmradioindia.di;

import dagger.Component;
import in.peerreview.fmradioindia.applogic.AppTrackingUtils;
import in.peerreview.fmradioindia.applogic.ThreadUtils;
import in.peerreview.fmradioindia.services.LogReportService;
import in.peerreview.fmradioindia.ui.MyApplication;
import in.peerreview.fmradioindia.ui.home.HomePresenter;
import in.peerreview.fmradioindia.ui.mainactivity.MainPresenter;
import in.peerreview.fmradioindia.ui.player.PlayerPresenter;
import in.peerreview.fmradioindia.ui.pref.UserPrefPresenter;
import in.peerreview.fmradioindia.ui.search.SearchPresenter;
import in.peerreview.fmradioindia.ui.splash.SplashPresenter;
import javax.inject.Singleton;

@Singleton
@Component(modules = {ContextModule.class})
public interface MyComponent {
  void inject(PlayerPresenter presenter);

  void inject(MainPresenter presenter);

  void inject(HomePresenter presenter);

  void inject(SearchPresenter searchPresenter);

  void inject(SplashPresenter splashPresenter);

  void inject(UserPrefPresenter presenter);

  void inject(ThreadUtils threadUtils);

  void inject(MyApplication myApplication);

  void inject(LogReportService logReportService);

  void inject(AppTrackingUtils appTrackingUtils);
}
