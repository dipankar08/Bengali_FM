package in.peerreview.fmradioindia.ui.pref;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import java.util.LinkedHashMap;

public class UserPrefState extends BaseViewState {

  LinkedHashMap<String, Boolean> config;

  protected UserPrefState(Builder builder) {
    super(builder);
    config = builder.config;
  }

  public LinkedHashMap<String, Boolean> getConfig() {
    return config;
  }

  public static class Builder extends BaseViewState.Builder<Builder> {
    LinkedHashMap<String, Boolean> config;

    public Builder() {}

    public Builder setConfig(LinkedHashMap<String, Boolean> page) {
      this.config = page;
      return this;
    }

    public UserPrefState build() {
      return new UserPrefState(this);
    }
  }
}
