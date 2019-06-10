package in.peerreview.fmradioindia.ui.search;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.applogic.ThreadUtils;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.ui.MyApplication;
import java.util.List;
import javax.inject.Inject;

public class SearchPresenter extends BasePresenter {
  @Inject ChannelManager mChannelManager;
  @Inject MusicManager mMusicManager;

  @Inject ThreadUtils mThreadUtils;

  public SearchPresenter(String name) {
    super(name);
    MyApplication.getMyComponent().inject(this);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {
            render(
                new SearchState.Builder()
                    .setRecentSearch(mChannelManager.getRecentSearch())
                    .build());
          }

          @Override
          public void onCatListRefreshed() {}

          @Override
          public void onChangeRecentSerachList() {}

          @Override
          public void onChangeFebList() {}

          @Override
          public void onChangeRecentPlayList() {
            render(
                new SearchState.Builder()
                    .setRecentSearch(mChannelManager.getRecentSearch())
                    .build());
          }

          @Override
          public void onPrefUpdated() {}
        });
  }

  public void onSearch(String s) {

    List<Channel> result = mChannelManager.applySearch(s);
    render(
        new SearchState.Builder()
            .setSearchChannel(result)
            .setShouldShowClose(s.length() > 0)
            .build());
  }

  public void onClickItem(String id) {
    mChannelManager.markRecentSearch(id);
    mMusicManager.playById(id);
  }

  public void onVisibilityChanged(int visibility) {
    render(
        new SearchState.Builder()
            .setRecentSearch(mChannelManager.getRecentSearch())
            .setSearchChannel(null)
            .build());
  }
}
