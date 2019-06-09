package in.peerreview.fmradioindia.ui.mainactivity;

import in.co.dipankar.quickandorid.arch.BaseViewState;

public class MainState extends BaseViewState {
  public enum Page {
    NONE,
    SPASH,
    HOME,
    SEARCH
  }

  private Page mPage = Page.NONE;

  protected MainState(Builder builder) {
    super(builder);
    mPage = builder.page;
  }

  public Page getPage() {
    return mPage;
  }

  public static class Builder extends BaseViewState.Builder<Builder> {
    private Page page = Page.NONE;

    public Builder() {}

    public Builder setPage(Page page) {
      this.page = page;
      return this;
    }

    public MainState build() {
      return new MainState(this);
    }
  }
}
