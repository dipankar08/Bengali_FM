package in.peerreview.fmradioindia.ui.home;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import java.util.List;

public class HomeState extends BaseViewState {
  List<Category> mCategoriesMap = null;
  List<Channel> mSuggested = null;
  List<Channel> mRecent = null;

  protected HomeState(Builder builder) {
    super(builder);
    mCategoriesMap = builder.mCategoriesMap;
    mSuggested = builder.mSuggested;
    mRecent = builder.mRecent;
  }

  public List<Category> getCategoriesMap() {
    return mCategoriesMap;
  }

  public List<Channel> getSuggestionList() {
    return mSuggested;
  }

  public List<Channel> getRecentList() {
    return mRecent;
  }

  public static class Builder extends BaseViewState.Builder<Builder> {
    List<Category> mCategoriesMap = null;
    List<Channel> mSuggested = null;
    List<Channel> mRecent = null;

    public Builder() {}

    public Builder setCategoriesList(List<Category> mCategoriesMap) {
      this.mCategoriesMap = mCategoriesMap;
      return this;
    }

    public Builder setSuggestionList(List<Channel> channelList) {
      mSuggested = channelList;
      return this;
    }

    public Builder setRecentList(List<Channel> channelList) {
      mRecent = channelList;
      return this;
    }

    public HomeState build() {
      return new HomeState(this);
    }
  }
}
