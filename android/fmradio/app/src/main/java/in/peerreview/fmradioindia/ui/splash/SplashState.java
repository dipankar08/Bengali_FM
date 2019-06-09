package in.peerreview.fmradioindia.ui.splash;

import in.co.dipankar.quickandorid.arch.BaseViewState;

public class SplashState extends BaseViewState {

  public enum Type {
    None,
    Boot,
    Ftux,
    Done,
  }

  Type type;

  protected SplashState(SplashState.Builder builder) {
    super(builder);
    type = builder.type;
  }

  public Type getType() {
    return type;
  }

  public static class Builder extends BaseViewState.Builder<SplashState.Builder> {
    Type type = Type.None;

    public Builder() {}

    public Builder setType(Type type) {
      this.type = type;
      return this;
    }

    public SplashState build() {
      return new SplashState(this);
    }
  }
}
