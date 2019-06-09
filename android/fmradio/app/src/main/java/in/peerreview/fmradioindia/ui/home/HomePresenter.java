package in.peerreview.fmradioindia.ui.home;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.applogic.ThreadUtils;
import in.peerreview.fmradioindia.ui.MyApplication;
import javax.inject.Inject;

public class HomePresenter extends BasePresenter {
  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;

  @Inject ThreadUtils mThreadUtils;

  public HomePresenter() {
    super("MainPresenter");
    MyApplication.getMyComponent().inject(this);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {
            render(
                new HomeState.Builder()
                    .setSuggestionList(mChannelManager.getSuggestedList())
                        .setRecentList(mChannelManager.getRecentPlayed())
                    .build());
          }


          @Override
          public void onCatListRefreshed() {
            render(new HomeState.Builder().setCategoriesList(mChannelManager.getCatMap()).build());
          }

          @Override
          public void onChangeRecentSerachList() {}

          @Override
          public void onChangeFebList() {}

          @Override
          public void onChangeRecentPlayList() {
            render(
                new HomeState.Builder().setRecentList(mChannelManager.getRecentPlayed()).build());
          }

            @Override
            public void onPrefUpdated() {

            }
        });
  }

  @Override
  protected void onViewAttached() {
    super.onViewAttached();
  }

  @Override
  protected void onViewDettached() {
    super.onViewDettached();
  }

  public void onItemClick(String id) {
    mThreadUtils.execute(
        new Runnable() {
          @Override
          public void run() {
            mMusicManager.playById(id);
          }
        });
  }
}
