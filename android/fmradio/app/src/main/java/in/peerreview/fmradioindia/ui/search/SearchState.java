package in.peerreview.fmradioindia.ui.search;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.ui.mainactivity.MainState;
import java.util.List;

public class SearchState extends BaseViewState {
  private List<Channel> mSearchChannel;
  private List<Channel> previousSerachList;
  private Boolean shouldShowClose;

  public List<Channel> getSearchChannel() {
    return mSearchChannel;
  }

  public List<Channel> getPreviousSerachList() {
    return previousSerachList;
  }

  public Boolean getShouldShowClose() {
    return shouldShowClose;
  }

  protected SearchState(Builder builder) {
    super(builder);
    mSearchChannel = builder.mSearchChannel;
    previousSerachList = builder.previousSerachList;
    shouldShowClose = builder.shouldShowClose;
  }

  public static class Builder extends BaseViewState.Builder<MainState.Builder> {
    private List<Channel> mSearchChannel = null;
    private List<Channel> previousSerachList = null;
    private Boolean shouldShowClose = null;

    public Builder() {}

    public Builder setSearchChannel(List<Channel> channel) {
      mSearchChannel = channel;
      return this;
    }

    public Builder setRecentSearch(List<Channel> channel) {
      this.previousSerachList = channel;
      return this;
    }

    public Builder setShouldShowClose(boolean shouldShowClose) {
      this.shouldShowClose = shouldShowClose;
      return this;
    }

    public SearchState build() {
      return new SearchState(this);
    }
  }
}
